package android.adwait.com.profile.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.dashboard.view.ADDashboardActivity
import android.adwait.com.donation.model.ADDonationModel
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.utils.ADBaseFragment
import android.adwait.com.utils.ADConstants
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_my_profile.*

class ADMyProfileFragment : ADBaseFragment() {
    private val TAG: String = "ADMyProfileFragment"
    private lateinit var userData: ADUserDetails
    private var alreadyCreated: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_my_profile, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_logout.setOnClickListener(View.OnClickListener {
            (activity as ADDashboardActivity).menuAction(ADConstants.MENU_LOGOUT, "")
        })

        contact.setOnClickListener(View.OnClickListener {
            (activity as ADDashboardActivity).menuAction(ADConstants.MENU_CONTACT_US, "Contact")
        })

        my_mentee.setOnClickListener(View.OnClickListener {
            (activity as ADDashboardActivity).menuAction(ADConstants.MENU_MY_MENTEE, "Mentee")
        })

        my_contribution.setOnClickListener(View.OnClickListener {
            (activity as ADDashboardActivity).menuAction(
                ADConstants.MENU_MY_CONTRIBUTION,
                "Contribution"
            )
        })

        my_subscription.setOnClickListener {

        }

        reset_pwd.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, ADResetPassword::class.java)
            startActivity(intent)
        })

        edit_layout.setOnClickListener(View.OnClickListener {
            showDialog(true)
        })

        if (!alreadyCreated) {
            alreadyCreated = true
            fetchUserDetails()
        }
    }


    private fun fetchUserDetails() {
        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(getString(R.string.no_internet), null, true)
            return
        }

        progress_layout.visibility = View.VISIBLE
        (activity as ADBaseActivity).getUserDetails(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {
                Log.e(TAG, "User fetched")
                if (data.exists()) {
                    userData = data.getValue(ADUserDetails::class.java)!!
                    if (userData != null) {
                        user_welcome.setText("Hello, " + userData.userName)
                        p_name.setText(userData.userName)
                        p_email.setText(userData.emailAddress)
                        p_contribution.setText("")

                        if (userData.phoneNumber.isEmpty() && userData.date_of_birth.isEmpty()) {
                            progress_layout.visibility = View.GONE
                            showDialog(false)
                        }
                        fetchContribution()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error : " + error.message)
                progress_layout.visibility = View.GONE
            }
        })
    }

    private fun fetchContribution() {
        var monthYr =
            MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.month_yr))
                .toString()
        val userId =
            MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.userId))
                .toString()
        var myContribution = 0;
        FirebaseDatabase.getInstance()
            .getReference((activity as ADBaseActivity).CONTRIBUTION_TABLE_NAME).child(monthYr)
            .orderByChild("amount")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    progress_layout.visibility = View.GONE
                    if (dataSnapshot.exists()) {
                        for (data in dataSnapshot.children) {
                            Log.e(TAG, "onDataChange : " + data)
                            val donation = data.getValue(ADDonationModel::class.java)
                            if (donation?.userId.equals(userId)) {
                                val amt: Int = donation?.amount?.toInt()!!
                                myContribution = myContribution + amt
                            }
                        }
                        val text =
                            getString(R.string.rupees) + " " + myContribution.toString()
                        p_contribution?.setText(text)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "fetchContribution Error : " + error.message)
                    progress_layout.visibility = View.GONE
                }

            })
    }

    private fun showDialog(isEdit: Boolean) {
        val intent = Intent(activity, ADEditProfileActivity::class.java)
        intent.putExtra("isedit", isEdit)
        intent.putExtra("data", userData)
        startActivityForResult(intent, 444)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 444) {
            if (resultCode == Activity.RESULT_OK) {
                fetchUserDetails()
                (activity as ADDashboardActivity).fetchUserData()
            }
        }
    }

}