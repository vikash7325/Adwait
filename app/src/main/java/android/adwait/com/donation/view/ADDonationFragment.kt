package android.adwait.com.donation.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.utils.ADBaseFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_donation.*

class ADDonationFragment : ADBaseFragment() {

    private val TAG = "ADDonationFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_donation, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserData()
    }


    private fun fetchUserData() {

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.no_internet),
                donation_parent,
                true
            )
            return
        }
        if ((activity as ADBaseActivity).isLoggedInUser()) {
            progress_layout?.visibility = View.VISIBLE

            (activity as ADBaseActivity).getUserDetails(object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.e(TAG, "Mentee fetched.")
                    if (data.exists()) {
                        val menteeDetails = data.getValue(ADUserDetails::class.java)!!
                        fetchChildData(menteeDetails.childId)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Mentee fetch Error : " + error.message)
                    progress_layout?.visibility = View.GONE
                }
            })
        }
    }

    private fun fetchChildData(childId: String) {

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.no_internet),
                donation_parent,
                true
            )
            return
        }
        if (childId.isEmpty()) {
            progress_layout.visibility = View.GONE
            return
        }
        if ((activity as ADBaseActivity).isLoggedInUser()) {

            (activity as ADBaseActivity).getChildDetails(childId, object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.e(TAG, "Child Fetched.")
                    progress_layout?.visibility = View.GONE
                    if (data.exists()) {
                        if (data != null) {

                            child_name?.setText(data.child("name").value.toString())
                            val imageUrl = data.child("child_image").value.toString()
                            if (!imageUrl.isEmpty()) {
                                Glide.with(activity as ADBaseActivity).load(imageUrl)
                                    .error(R.drawable.ic_guest_user).into(child_image)
                            }

                            val age: String = (activity as ADBaseActivity).getAge(
                                data.child("date_of_birth").value.toString(),
                                "dd-MMM-yyyy"
                            ).toString()
                            child_age?.setText(age + " Years")

                            val monthlyAmount = data.child("amount_needed").value.toString()
                            var monthYr =
                                MySharedPreference(activity as ADBaseActivity).getValueString(
                                    getString(R.string.month_yr)
                                ).toString()

                            if (monthYr.isEmpty()) {
                                monthYr =
                                    (activity as ADBaseActivity).getServerDate("getCurrentMonthAndYr")
                            }

                            val collectedAmount =
                                data.child("contribution").child(monthYr).child("collected_amt")
                                    .value.toString()

                            val text = java.lang.String.format(
                                getString(R.string.fund_raised_msg),
                                collectedAmount,
                                monthlyAmount
                            )
                            fund_details?.setText(text)

                            if (!collectedAmount.isEmpty() && collectedAmount != null && !collectedAmount.equals(
                                    "null"
                                ) &&
                                monthlyAmount != null && !monthlyAmount.equals("null") && monthlyAmount.toInt() > 0
                            ) {
                                val percent =
                                    ((collectedAmount.toInt()) * 100 / monthlyAmount.toInt())
                                progress.setProgress(percent)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error : " + error.message)
                    progress_layout?.visibility = View.GONE
                }
            })
        }
    }
}