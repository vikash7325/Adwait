package ad.adwait.mcom.be_the_change.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import ad.adwait.mcom.R
import ad.adwait.mcom.be_the_change.model.ADPledgeBdayModel
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_pledge_bday.*
import kotlinx.android.synthetic.main.common_toolbar.*
import java.text.SimpleDateFormat
import java.util.*



class ADPledgeBdayActivity : ADBaseActivity() {
    private val PLEDGE_TABLE: String = "Pledge_bday"
    private lateinit var mProgressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pledge_bday)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })


        pledge_apply.setOnClickListener(View.OnClickListener {

            pledge_page1.visibility = View.GONE
            pledge_page2.visibility = View.VISIBLE
            hint21.visibility = View.GONE
            hint22.visibility = View.VISIBLE
            pledge_apply.visibility = View.GONE
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
                    mProgressDialog = showProgressDialog("", false)
                    mProgressDialog.show()

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
                            hideProgress(mProgressDialog)
                            showAlertDialog(getString(R.string.pledge_bday_msg),getString(R.string.done),"",
                                ad.adwait.mcom.utils.ADViewClickListener {
                                    finish()
                                })
                        }
                        .addOnFailureListener {
                            hideProgress(mProgressDialog)
                            showMessage(getString(R.string.contact_failed), pledge_parent, true)
                        }
                }
            }

        })


        action_menu.setOnClickListener(View.OnClickListener {
            setResult(ad.adwait.mcom.utils.ADConstants.KEY_MENU)
            finish()
        })

        action_profile.setOnClickListener(View.OnClickListener {
            setResult(ad.adwait.mcom.utils.ADConstants.KEY_PROFILE)
            finish()
        })
    }
}