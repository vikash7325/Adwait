package ad.adwait.mcom.be_the_change.view

import ad.adwait.mcom.R
import ad.adwait.mcom.be_the_change.model.ADCampusambassadorModel
import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_campus_ambassador.*
import kotlinx.android.synthetic.main.common_toolbar.*

class ADCampusambassadorActivity : ADBaseActivity() {
    private val CAMPUS_TABLE: String = "Campus_ambassador"
    private lateinit var mProgressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campus_ambassador)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })
        apply_btn.setOnClickListener {
            apply_btn.visibility = View.GONE
            campus_ambassador_page1.visibility = View.GONE
            campus_ambassador_form.visibility = View.VISIBLE
            submit_btn.visibility = View.VISIBLE
        }

        submit_btn.setOnClickListener(View.OnClickListener {

            val loca = location.text.toString()
            val mobile = contact_phone.text.toString()
            val emailId = email.text.toString()

            if (area_of_interest.selectedItemPosition == 0) {
                showMessage(getString(R.string.empty_area_of_interest), campus_parent, true)
            } else if (loca.isEmpty()) {
                location.setError(getString(R.string.empty_location))
            } else if (mobile.isEmpty()) {
                contact_phone.setError(getString(R.string.empty_phone))
            } else if (!isValidPhone(mobile)) {
                contact_phone.setError(getString(R.string.err_invalid_phone))
            } else if (TextUtils.isEmpty(emailId)) {
                email.error = getString(R.string.empty_email)
            } else if (!isValidEmail(emailId)) {
                email.error = getString(R.string.err_invalid_email)
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), campus_parent, true)
                } else {

                    mProgressDialog = showProgressDialog("", false)
                    mProgressDialog.show()
                    val campusModel = ADCampusambassadorModel(
                        MySharedPreference(applicationContext).getValueString(getString(R.string.userId))!!,
                        area_of_interest.selectedItem.toString(),
                        "",
                        loca,
                        mobile, emailId
                    )

                    val fireBaseInstance = FirebaseDatabase.getInstance()
                    val fireBaseDB = fireBaseInstance.getReference(CAMPUS_TABLE)

                    val key = fireBaseDB.push().key.toString()

                    fireBaseDB.child(key).setValue(campusModel)
                        .addOnSuccessListener {
                            hideProgress(mProgressDialog)
                            showAlertDialog(getString(R.string.campus_success),
                                getString(R.string.close),
                                "",
                                ad.adwait.mcom.utils.ADViewClickListener {
                                    finish()
                                })
                        }
                        .addOnFailureListener {
                            hideProgress(mProgressDialog)
                            showMessage(getString(R.string.contact_failed), campus_parent, true)
                        }
                }
            }
        })


        contact_phone.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!isValidPhone(s.toString())) {
                        contact_phone.setError(getString(R.string.err_invalid_phone))
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