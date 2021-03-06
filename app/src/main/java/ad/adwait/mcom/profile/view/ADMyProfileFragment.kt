package ad.adwait.mcom.profile.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import ad.adwait.mcom.R
import ad.adwait.mcom.dashboard.view.ADDashboardActivity
import ad.adwait.mcom.donation.model.ADDonationModel
import ad.adwait.mcom.registeration.model.ADUserDetails
import ad.adwait.mcom.subscription.view.ADSubscriptionActivity
import ad.adwait.mcom.utils.ADBaseFragment
import ad.adwait.mcom.utils.ADViewClickListener
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_my_profile.*

class ADMyProfileFragment : ADBaseFragment() {
    private val TAG: String = "ADMyProfileFragment"
    private lateinit var userData: ADUserDetails
    private lateinit var mProgressDialog: AlertDialog

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

        profile_logout.setOnClickListener {

            (activity as ADBaseActivity).showAlertDialog(getString(R.string.logout_message),getString(R.string.yes),getString(R.string.cancel),
                ADViewClickListener {
                    (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_LOGOUT, "")
                })
        }

        contact.setOnClickListener {
            (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_CONTACT_US, "Contact")
        }

        my_mentee.setOnClickListener {
            (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_MY_MENTEE, "Mentee")
        }

        terms_condition.setOnClickListener {
            val intent = Intent(activity, ADTermsActivity::class.java)
            startActivity(intent)
        }

        my_contribution.setOnClickListener {
            (activity as ADDashboardActivity).menuAction(
                ad.adwait.mcom.utils.ADConstants.MENU_MY_CONTRIBUTION,
                "Contribution"
            )
        }

        my_subscription.setOnClickListener {
            val intent = Intent(activity, ADSubscriptionActivity::class.java)
            startActivityForResult(intent, 323)
        }

        reset_pwd.setOnClickListener {
            val intent = Intent(activity, ADResetPassword::class.java)
            startActivityForResult(intent, 444)
        }

        edit_layout.setOnClickListener {
            showDialog(true)
        }
        mProgressDialog = (activity as ADBaseActivity).showProgressDialog("", false)

        fetchUserDetails()

    }


    private fun fetchUserDetails() {
        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(getString(R.string.no_internet), null, true)
            return
        }
        mProgressDialog.show()

        (activity as ADBaseActivity).getUserDetails(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {
                Log.e(TAG, "User fetched")
                if (data.exists()) {
                    userData = data.getValue(ADUserDetails::class.java)!!
                    if (userData != null) {
                        user_welcome.setText("Hello, " + userData.userName)
                        p_name.setText(userData.userName)
                        p_email.setText(userData.emailAddress)
                        p_contribution.setText("-")

                        if (userData.phoneNumber.isEmpty() && userData.date_of_birth.isEmpty()) {
                            (activity as ADBaseActivity).hideProgress(mProgressDialog)
                            showDialog(false)
                        }
                        fetchContribution()
                    } else {
                        (activity as ADBaseActivity).hideProgress(mProgressDialog)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error : " + error.message)
                (activity as ADBaseActivity).hideProgress(mProgressDialog)
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
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
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
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
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
                (activity as ADDashboardActivity).fetchUserData(true)
            }
        } else if (requestCode == 323) {
            if (resultCode == Activity.RESULT_OK) {
                (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_DONATION, "")
            }
        }
    }

}