package ad.adwait.mcom.be_the_change.view

import ad.adwait.mcom.R
import ad.adwait.mcom.be_the_change.model.ADAreaHeadModel
import ad.adwait.mcom.utils.ADConstants
import ad.adwait.mcom.utils.ADViewClickListener
import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_area_head.*
import kotlinx.android.synthetic.main.common_toolbar.*


class ADAreaHeadActivity : ADBaseActivity() {
    private val OUR_AREA_HEAD_TABLE: String = "Our_Area_Head"
    private lateinit var mProgressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_head)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener { onBackPressed() }


        area_head_apply.setOnClickListener{

            area_head_page1.visibility = View.GONE
            area_head_page2.visibility = View.VISIBLE
            area_head_apply.visibility = View.GONE
        }

        area_head_submit.setOnClickListener{
            val uName = name.text.toString()
            val mobile = contact_phone.text.toString()
            val emailId = email.text.toString()
            val locationVal = location.text.toString()
            val cityVal = city.text.toString()
            val stateVal = state.text.toString()

            if (uName.isEmpty()) {
                name.setError(getString(R.string.empty_username))
            } else if (mobile.isEmpty()) {
                contact_phone.setError(getString(R.string.empty_phone))
            } else if (!isValidPhone(mobile)) {
                contact_phone.setError(getString(R.string.err_invalid_phone))
            } else if (TextUtils.isEmpty(emailId)) {
                email.error = getString(R.string.empty_email)
            } else if (!isValidEmail(emailId)) {
                email.error = getString(R.string.err_invalid_email)
            } else if (locationVal.isEmpty()) {
                location.setError(getString(R.string.empty_location))
            } else if (cityVal.isEmpty()) {
                city.setError(getString(R.string.empty_city))
            } else if (stateVal.isEmpty()) {
                state.setError(getString(R.string.empty_state))
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), area_head_parent, true)
                } else {
                    mProgressDialog = showProgressDialog("", false)
                    mProgressDialog.show()

                    val dataModel =
                        ADAreaHeadModel(
                            MySharedPreference(applicationContext).getValueString(getString(R.string.userId))!!,
                            uName,
                            mobile,
                            emailId, locationVal, cityVal, stateVal
                        )
                    val fireBaseInstance = FirebaseDatabase.getInstance()
                    val fireBaseDB = fireBaseInstance.getReference(OUR_AREA_HEAD_TABLE)

                    val key = fireBaseDB.push().key.toString()

                    fireBaseDB.child(key).setValue(dataModel)
                        .addOnSuccessListener {
                            hideProgress(mProgressDialog)
                            showAlertDialog(
                                getString(R.string.area_head_success),
                                getString(R.string.done),
                                "",
                                ADViewClickListener {
                                    finish()
                                })
                        }
                        .addOnFailureListener {
                            hideProgress(mProgressDialog)
                            showMessage(getString(R.string.contact_failed), area_head_parent, true)
                        }
                }
            }

        }


        action_menu.setOnClickListener{
            setResult(ADConstants.KEY_MENU)
            finish()
        }

        action_profile.setOnClickListener{
            setResult(ADConstants.KEY_PROFILE)
            finish()
        }
    }
}