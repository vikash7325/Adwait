package android.adwait.com.be_the_change.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.be_the_change.model.ADDayOutModel
import android.adwait.com.utils.ADConstants
import android.adwait.com.utils.ADViewClickListener
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_adwaits_day_out.*
import kotlinx.android.synthetic.main.common_toolbar.*
import java.text.SimpleDateFormat
import java.util.*

class ADAdwaitsDayOutActivity : ADBaseActivity() {
    private val DAY_OUT_TABLE: String = "Adwait_day_out"
    private lateinit var mProgressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adwaits_day_out)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        /*Show Date dialog*/
        date.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    date.setText(SimpleDateFormat("dd-MM-yyyy").format(cal.time))
                }, year, month, day
            )
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        })

        day_out_apply.setOnClickListener(View.OnClickListener {

            if (day_out_page1.visibility == View.VISIBLE) {

                day_out_page1.visibility = View.GONE
                day_out_page2.visibility = View.VISIBLE
                day_out_apply.setText("Submit")
            } else {
                val uName = name.text.toString()
                val uDate = date.text.toString()
                val contact = contact_details.text.toString()
                val uMessage = message.text.toString()

                if (uName.isEmpty()) {
                    name.setError(getString(R.string.empty_username))
                } else if (uDate.isEmpty()) {
                    date.setError(getString(R.string.empty_date))
                } else if (contact.isEmpty()) {
                    contact_details.setError(getString(R.string.empty_contact))
                } else if (!isValidContactDetails(contact)) {
                    contact_details.setError(getString(R.string.invalid_contact))
                } else if (uMessage.isEmpty()) {
                    message.setError(getString(R.string.empty_message))
                } else {
                    if (!isNetworkAvailable()) {
                        showMessage(getString(R.string.no_internet), day_parent, true)
                    } else {
                        val dayoutData = ADDayOutModel(
                            MySharedPreference(applicationContext).getValueString(getString(R.string.userId))!!,
                            uName, uDate, contact, uMessage
                        )
                        mProgressDialog = showProgressDialog("", false)
                        mProgressDialog.show()
                        val fireBaseInstance = FirebaseDatabase.getInstance()
                        val fireBaseDB = fireBaseInstance.getReference(DAY_OUT_TABLE)

                        val key = fireBaseDB.push().key.toString()

                        fireBaseDB.child(key).setValue(dayoutData)
                            .addOnSuccessListener {
                                hideProgress(mProgressDialog)
                                showAlertDialog(getString(R.string.dayout_success),
                                    getString(R.string.close),
                                    "",
                                    ADViewClickListener {
                                        finish()
                                    })
                            }
                            .addOnFailureListener {
                                hideProgress(mProgressDialog)
                                showMessage(getString(R.string.contact_failed), day_parent, true)
                            }
                    }
                }
            }
        })

        action_menu.setOnClickListener(View.OnClickListener {
            setResult(ADConstants.KEY_MENU)
            finish()
        })

        action_profile.setOnClickListener(View.OnClickListener {
            setResult(ADConstants.KEY_PROFILE)
            finish()
        })
    }
}