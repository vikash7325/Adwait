package ad.adwait.mcom.profile.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import ad.adwait.mcom.R
import ad.adwait.mcom.registeration.model.ADUserDetails
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_edit_profile.*
import java.util.*

class ADEditProfileActivity : ADBaseActivity() {

    private var isEdit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_edit_profile)

        if (intent != null && intent.hasExtra("isedit")) {
            isEdit = intent.getBooleanExtra("isedit", false)
            val userData = intent.getParcelableExtra<ADUserDetails>("data")

            if (!isEdit) {
                setFinishOnTouchOutside(false)
            }

            username.setText(userData.userName)
            email.setText(userData.emailAddress)
            password.setText(userData.password)
            phone.setText(userData.phoneNumber)
            dob.setText(userData.date_of_birth)

            if (userData.password.isEmpty()) {
                password_view.visibility = View.GONE
            }
        }

        dob.setOnClickListener(View.OnClickListener {
            showCalendar()
        })

        save_btn.setOnClickListener(View.OnClickListener {

            val name = username.text.toString()
            val pass = password.text.toString()
            val phoneNo = phone.text.toString()
            val emailId = email.text.toString()
            val doBirth = dob.text.toString()

            email.hideKeyboard()

            if (TextUtils.isEmpty(name)) {
                username.error = getString(R.string.empty_username)
            } else if (password_view.visibility == View.VISIBLE && TextUtils.isEmpty(pass)) {
                password.error = getString(R.string.empty_password)
            } else if (TextUtils.isEmpty(phoneNo)) {
                phone.error = getString(R.string.empty_phone)
            } else if (TextUtils.isEmpty(emailId)) {
                email.error = getString(R.string.empty_email)
            } else if (TextUtils.isEmpty(doBirth)) {
                dob.error = getString(R.string.empty_dob)
            } else if (password_view.visibility == View.VISIBLE && !isValidPassword(pass)) {
                password.error = getString(R.string.err_invalid_password)
            } else if (!isValidEmail(emailId)) {
                email.error = getString(R.string.err_invalid_email)
            } else if (!isValidPhone(phoneNo)) {
                phone.error = getString(R.string.err_invalid_phone)
            } else if (getAge(doBirth,"dd-MM-yyyy") < 18) {
                showMessage(getString(R.string.invalid_dob), null, true)
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), null, true)
                    return@OnClickListener
                }
                progress_layout.visibility = View.VISIBLE
                val userRegister = ADUserDetails(name, pass, phoneNo, emailId, doBirth)
                saveData(userRegister)
            }
        })


        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isValidEmail(s.toString())) {
                    email.error = getString(R.string.err_invalid_email)
                } else {
                    email.error = null
                }
            }
        })

        phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isValidPhone(s.toString())) {
                    phone.error = getString(R.string.err_invalid_phone)
                } else {
                    phone.error = null
                    phone.hideKeyboard()
                }
            }

        })

    }

    private fun showCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            dob.setText("" + dayOfMonth + "-" + monthOfYear + "-" + year)
        }, year, month, day)

        dpd.datePicker.maxDate = System.currentTimeMillis() + 1000
        dpd.show()
    }

    fun saveData(user: ADUserDetails) {

        val mFirebaseInstance = FirebaseDatabase.getInstance()

        // get reference to 'users' node
        val mFirebaseDatabase = mFirebaseInstance.getReference(USER_TABLE_NAME)
        mFirebaseDatabase.child(MySharedPreference(applicationContext).getValueString(getString(R.string.userId))!!).updateChildren(user.toMap())
                .addOnSuccessListener {
                    progress_layout.visibility = View.GONE
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    showMessage(getString(R.string.profile_update_failed), null, true)
                    progress_layout.visibility = View.GONE
                }
    }

    override fun onStart() {
        super.onStart()
        val window = this.getWindow()
        window.setLayout((getScreenDetails(false) * 0.9).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
    }


    override fun onBackPressed() {
        if (isEdit) {
            super.onBackPressed()
        }
    }
}