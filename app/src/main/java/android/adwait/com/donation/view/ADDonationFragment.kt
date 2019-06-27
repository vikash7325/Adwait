package android.adwait.com.donation.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.CommonUtils
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.dashboard.view.ADDashboardActivity
import android.adwait.com.donation.model.ADDonationModel
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.utils.ADBaseFragment
import android.adwait.com.utils.ADConstants
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.android.synthetic.main.fragment_donation.*
import org.json.JSONObject


class ADDonationFragment : ADBaseFragment(), PaymentResultWithDataListener {

    private val TAG = "ADDonationFragment"
    private var mPhoneNumber = ""
    private var mEmail = ""
    private var mUserId = ""
    private var mChildId = ""
    private var mUserName = ""
    private var mOldContribution=0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_donation, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Checkout.preload(activity!!.applicationContext)
        fetchUserData()

        done_btn.setOnClickListener(View.OnClickListener {
            (activity as ADDashboardActivity).menuAction(ADConstants.MENU_HOME,"")
        })

        donate_now.setOnClickListener(View.OnClickListener { startPayment() })
        hx_content.setText(CommonUtils.getHtmlText(getString(R.string.hx_content))) }

    private fun startPayment() {

        if (amount.text.toString().isEmpty()){
            (activity as ADBaseActivity).showMessage(getString(R.string.empty_amount),donation_parent,true)
            return
        }

        val checkout = Checkout()

        try {

            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("description", getString(R.string.description))

            val money = amount.text.toString().toInt() * 100

            if (money==0){
                (activity as ADBaseActivity).showMessage(getString(R.string.invalid_amount),donation_parent,true)
                return
            }
            options.put("currency", "INR")
            options.put("amount", money.toString())

            val preFill = JSONObject()
            preFill.put("email", mEmail)
            preFill.put("contact", mPhoneNumber)

            options.put("prefill", preFill)

            checkout.open(activity, options)

        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private fun fetchUserData() {

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(getString(R.string.no_internet), donation_parent, true)
            return
        }
        if ((activity as ADBaseActivity).isLoggedInUser()) {
            progress_layout?.visibility = View.VISIBLE

            (activity as ADBaseActivity).getUserDetails(object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.e(TAG, "Mentee fetched.")
                    if (data.exists()) {
                        val menteeDetails = data.getValue(ADUserDetails::class.java)!!
                        mPhoneNumber = menteeDetails.phoneNumber
                        mEmail = menteeDetails.emailAddress
                        mUserName = menteeDetails.userName

                        mUserId = MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.userId)).toString()
                        mPhoneNumber = CommonUtils.checkForEmpty(mPhoneNumber)
                        mEmail = CommonUtils.checkForEmpty(mEmail)
                        mUserName = CommonUtils.checkForEmpty(mUserName)

                        if (mUserName.isEmpty()){
                            mUserName = mEmail
                        }
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
        mChildId = childId
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
                                    .apply(RequestOptions().placeholder(R.drawable.ic_guest_user).diskCacheStrategy(
                                        DiskCacheStrategy.AUTOMATIC)).into(child_image)
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

                            if (monthYr.isEmpty() || monthYr.equals("null")) {
                                monthYr =
                                    (activity as ADBaseActivity).getServerDate("getCurrentMonthAndYr")
                            }

                            var collectedAmount =
                                data.child("contribution").child(monthYr).child("collected_amt")
                                    .value.toString()

                            if (collectedAmount.isEmpty() || collectedAmount.equals("null")) {
                                collectedAmount = "0"
                            }
                            val text = java.lang.String.format(getString(R.string.fund_raised_msg),
                                collectedAmount, monthlyAmount)
                            fund_details?.setText(text)

                            if (!collectedAmount.isEmpty() && collectedAmount != null && !collectedAmount.equals("null") &&
                                monthlyAmount != null && !monthlyAmount.equals("null") && monthlyAmount.toInt() > 0) {
                                val percent =
                                    ((collectedAmount.toInt()) * 100 / monthlyAmount.toInt())
                                progress.setProgress(percent)
                                mOldContribution = collectedAmount.toInt()
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

    override fun onPaymentSuccess(paymentId: String?, paymentData: PaymentData?) {
        progress_layout.visibility = View.VISIBLE
        saveData(paymentData,true)
    }

    override fun onPaymentError(error_id: Int, error_msg: String?, paymentData: PaymentData?) {
        if (error_id == Checkout.PAYMENT_CANCELED
            || error_id == Checkout.NETWORK_ERROR)
        {
            return
        }
        progress_layout.visibility = View.VISIBLE
        saveData(paymentData,false)
        (activity as ADBaseActivity).showMessage(error_msg.toString(), donation_parent, true)
    }

    private fun saveData(payment: PaymentData?,status:Boolean) {
        try {
            val preference = MySharedPreference(activity!!.applicationContext)
            val userId = preference.getValueString(getString(R.string.userId)).toString()

            var monthYr =
                MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.month_yr)).toString()

            if (monthYr.isEmpty() || monthYr.equals("null")) {
                monthYr =
                    (activity as ADBaseActivity).getServerDate("getCurrentMonthAndYr")
            }

            var today =
                MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.current_date)).toString()

            if (today.isEmpty() || today.equals("null")) {
                today =
                    (activity as ADBaseActivity).getServerDate("getCurrentDate")
            }
            val donation = ADDonationModel(payment?.paymentId.toString(), "", today, amount.text.toString(), status, mChildId, mUserName,userId)
            var mFirebaseDatabase = FirebaseDatabase.getInstance().getReference((activity as ADBaseActivity).USER_TABLE_NAME)

            val key = mFirebaseDatabase.push().key.toString()

            mFirebaseDatabase.child(mUserId)
                .child("contribution")
                .child(monthYr).child(key).setValue(donation)
                .addOnSuccessListener {

                    mFirebaseDatabase = FirebaseDatabase.getInstance().getReference((activity as ADBaseActivity).CHILD_TABLE_NAME)
                    val key2 = mFirebaseDatabase.push().key.toString()
                    mFirebaseDatabase.child(mChildId).child("contribution")
                        .child(monthYr).child(key2).setValue(donation)
                        .addOnSuccessListener {
                            if(status) {
                                mOldContribution = mOldContribution + amount.text.toString().toInt()
                                mFirebaseDatabase.child(mChildId).child("contribution")
                                    .child(monthYr).child("collected_amt")
                                    .setValue(mOldContribution)
                            }
                            payment_layout.visibility = View.GONE
                            congrats_layout.visibility = View.VISIBLE
                            progress_layout.visibility = View.GONE
                        }
                        .addOnFailureListener {
                            progress_layout.visibility = View.GONE
                        }

                }
                .addOnFailureListener {
                    progress_layout.visibility = View.GONE
                }
        }catch(e:Exception){
            Log.i("Tag",e.printStackTrace().toString())
            progress_layout.visibility = View.GONE
        }
    }


}