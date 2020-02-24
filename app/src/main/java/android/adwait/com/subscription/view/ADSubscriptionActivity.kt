package android.adwait.com.subscription.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.admin.model.RestError
import android.adwait.com.donation.model.ADDonationModel
import android.adwait.com.rest_api.ApiClient
import android.adwait.com.rest_api.ApiInterface
import android.adwait.com.subscription.adapter.ADCompletedSubAdapter
import android.adwait.com.subscription.model.ADSubscriptionResponse
import android.app.Activity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_adsubscription.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ADSubscriptionActivity : ADBaseActivity() {

    private lateinit var mProgressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adsubscription)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        mProgressDialog = showProgressDialog("", false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        getSubscription()

        val spannable = SpannableString(getString(R.string.no_subscription))
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary)),
            40,
            getString(R.string.no_subscription).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        no_subscription.setText(spannable, TextView.BufferType.SPANNABLE)

        no_subscription.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun fetchCompletedSub() {

        val mUserId =
            MySharedPreference(this).getValueString(getString(R.string.userId))
        val subscriptionList = ArrayList<ADDonationModel>()
        FirebaseDatabase.getInstance()
            .getReference(SUBSCRIPTION_TABLE_NAME)
            .orderByChild("date")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (data in dataSnapshot.children) {
                            Log.e("", "onDataChange : " + data)
                            val donation = data.getValue(ADDonationModel::class.java)
                            if (donation?.userId.equals(mUserId)) {
                                subscriptionList.add(donation!!)
                            }
                        }

                        val adapter =
                            ADCompletedSubAdapter(this@ADSubscriptionActivity, subscriptionList)
                        completed_subscription.adapter = adapter
                        no_subscription.visibility = View.GONE
                        completed_subscription.visibility = View.VISIBLE
                        hideProgress(mProgressDialog)
                    } else {
                        hideProgress(mProgressDialog)
                        no_subscription.visibility = View.VISIBLE
                        completed_subscription.visibility = View.GONE
                        activeSubscription.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    hideProgress(mProgressDialog)
                    no_subscription.visibility = View.GONE
                    completed_subscription.visibility = View.GONE
                    activeSubscription.visibility = View.GONE
                    showMessage(getString(R.string.generic_error), sub_parent, true)
                }

            })
    }

    private fun getSubscription() {
        val subId =
            MySharedPreference(applicationContext).getValueString(
                getString(R.string.subscription_id)
            )
        mProgressDialog.show()
        if (subId == null || subId.length == 0) {
            hideProgress(mProgressDialog)
            no_subscription.visibility = View.VISIBLE
            completed_subscription.visibility = View.GONE
            activeSubscription.visibility = View.GONE
            return
        }
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getSubscription(subId)

        if (!isNetworkAvailable()) {
            showMessage(getString(R.string.no_internet), null, true)
            return
        }
        call.enqueue(object : Callback<ADSubscriptionResponse> {

            override fun onResponse(
                call: Call<ADSubscriptionResponse>?,
                response: Response<ADSubscriptionResponse>?
            ) {
                fetchCompletedSub()
                if (response != null && response.isSuccessful) {
                    if (response.body() != null && response.body().isSuccessFlag) {
                        val fullResponse: ADSubscriptionResponse = response?.body()!!
                        if (fullResponse.data.status.toLowerCase().equals("active")) {
                            val text = String.format(
                                getString(R.string.active_subscription),
                                fullResponse.data.remaining_count
                            )
                            activeSubscription.setText(text)
                            activeSubscription.visibility = View.VISIBLE
                        } else {
                            hideProgress(mProgressDialog)
                            no_subscription.visibility = View.VISIBLE
                            completed_subscription.visibility = View.GONE
                            activeSubscription.visibility = View.GONE
                        }
                    }
                } else {
                    val error = Gson().fromJson(
                        response?.errorBody()?.charStream(),
                        RestError::class.java
                    )
                    Log.i("Testing ==> ", error.toString())
                    hideProgress(mProgressDialog)
                }

            }

            override fun onFailure(call: Call<ADSubscriptionResponse>?, t: Throwable?) {
                hideProgress(mProgressDialog)
            }
        })
    }

    fun cancelCompletedSub() {
        val subId =
            MySharedPreference(applicationContext).getValueString(
                getString(R.string.subscription_id)
            )
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.cancelSubscription(subId)

        if (!isNetworkAvailable()) {
            showMessage(getString(R.string.no_internet), sub_parent, true)
            return
        }
        mProgressDialog.show()
        call.enqueue(object : Callback<ADSubscriptionResponse> {

            override fun onResponse(
                call: Call<ADSubscriptionResponse>?,
                response: Response<ADSubscriptionResponse>?
            ) {

                if (response != null && response.isSuccessful) {
                    if (response.body().isSuccessFlag) {
                        if (response.body() != null) {
                            fetchCompletedSub()
                        }
                    } else {
                        showMessage(response.body().message, sub_parent, true)
                        hideProgress(mProgressDialog)
                    }
                } else {
                    val error = Gson().fromJson(
                        response?.errorBody()?.charStream(),
                        RestError::class.java
                    )
                    Log.i("Testing ==> ", error.toString())
                    showMessage(error.error.description, sub_parent, true)
                    hideProgress(mProgressDialog)
                }
            }

            override fun onFailure(call: Call<ADSubscriptionResponse>?, t: Throwable?) {
                hideProgress(mProgressDialog)
            }
        })
    }
}
