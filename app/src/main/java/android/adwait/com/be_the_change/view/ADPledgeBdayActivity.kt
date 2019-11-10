package android.adwait.com.be_the_change.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.be_the_change.model.ADPledgeBdayModel
import android.adwait.com.utils.ADViewClickListener
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_pledge_bday.*
import java.text.SimpleDateFormat
import java.util.*



class ADPledgeBdayActivity : ADBaseActivity() {
    private val PLEDGE_TABLE: String = "Pledge_bday"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pledge_bday)
        val toolbar: Toolbar = findViewById(R.id.pledge_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })


        pledge_apply.setOnClickListener(View.OnClickListener {

            pledge_page1.visibility = View.GONE
            pledge_page2.visibility = View.VISIBLE
        })


        birthday.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    birthday.setText(SimpleDateFormat("dd-MM-yyyy").format(cal.time).toString())
                }, year, month, day
            )

            dpd.datePicker.maxDate = System.currentTimeMillis() + 1000
            dpd.show()
        })

        pledge_submit.setOnClickListener(View.OnClickListener {
            val uName = name.text.toString()
            val uBday = birthday.text.toString()
            val contact = contact_details.text.toString()

            if (uName.isEmpty()) {
                name.setError(getString(R.string.empty_username))
            } else if (uBday.isEmpty()) {
                birthday.setError(getString(R.string.empty_dob))
            } else if (contact.isEmpty()) {
                contact_details.setError(getString(R.string.empty_contact))
            } else if (!isValidContactDetails(contact)) {
                contact_details.setError(getString(R.string.invalid_contact))
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), pledge_parent, true)
                } else {
                    progress_layout.visibility = View.VISIBLE
                    val dataModel = ADPledgeBdayModel(
                        MySharedPreference(applicationContext).getValueString(getString(R.string.userId))!!,
                        uName,
                        uBday,
                        contact
                    )
                    val fireBaseInstance = FirebaseDatabase.getInstance()
                    val fireBaseDB = fireBaseInstance.getReference(PLEDGE_TABLE)

                    val key = fireBaseDB.push().key.toString()

                    fireBaseDB.child(key).setValue(dataModel)
                        .addOnSuccessListener {
                            progress_layout.visibility = View.GONE
                            showAlertDialog(getString(R.string.pledge_bday_msg),getString(R.string.done),"", ADViewClickListener {
                                finish()
                            })
                        }
                        .addOnFailureListener {
                            progress_layout.visibility = View.GONE
                            showMessage(getString(R.string.contact_failed), pledge_parent, true)
                        }
                }
            }

        })

    }
}