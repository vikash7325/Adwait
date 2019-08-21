package android.adwait.com.admin.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.admin.model.*
import android.adwait.com.rest_api.ApiClient
import android.adwait.com.rest_api.ApiInterface
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_child.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ADAddChildActivity : ADBaseActivity() {

    private lateinit var mChildData: ADAddChildModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_child)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        dob.setOnClickListener(View.OnClickListener {
            dob.hideKeyboard()
            showCalendar()
        })

        submit_btn.setOnClickListener(View.OnClickListener {

            val name = child_name.text.toString()
            val doBirth = dob.text.toString()
            val ngo = ngo_name.text.toString()
            val school = school_name.text.toString()
            val emailId = email.text.toString()
            val mentor = mentor_name.text.toString()
            val amountNeeded = monthly_amt.text.toString()
            val carrier = carrier_int.text.toString()
            val childHobby = hobbies.text.toString()
            val guardian = guardian_name.text.toString()
            val zipcode = pincode.text.toString()
            val childCity = city.text.toString()
            val ngoAddress = address.text.toString()

            val holderName = account_name.text.toString()
            val number = account_no.text.toString()
            val ifsc = ifsc_code.text.toString()
            val accountType = account_type.text.toString()
            val businessName = business_name.text.toString()
            val businessType = business_type.text.toString()

            val educationAmt = education.text.toString()
            val foodAmt = food.text.toString()
            val hobbiesAmt = hobbies_amt.text.toString()
            val necessityAmt = necessity.text.toString()
            val extrasAmt = extras.text.toString()

            extras.hideKeyboard()

            if (TextUtils.isEmpty(name)) {
                child_name.error = getString(R.string.empty_username)
            } else if (TextUtils.isEmpty(doBirth)) {
                dob.error = getString(R.string.empty_dob)
            } else if (TextUtils.isEmpty(ngo)) {
                ngo_name.error = getString(R.string.empty_ngo)
            } else if (TextUtils.isEmpty(school)) {
                school_name.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(emailId)) {
                email.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(amountNeeded)) {
                monthly_amt.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(carrier)) {
                carrier_int.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(childHobby)) {
                hobbies.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(guardian)) {
                guardian_name.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(zipcode)) {
                pincode.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(childCity)) {
                city.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(ngoAddress)) {
                address.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(holderName)) {
                account_name.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(number)) {
                account_no.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(ifsc)) {
                ifsc_code.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(accountType)) {
                account_type.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(businessName)) {
                business_name.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(businessType)) {
                business_type.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(educationAmt)) {
                education.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(foodAmt)) {
                food.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(hobbiesAmt)) {
                hobbies_amt.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(necessityAmt)) {
                necessity.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(extrasAmt)) {
                extras.error = getString(R.string.empty_field)
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), add_child_parent, true)
                    return@OnClickListener
                }
                progress_layout.visibility = View.VISIBLE

                val bankDetails = ADBankDetails(number, ifsc, holderName, accountType)
                val splitDetails = ADMoneySplitUp(
                    educationAmt.toInt(), foodAmt.toInt(), hobbiesAmt.toInt(),
                    necessityAmt.toInt(), extrasAmt.toInt()
                )

                mChildData = ADAddChildModel(
                    name,
                    doBirth,
                    mentor,
                    ngoAddress,
                    emailId,
                    amountNeeded.toLong(),
                    carrier,
                    guardian,
                    childHobby,
                    zipcode,
                    childCity,
                    school,
                    ngo,
                    "",
                    ""
                )

                mChildData.bankDetails = bankDetails
                mChildData.splitDetails = splitDetails
            }

            var accountRequest = ADCreateAccountRequest(
                holderName, emailId, true, ADBankAccount(ifsc, holderName, accountType, number),
                AccountDetails(businessName, businessType)
            )

            val apiService = ApiClient.getClient(
                getString(R.string.razor_pay_id) + ":" +
                        getString(R.string.razor_pay_secret) + "@"
            ).create(ApiInterface::class.java)

            val call = apiService.createAccount(accountRequest)

            call.enqueue(object : Callback<ADCreateAccountResponse> {
                override fun onFailure(call: Call<ADCreateAccountResponse>?, t: Throwable?) {
                    progress_layout.visibility = View.GONE
                    showMessage("Something went wrong. Try again later", add_child_parent, true)
                }

                override fun onResponse(
                    call: Call<ADCreateAccountResponse>?,
                    response: Response<ADCreateAccountResponse>?
                ) {
                    if (response!=null) {
                        val data: ADCreateAccountResponse = response?.body()!!
                        mChildData.accountId = data.id
                        addChildToServer()
                    }
                }

            })

        })
    }

    private fun addChildToServer(){
        val mChildTable =
            FirebaseDatabase.getInstance().reference.child("Children_Details")

        val key = mChildTable.push().key.toString()

        mChildTable.child(key).setValue(mChildData)
            .addOnSuccessListener {
                progress_layout.visibility = View.GONE
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener {
                progress_layout.visibility = View.GONE
                showMessage(it.message.toString(),null,true)
            }
    }

    private fun showCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                dob.setText("" + dayOfMonth + "-" + monthOfYear + "-" + year)
            },
            year,
            month,
            day
        )

        dpd.show()
    }
}