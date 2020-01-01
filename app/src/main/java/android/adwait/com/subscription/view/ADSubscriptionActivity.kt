package android.adwait.com.subscription.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.admin.model.RestError
import android.adwait.com.donation.model.ADStoredSubRequest
import android.adwait.com.rest_api.ApiClient
import android.adwait.com.rest_api.ApiInterface
import android.adwait.com.subscription.adapter.ADCompletedSubAdapter
import android.adwait.com.subscription.model.ADStoredSubResponse
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
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

        fetchCompletedSub()

        val spannable = SpannableString(getString(R.string.no_subscription))
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
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
        var subRequest = ADStoredSubRequest();
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getCompletedSubscription(subRequest)

        if (!isNetworkAvailable()) {
            showMessage(getString(R.string.no_internet), sub_parent, true)
            return
        }
        mProgressDialog.show()
        call.enqueue(object : Callback<ADStoredSubResponse> {

            override fun onResponse(
                call: Call<ADStoredSubResponse>?,
                response: Response<ADStoredSubResponse>?
            ) {

                if (response != null && response.isSuccessful) {
                    if (response.body().successFlag) {
                        if (response.body() != null) {
                            val fullResponse: ADStoredSubResponse = response?.body()!!
                            val adapter = ADCompletedSubAdapter(
                                this@ADSubscriptionActivity,
                                fullResponse.data
                            )
                            completed_subscription.adapter = adapter
                            no_subscription.visibility = View.GONE
                            completed_subscription.visibility = View.VISIBLE
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
                    no_subscription.visibility = View.VISIBLE
                    completed_subscription.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ADStoredSubResponse>?, t: Throwable?) {
                hideProgress(mProgressDialog)
                no_subscription.visibility = View.VISIBLE
                completed_subscription.visibility = View.GONE
            }
        })
    }

}
