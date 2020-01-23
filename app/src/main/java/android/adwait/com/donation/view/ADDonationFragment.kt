package android.adwait.com.donation.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.CommonUtils
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.admin.model.ADAddChildModel
import android.adwait.com.admin.model.ADMoneySplitUp
import android.adwait.com.admin.model.RestError
import android.adwait.com.dashboard.view.ADDashboardActivity
import android.adwait.com.donation.model.*
import android.adwait.com.my_mentee.view.ADMonthlySplitActivity
import android.adwait.com.registeration.model.ADLastCheckout
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.rest_api.ApiClient
import android.adwait.com.rest_api.ApiInterface
import android.adwait.com.subscription.model.ADSubscriptionRequest
import android.adwait.com.subscription.model.ADSubscriptionResponse
import android.adwait.com.subscription.view.ADSubscriptionActivity
import android.adwait.com.utils.ADBaseFragment
import android.adwait.com.utils.ADCommonResponseListener
import android.adwait.com.utils.ADConstants
import android.adwait.com.utils.ADViewClickListener
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.android.synthetic.main.fragment_donation.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ADDonationFragment : ADBaseFragment(), PaymentResultWithDataListener {

    private val TAG = "ADDonationFragment"
    private var mPhoneNumber = ""
    private var mEmail = ""
    private var mUserId = ""
    private var mChildId = ""
    private var mUserName = ""
    private var mOldContribution = 0
    private var monthlyAmount = "0";
    private var splitData: ADMoneySplitUp = ADMoneySplitUp()
    private var receiptNo = "Receipt_"
    private var mSubId = ""
    private lateinit var mProgressDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_donation, container, false)

        mProgressDialog = (activity as ADBaseActivity).showProgressDialog("", false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Checkout.preload(activity!!.applicationContext)
        fetchUserData()

        done_btn.setOnClickListener(View.OnClickListener {

            if (activity is ADDashboardActivity) {
                (activity as ADDashboardActivity).fetchUserData(true)
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_HOME, "")
            } else {
                (activity as ADDonationActivity).finish()
            }
        })

        start.setOnClickListener {
            monthly_subscription.isChecked = true
            donate_now.performClick()
        }

        repeat.setOnClickListener {
            amount.setText(repeat_amount.text.toString().replace(getString(R.string.rupees),""))
            donate_now.performClick()
        }

        donate_now.setOnClickListener(View.OnClickListener {
            if (mPhoneNumber == null || mPhoneNumber.length == 0) {
                (activity as ADBaseActivity).showAlertDialog(
                    getString(R.string.no_phone_num),
                    getString(R.string.update_now),
                    getString(R.string.cancel),
                    ADViewClickListener {
                        (activity as ADDashboardActivity).menuAction(ADConstants.MENU_PROFILE, "")
                    })
            } else {
                createOrderOrSub()
            }
        })
        hx_content.setText(CommonUtils.getHtmlText(getString(R.string.hx_content)))

        if (arguments != null) {
            var bundle = arguments
            val price: String = bundle?.getInt("amount", 0).toString()
            amount.setText(price)
        }

        info_icon.setOnClickListener(View.OnClickListener {
            if (splitData.toString().length == 0) {
            } else {
                val intent = Intent(activity, ADMonthlySplitActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("splitData", splitData)
                intent.putExtra("data", bundle)
                startActivity(intent)
            }
        })

        activeSubscription.setOnClickListener {
            val intent = Intent(activity, ADSubscriptionActivity::class.java)
            startActivity(intent)
        }

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

        view.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if (event?.action == MotionEvent.ACTION_UP) {
                    val imm =
                        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
                }
                return true
            }

        })


        val spannable = SpannableString(getString(R.string.subscription_details))
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorPrimary)),
            32,
            getString(R.string.subscription_details).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        activeSubscription.setText(spannable, TextView.BufferType.SPANNABLE)
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
            mProgressDialog.show()

            (activity as ADBaseActivity).getUserDetails(object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.e(TAG, "Mentee fetched.")
                    if (data.exists()) {
                        val menteeDetails = data.getValue(ADUserDetails::class.java)!!
                        mPhoneNumber = menteeDetails.phoneNumber
                        mEmail = menteeDetails.emailAddress
                        mUserName = menteeDetails.userName

                        mUserId =
                            MySharedPreference(activity as ADBaseActivity).getValueString(
                                getString(
                                    R.string.userId
                                )
                            )
                                .toString()
                        mPhoneNumber = CommonUtils.checkForEmpty(mPhoneNumber)
                        mEmail = CommonUtils.checkForEmpty(mEmail)
                        mUserName = CommonUtils.checkForEmpty(mUserName)

                        if (mUserName.isEmpty()) {
                            mUserName = mEmail
                        }

                        if (menteeDetails.lastTransaction != null && menteeDetails.lastTransaction.amount > 0) {
                            repeat_amount.setText(getString(R.string.rupees) + menteeDetails.lastTransaction.amount)
                            payment_method.setText("Using " + menteeDetails.lastTransaction.method)
                            repeat_layout.visibility = View.VISIBLE
                        } else {
                            repeat_layout.visibility = View.GONE
                        }
                        fetchChildData(menteeDetails.childId, false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Mentee fetch Error : " + error.message)
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
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
            (activity as ADBaseActivity).hideProgress(mProgressDialog)
            return
        }
        if ((activity as ADBaseActivity).isLoggedInUser()) {

            (activity as ADBaseActivity).getChildDetails(childId, object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.e(TAG, "Child Fetched.")
                    getSubscription()
                    if (data.exists()) {
                        if (data != null) {

                            val childData = data.getValue(ADAddChildModel::class.java)!!
                            child_name?.setText(childData.childName)
                            val imageUrl = childData.childImage
                            if (!imageUrl.isEmpty()) {
                                Glide.with(activity as ADBaseActivity).load(imageUrl)
                                    .placeholder(R.drawable.ic_guest_user)
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(child_image)
                            }

                            val age: String = (activity as ADBaseActivity).getAge(
                                childData.dateOfBirth,
                                "dd-MM-yyyy"
                            ).toString()
                            child_age?.setText(age + " Years")


                            monthlyAmount = childData.amountNeeded.toString()
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

                            splitData = childData.splitDetails


                            fetchContribution(mUserId, monthYr, childId)

                            btn_monthly.setText(getString(R.string.rupees) + " " + monthlyAmount)
                            val text = java.lang.String.format(
                                getString(R.string.fund_raised_msg),
                                collectedAmount, monthlyAmount, monthYr
                            )
                            fund_details?.setText(text)

                            val hint = java.lang.String.format(
                                getString(R.string.hint_with_name),
                                childData.childName
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
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
                }
            })
        }
    }

    private fun createOrderOrSub() {
        if (amount.text.toString().isEmpty()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.empty_amount),
                donation_parent,
                true
            )
            return
        }
        val money = amount.text.toString().toInt() * 100

        if (money == 0) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.invalid_amount),
                donation_parent,
                true
            )
            return
        }
        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(getString(R.string.no_internet), null, true)
            return
        }
        mProgressDialog.show()


        if (monthly_subscription.isChecked) {
            createSubscription(money)
        } else {

            receiptNo = receiptNo + System.currentTimeMillis()

            if (receiptNo.length > 30) {
                receiptNo = receiptNo.substring(0, 25)
            }
            var orderRequest = ADCreateOrderRequest(receiptNo, money, "INR");
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.createOrder(orderRequest)

            Log.v("orderRequest=> ", orderRequest.toString())
            call.enqueue(object : Callback<ADCreateOrderResponse> {
                override fun onResponse(
                    call: Call<ADCreateOrderResponse>?,
                    response: Response<ADCreateOrderResponse>?
                ) {

                    if (response != null && response.isSuccessful) {
                        if (response.body().successFlag) {
                            if (response.body() != null) {
                                val fullResponse: ADCreateOrderResponse = response?.body()!!
                                startPayment(fullResponse.data, "")
                            }
                        } else {
                            (activity as ADBaseActivity).showMessage(
                                response.body().message,
                                donation_parent,
                                true
                            )
                            (activity as ADBaseActivity).hideProgress(mProgressDialog)
                        }
                    } else {
                        val error = Gson().fromJson(
                            response?.errorBody()?.charStream(),
                            RestError::class.java
                        )
                        Log.i("Testing ==> ", error.toString())
                        (activity as ADBaseActivity).showMessage(
                            error.error.description,
                            donation_parent,
                            true
                        )
                        (activity as ADBaseActivity).hideProgress(mProgressDialog)
                    }
                }

                override fun onFailure(call: Call<ADCreateOrderResponse>?, t: Throwable?) {
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
                    Log.i("Testing ==> ", t?.message.toString())
                }

            })
        }

    }

    private fun createSubscription(amount: Int) {
        var subRequest = ADSubscriptionRequest(
            amount,
            "",
            "",
            mUserId,
            mUserName,
            mChildId
        );
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.createSubscription(subRequest)
        Log.v(TAG, subRequest.toString())
        call.enqueue(object : Callback<ADSubscriptionResponse> {

            override fun onResponse(
                call: Call<ADSubscriptionResponse>?,
                response: Response<ADSubscriptionResponse>?
            ) {

                if (response != null && response.isSuccessful) {
                    if (response.body().isSuccessFlag) {
                        if (response.body() != null) {
                            val fullResponse: ADSubscriptionResponse = response?.body()!!
                            mSubId = fullResponse.data.id
                            startPayment(null, fullResponse.data.id)
                            MySharedPreference((activity as ADBaseActivity).applicationContext).saveStrings(
                                getString(R.string.subscription_id),
                                fullResponse.data.id
                            )
                        }
                    } else {
                        (activity as ADBaseActivity).showMessage(
                            response.body().message,
                            donation_parent,
                            true
                        )
                        (activity as ADBaseActivity).hideProgress(mProgressDialog)
                    }
                } else {
                    val error = Gson().fromJson(
                        response?.errorBody()?.charStream(),
                        RestError::class.java
                    )
                    Log.i("Testing ==> ", error.toString())
                    (activity as ADBaseActivity).showMessage(
                        error.error.description,
                        donation_parent,
                        true
                    )
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
                }
            }

            override fun onFailure(call: Call<ADSubscriptionResponse>?, t: Throwable?) {
                (activity as ADBaseActivity).hideProgress(mProgressDialog)
            }
        })
    }

    private fun getSubscription() {
        val subId =
            MySharedPreference((activity as ADBaseActivity).applicationContext).getValueString(
                getString(R.string.subscription_id)
            )

        if (subId == null || subId.length == 0) {
            monthly_subscription.visibility = View.VISIBLE
            subscribe_layout.visibility = View.VISIBLE
            activeSubscription.visibility = View.GONE
            (activity as ADBaseActivity).hideProgress(mProgressDialog)
            return
        }
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getSubscription(subId)

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(getString(R.string.no_internet), null, true)
            return
        }
        call.enqueue(object : Callback<ADSubscriptionResponse> {

            override fun onResponse(
                call: Call<ADSubscriptionResponse>?,
                response: Response<ADSubscriptionResponse>?
            ) {
                (activity as ADBaseActivity).hideProgress(mProgressDialog)
                if (response != null && response.isSuccessful) {
                    if (response.body() != null && response.body().isSuccessFlag) {
                        val fullResponse: ADSubscriptionResponse = response?.body()!!
                        if (fullResponse.data.status.toLowerCase().equals("active")) {
                            monthly_subscription.visibility = View.GONE
                            subscribe_layout.visibility = View.GONE
                            activeSubscription.visibility = View.VISIBLE
                        } else {
                            monthly_subscription.visibility = View.VISIBLE
                            subscribe_layout.visibility = View.VISIBLE
                            activeSubscription.visibility = View.GONE
                        }

                    }
                } else {
                    val error = Gson().fromJson(
                        response?.errorBody()?.charStream(),
                        RestError::class.java
                    )
                    Log.i("Testing ==> ", error.toString())
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
                }

            }

            override fun onFailure(call: Call<ADSubscriptionResponse>?, t: Throwable?) {
                (activity as ADBaseActivity).hideProgress(mProgressDialog)
            }
        })
    }

    private fun startPayment(data: ADCreateOrderData?, sub_id: String) {

        val checkout = Checkout()
        try {


            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("description", getString(R.string.description))


            options.put("currency", "INR")

            if (sub_id != null && sub_id.length == 0) {
                options.put("order_id", data?.id)
                options.put("amount", data?.amount.toString())
            } else {
                val money = amount.text.toString().toInt() * 100

                options.put("subscription_id", sub_id)
                options.put("amount", money.toString())
            }
            val preFill = JSONObject()
            preFill.put("email", mEmail)
            preFill.put("contact", mPhoneNumber)

            options.put("prefill", preFill)

            checkout.open(activity, options)

        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT)
                .show();
            (activity as ADBaseActivity).hideProgress(mProgressDialog)
            e.printStackTrace();
        }
    }

    private fun verifySignature(paymentData: PaymentData?) {
        try {

            var signRequest =
                ADSignVerifyRequest(paymentData!!.orderId, paymentData!!.signature);
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.verifySignature(signRequest)
            Log.i("Testing ==> ", "verifySignature called")
            Log.i("Testing ==> ", signRequest.toString())

            call.enqueue(object : Callback<ADSignVerifyResponse> {

                override fun onResponse(
                    call: Call<ADSignVerifyResponse>?,
                    response: Response<ADSignVerifyResponse>?
                ) {
                    if (response != null && response.isSuccessful) {
                        if (response.body().isSuccessFlag) {
                            if (response.body() != null) {
                                val fullData: ADSignVerifyResponse = response?.body()!!
                                if (fullData.message.equals("Signature Matched")) {
                                    fetchPaymentMethod(paymentData, true)
                                }
                            }
                        } else {
                            (activity as ADBaseActivity).showMessage(
                                response.body().message,
                                donation_parent,
                                true
                            )
                            (activity as ADBaseActivity).hideProgress(mProgressDialog)
                        }
                    } else {
                        val error = Gson().fromJson(
                            response?.errorBody()?.charStream(),
                            RestError::class.java
                        )
                        Log.i("Testing ==> ", error.toString())
                        (activity as ADBaseActivity).showMessage(
                            error.error.description,
                            donation_parent,
                            true
                        )
                        (activity as ADBaseActivity).hideProgress(mProgressDialog)
                    }
                }

                override fun onFailure(call: Call<ADSignVerifyResponse>?, t: Throwable?) {
                    fetchPaymentMethod(paymentData, false)
                    Log.i("Testing ==> onFailure", t.toString())
                }
            })
        } catch (e: Exception) {
            (activity as ADBaseActivity).hideProgress(mProgressDialog)
            Log.i("Testing ==> onException", e.toString())

        }
    }

    private fun fetchPaymentMethod(paymentData: PaymentData?, status: Boolean) {
        try {
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.getPaymentMethod(paymentData?.paymentId)

            call.enqueue(object : Callback<ADPaymentMethodResponse> {

                override fun onResponse(
                    call: Call<ADPaymentMethodResponse>?,
                    response: Response<ADPaymentMethodResponse>?
                ) {
                    if (response != null && response.isSuccessful) {
                        if (response.body().isSuccessFlag) {
                            if (response.body() != null) {
                                val fullData: ADPaymentMethodResponse = response?.body()!!
                                checkAmountOfChild(
                                    paymentData,
                                    status,
                                    fullData.data.method)
                            }
                        } else {
                            (activity as ADBaseActivity).showMessage(
                                response.body().message, donation_parent, true
                            )
                            (activity as ADBaseActivity).hideProgress(mProgressDialog)
                        }
                    } else {
                        val error = Gson().fromJson(
                            response?.errorBody()?.charStream(),
                            RestError::class.java
                        )
                        Log.i("Testing ==> ", error.toString())
                        (activity as ADBaseActivity).showMessage(
                            error.error.description, donation_parent,
                            true
                        )
                        (activity as ADBaseActivity).hideProgress(mProgressDialog)
                    }
                }

                override fun onFailure(
                    call: Call<ADPaymentMethodResponse>?,
                    t: Throwable?
                ) {
                    checkAmountOfChild(paymentData, status, "No Data")
                    Log.i("Testing ==> onFailure", t.toString())
                }
            })
        } catch (e: Exception) {
            (activity as ADBaseActivity).hideProgress(mProgressDialog)
            Log.i("Testing ==> onException", e.toString())

        }
    }


    private fun fetchContribution(
        userId: String,
        monthYr: String,
        childId: String
    ) {

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
                        var size = topList.size
                        var looper = size - 1

                        if (size > 5) {
                            size = 5
                        }

                        if (size == 0) {
                            top_contribution.visibility = View.GONE
                            no_contribution.visibility = View.VISIBLE
                            val text = java.lang.String.format(
                                getString(R.string.no_contribution),
                                child_name.text.toString()
                            )
                            no_contribution.setText(text)
                        } else {
                            while (count <= size) {
                                val donation = topList.get(looper)
                                val view =
                                    View.inflate(
                                        activity,
                                        R.layout.contributers_topper_list_item,
                                        null
                                    )
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
        mProgressDialog.show()
        if (monthly_subscription.isChecked) {
            showCelebration()
        } else {
            verifySignature(paymentData)
        }
    }

    override fun onPaymentError(
        error_id: Int,
        error_msg: String?,
        paymentData: PaymentData?
    ) {
        (activity as ADBaseActivity).hideProgress(mProgressDialog)
        Log.i("Testing ==> ", error_msg.toString())
        if (error_id == Checkout.PAYMENT_CANCELED
            || error_id == Checkout.NETWORK_ERROR
        ) {
            return
        }
        Checkout.clearUserData(activity)
        (activity as ADBaseActivity).showMessage(
            error_msg.toString(),
            donation_parent,
            true
        )
    }

    private fun checkAmountOfChild(
        payment: PaymentData?,
        status: Boolean,
        paymentMethod: String
    ) {
        try {
            updateUserLastTrans(paymentMethod)
            val preference = MySharedPreference(activity!!.applicationContext)
            val userId = preference.getValueString(getString(R.string.userId)).toString()

            var monthYr =
                MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.month_yr))
                    .toString()

            var today =
                MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.current_date))
                    .toString()

            var id = payment?.orderId.toString()

            if (id == null) {
                id = mSubId
            }
            var donation = ADDonationModel(
                id,
                receiptNo,
                payment?.paymentId.toString(),
                paymentMethod,
                today,
                amount.text.toString().toInt(),
                status,
                mChildId,
                mUserName,
                userId
            )

            var tempAmount = mOldContribution + amount.text.toString().toInt()

            if ((mOldContribution + amount.text.toString().toInt()) > monthlyAmount.toInt()) {
                var balance = monthlyAmount.toInt() - mOldContribution
                tempAmount = mOldContribution + balance
                donation = ADDonationModel(
                    id, receiptNo,
                    payment?.paymentId.toString(),
                    paymentMethod,
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

                (activity as ADBaseActivity).getNextDate(ADConstants.KEY_GET_NEXT_MONTH_YR,
                    monthYr,
                    object : ADCommonResponseListener {
                        override fun onSuccess(data: Any?) {
                            MySharedPreference(activity as ADBaseActivity).saveStrings(
                                getString(R.string.month_yr), data.toString()
                            )
                            monthYr = data.toString()

                            balance =
                                (mOldContribution + amount.text.toString().toInt()) - monthlyAmount.toInt()
                            mOldContribution = 0
                            donation = ADDonationModel(
                                payment?.orderId.toString(), receiptNo,
                                payment?.paymentId.toString(),
                                "",
                                today,
                                balance,
                                status,
                                mChildId,
                                mUserName,
                                userId
                            )
                            savePaymentToServer(
                                data.toString(),
                                donation,
                                balance,
                                status,
                                true
                            )
                        }

                        override fun onError(data: Any?) {
                            (activity as ADBaseActivity).hideProgress(mProgressDialog)
                        }
                    })
            } else {
                savePaymentToServer(monthYr, donation, tempAmount, status, true)
            }

        } catch (e: Exception) {
            Log.i("Tag", e.printStackTrace().toString())
            (activity as ADBaseActivity).hideProgress(mProgressDialog)
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
//                val key2 = mFirebaseDatabase.push().key.toString()
//                mFirebaseDatabase.child(mChildId).child("contribution")
//                    .child(monthYr).child(key2).setValue(donation)

                mFirebaseDatabase.child(mChildId).child("contribution")
                    .child(monthYr).child("collected_amt")
                    .setValue(amount).addOnSuccessListener {
                        if (status) {

                            if (showCele) {
                                showCelebration()
                            }
                        }
                    }
                    .addOnFailureListener {
                        (activity as ADBaseActivity).hideProgress(mProgressDialog)
                    }

            }
            .addOnFailureListener {
                (activity as ADBaseActivity).hideProgress(mProgressDialog)
            }
    }

    private fun showCelebration() {
        payment_layout.visibility = View.GONE
        congrats_layout.visibility = View.VISIBLE
        (activity as ADBaseActivity).hideProgress(mProgressDialog)

        celebration_view.visibility = View.VISIBLE
        celebration_view.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(ADConstants.ANIMATION_TIME_TO_LIVE)
            .addShapes(Shape.RECT, Shape.CIRCLE)
            .addSizes(Size(ADConstants.ANIMATION_SIZE))
            .setPosition(
                -50f,
                celebration_view.width + 50f,
                -50f,
                -50f
            )
            .streamFor(
                ADConstants.ANIMATION_COUNT,
                ADConstants.ANIMATION_EMITTING_TIME
            )
    }

    private fun updateUserLastTrans(paymentMethod: String) {

        val user = ADUserDetails()
        user.lastTransaction = ADLastCheckout(amount.text.toString().toInt(), paymentMethod)
        val mFirebaseInstance = FirebaseDatabase.getInstance()

        val subId =
            MySharedPreference((activity as ADBaseActivity).applicationContext).getValueString(
                getString(R.string.subscription_id)
            )
        if (subId != null) {
            user.subscriptionId = subId
        }

        // get reference to 'users' node
        val mFirebaseDatabase =
            mFirebaseInstance.getReference((activity as ADBaseActivity).USER_TABLE_NAME)
        mFirebaseDatabase.child(
            MySharedPreference((activity as ADBaseActivity).applicationContext).getValueString(
                getString(R.string.userId)
            )!!
        ).updateChildren(user.toMap2())
    }
}