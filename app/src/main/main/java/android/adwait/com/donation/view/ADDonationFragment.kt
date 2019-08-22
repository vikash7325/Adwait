package android.adwait.com.donation.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.CommonUtils
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.dashboard.view.ADDashboardActivity
import android.adwait.com.donation.model.ADDonationModel
import android.adwait.com.my_mentee.view.ADMonthlySplit
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.utils.ADBaseFragment
import android.adwait.com.utils.ADCommonResponseListener
import android.adwait.com.utils.ADConstants
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.android.synthetic.main.fragment_donation.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import org.json.JSONObject


class ADDonationFragment : ADBaseFragment(), PaymentResultWithDataListener {

    private val TAG = "ADDonationFragment"
    private var mPhoneNumber = ""
    private var mEmail = ""
    private var mUserId = ""
    private var mChildId = ""
    private var mUserName = ""
    private var mOldContribution = 0
    private var monthlyAmount = "0";
    private var splitData:String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_donation, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Checkout.preload(activity!!.applicationContext)
        fetchUserData()

        done_btn.setOnClickListener(View.OnClickListener {

            if (activity is ADDashboardActivity) {
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_HOME, "")
            } else {
                (activity as ADDonationActivity).finish()
            }
        })

        donate_now.setOnClickListener(View.OnClickListener { startPayment() })
        hx_content.setText(CommonUtils.getHtmlText(getString(R.string.hx_content)))

        if (arguments != null) {
            var bundle = arguments
            val price: String = bundle?.getInt("amount", 0).toString()
            amount.setText(price)
        }

        info_icon.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, ADMonthlySplit::class.java)
            intent.putExtra("data", splitData)
            startActivity(intent)
        })

        btn_monthly.setOnClickListener(View.OnClickListener {
            amount.setText(monthlyAmount)
        })

        btn_100.setOnClickListener(View.OnClickListener {
            amount.setText("100")
        })
        btn_200.setOnClickListener(View.OnClickListener {
            amount.setText("200")
        })
        btn_500.setOnClickListener(View.OnClickListener {
            amount.setText("500")
        })
        btn_1000.setOnClickListener(View.OnClickListener {
            amount.setText("1000")
        })
    }

    private fun startPayment() {

        if (amount.text.toString().isEmpty()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.empty_amount),
                donation_parent,
                true
            )
            return
        }

        val checkout = Checkout()

        try {

            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("description", getString(R.string.description))

            val money = amount.text.toString().toInt() * 100

            if (money == 0) {
                (activity as ADBaseActivity).showMessage(
                    getString(R.string.invalid_amount),
                    donation_parent,
                    true
                )
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
                        mPhoneNumber = menteeDetails.phoneNumber
                        mEmail = menteeDetails.emailAddress
                        mUserName = menteeDetails.userName

                        mUserId = MySharedPreference(activity as ADBaseActivity).getValueString(
                            getString(R.string.userId)
                        ).toString()
                        mPhoneNumber = CommonUtils.checkForEmpty(mPhoneNumber)
                        mEmail = CommonUtils.checkForEmpty(mEmail)
                        mUserName = CommonUtils.checkForEmpty(mUserName)

                        if (mUserName.isEmpty()) {
                            mUserName = mEmail
                        }
                        fetchChildData(menteeDetails.childId, false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Mentee fetch Error : " + error.message)
                    progress_layout?.visibility = View.GONE
                }
            })
        }
    }

    private fun fetchChildData(childId: String, amtCollected: Boolean) {
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
                                    .placeholder(R.drawable.ic_guest_user).diskCacheStrategy(
                                        DiskCacheStrategy.SOURCE
                                    ).into(child_image)
                            }

                            val age: String = (activity as ADBaseActivity).getAge(
                                data.child("date_of_birth").value.toString(),
                                "dd-MMM-yyyy"
                            ).toString()
                            child_age?.setText(age + " Years")


                            monthlyAmount = data.child("amount_needed").value.toString()
                            var monthYr =
                                MySharedPreference(activity as ADBaseActivity).getValueString(
                                    getString(R.string.month_yr)
                                ).toString()

                            if (amtCollected) {
                                monthYr =
                                    MySharedPreference(activity as ADBaseActivity).getValueString(
                                        getString(R.string.next_month_yr)
                                    ).toString()
                            }

                            var collectedAmount =
                                data.child("contribution").child(monthYr).child("collected_amt")
                                    .value.toString()

                            if (collectedAmount.isEmpty() || collectedAmount.equals("null")) {
                                collectedAmount = "0"
                            }

                            if (monthlyAmount.toInt() > 0 && monthlyAmount.toInt() == collectedAmount.toInt()) {
                                fetchChildData(childId, true)
                                return
                            }

                            splitData = data.child("split_up").value.toString()


                            fetchContribution(mUserId, monthYr, childId)

                            btn_monthly.setText(getString(R.string.rupees) + " " + monthlyAmount)
                            val text = java.lang.String.format(
                                getString(R.string.fund_raised_msg),
                                collectedAmount, monthlyAmount, monthYr
                            )
                            fund_details?.setText(text)

                            val hint = java.lang.String.format(
                                getString(R.string.hint_with_name),
                                data.child("name").value.toString()
                            )
                            hint_with_name.setText(hint)

                            if (!collectedAmount.isEmpty() && collectedAmount != null && !collectedAmount.equals(
                                    "null"
                                ) &&
                                monthlyAmount != null && !monthlyAmount.equals("null") && monthlyAmount.toInt() > 0
                            ) {
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

    private fun fetchContribution(userId: String, monthYr: String, childId: String) {

        var myContribution = 0;
        if (contributers_list.childCount > 0) {
            contributers_list.removeAllViews()
        }

        val topList = ArrayList<ADDonationModel>()
        FirebaseDatabase.getInstance()
            .getReference((activity as ADBaseActivity).CONTRIBUTION_TABLE_NAME).child(monthYr)
            .orderByChild("amount")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (data in dataSnapshot.children) {
                            Log.e(TAG, "onDataChange : " + data)
                            val donation = data.getValue(ADDonationModel::class.java)
                            if (donation?.userId.equals(userId)) {
                                val amt: Int = donation?.amount?.toInt()!!
                                myContribution = myContribution + amt
                            }
                            if (donation != null && donation.childId.equals(childId)) {
                                topList.add(donation)
                            }
                        }
                        val text = java.lang.String.format(
                            getString(R.string.your_contribution), myContribution.toString()
                        )
                        my_contribution?.setText(text)

                        var count = 1
                        val size = topList.size
                        var looper = size - 1

                        if (size > 3) {
                            size == 3
                        }

                        if (size == 0) {
                            top_contribution.visibility = View.GONE
                            no_contribution.visibility = View.VISIBLE
                            val text = java.lang.String.format(
                                getString(R.string.no_contribution), child_name.text.toString()
                            )
                            no_contribution.setText(text)
                        } else {
                            while (count <= size) {
                                val donation = topList.get(looper)
                                val view =
                                    View.inflate(
                                        activity, R.layout.contributers_topper_list_item, null)
                                val name = view.findViewById<TextView>(R.id.contribution_data)

                                name.setText(
                                    (contributers_list.childCount + 1).toString() + ". " + donation?.userName + " "
                                            + getString(R.string.rupees) + donation?.amount.toString()
                                )
                                contributers_list.addView(view)
                                count++
                                looper--
                            }
                        }
                    } else {
                        top_contribution.visibility = View.GONE
                        no_contribution.visibility = View.VISIBLE
                        val text = java.lang.String.format(
                            getString(R.string.no_contribution), child_name.text.toString()
                        )
                        no_contribution.setText(text)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "fetchContribution Error : " + error.message)
                }

            })
    }

    override fun onPaymentSuccess(paymentId: String?, paymentData: PaymentData?) {
        progress_layout.visibility = View.VISIBLE
        checkAmountOfChild(paymentData, true, amount.text.toString().toInt())
    }

    override fun onPaymentError(error_id: Int, error_msg: String?, paymentData: PaymentData?) {
        if (error_id == Checkout.PAYMENT_CANCELED
            || error_id == Checkout.NETWORK_ERROR
        ) {
            return
        }
        progress_layout.visibility = View.VISIBLE
        checkAmountOfChild(paymentData, false,amount.text.toString().toInt())
        (activity as ADBaseActivity).showMessage(error_msg.toString(), donation_parent, true)
    }

    private fun checkAmountOfChild(payment: PaymentData?, status: Boolean,amount: Int) {
        try {
            val preference = MySharedPreference(activity!!.applicationContext)
            val userId = preference.getValueString(getString(R.string.userId)).toString()

            var monthYr =
                MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.month_yr))
                    .toString()

            var today =
                MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.current_date))
                    .toString()
            var donation = ADDonationModel(
                payment?.paymentId.toString(), "", today, amount,
                status, mChildId, mUserName, userId
            )

            var tempAmount = mOldContribution + amount

            if ((mOldContribution + amount) > monthlyAmount.toInt()) {
                var balance = monthlyAmount.toInt() - mOldContribution
                tempAmount = mOldContribution + balance
                donation = ADDonationModel(
                    payment?.paymentId.toString(),
                    "",
                    today,
                    balance,
                    status,
                    mChildId,
                    mUserName,
                    userId
                )
                savePaymentToServer(monthYr, donation, tempAmount, status, false)
                MySharedPreference(activity as ADBaseActivity).saveStrings(
                    getString(R.string.previous_month_yr),
                    monthYr
                )

                (activity as ADBaseActivity).getNextDate("getNextMonthAndYr", monthYr,
                    object : ADCommonResponseListener {
                        override fun onSuccess(data: Any?) {
                            MySharedPreference(activity as ADBaseActivity).saveStrings(
                                getString(R.string.month_yr), data.toString()
                            )
                            monthYr = data.toString()

                            balance =
                                (mOldContribution + amount) - monthlyAmount.toInt()
                            mOldContribution = 0
                           checkAmountOfChild(payment,status,balance)
                        }

                        override fun onError(data: Any?) {
                            progress_layout.visibility = View.GONE
                        }
                    })
            } else {

                savePaymentToServer(monthYr, donation, tempAmount, status, true)
            }

        } catch (e: Exception) {
            Log.i("Tag", e.printStackTrace().toString())
            progress_layout.visibility = View.GONE
        }
    }

    private fun savePaymentToServer(
        monthYr: String,
        donation: ADDonationModel,
        amount: Int,
        status: Boolean,
        showCele: Boolean
    ) {
        var mFirebaseDatabase = FirebaseDatabase.getInstance()
            .getReference((activity as ADBaseActivity).CONTRIBUTION_TABLE_NAME)

        val key = mFirebaseDatabase.push().key.toString()

        mFirebaseDatabase
            .child(monthYr).child(key).setValue(donation)
            .addOnSuccessListener {

                mFirebaseDatabase = FirebaseDatabase.getInstance()
                    .getReference((activity as ADBaseActivity).CHILD_TABLE_NAME)
                val key2 = mFirebaseDatabase.push().key.toString()
                mFirebaseDatabase.child(mChildId).child("contribution")
                    .child(monthYr).child(key2).setValue(donation)
                    .addOnSuccessListener {
                        if (status) {
                            mFirebaseDatabase.child(mChildId).child("contribution")
                                .child(monthYr).child("collected_amt")
                                .setValue(amount)
                        }
                        if (showCele) {
                            payment_layout.visibility = View.GONE
                            congrats_layout.visibility = View.VISIBLE
                            progress_layout.visibility = View.GONE

                            celebration_view.visibility = View.VISIBLE
                            celebration_view.build()
                                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                                .setDirection(0.0, 359.0)
                                .setSpeed(1f, 5f)
                                .setFadeOutEnabled(true)
                                .setTimeToLive(ADConstants.ANIMATION_TIME_TO_LIVE)
                                .addShapes(Shape.RECT, Shape.CIRCLE)
                                .addSizes(Size(ADConstants.ANIMATION_SIZE))
                                .setPosition(-50f, celebration_view.width + 50f, -50f, -50f)
                                .streamFor(
                                    ADConstants.ANIMATION_COUNT,
                                    ADConstants.ANIMATION_EMITTING_TIME
                                )
                        }
                    }
                    .addOnFailureListener {
                        progress_layout.visibility = View.GONE
                    }

            }
            .addOnFailureListener {
                progress_layout.visibility = View.GONE
            }
    }


}