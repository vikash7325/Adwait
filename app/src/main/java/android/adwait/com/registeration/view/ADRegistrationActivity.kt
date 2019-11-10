package android.adwait.com.registeration.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.CommonUtils
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.dashboard.view.ADDashboardActivity
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.utils.ADConstants
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.logo_layout.*
import kotlinx.android.synthetic.main.register_otp.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.util.*
import java.util.concurrent.TimeUnit


class ADRegistrationActivity : ADBaseActivity() {

    private lateinit var mFirebaseDatabase: DatabaseReference
    private lateinit var mFirebaseInstance: FirebaseDatabase
    private val TAG: String = "ADRegistrationActivity"
    private lateinit var mFirebaseAuth: FirebaseAuth
    private var userRegister = ADUserDetails()
    private var storedVerificationId: String = ""
    private var mUserId: String = ""
    private var mIsRegistered: Boolean = false
    private val SEND_EMAIL_VERIFICATION: Int = 0
    private val SEND_MOBILE_VERIFICATION: Int = 1
    private val SEND_ALL_VERIFICATION: Int = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mFirebaseInstance = FirebaseDatabase.getInstance()

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference(USER_TABLE_NAME)

        back_icon.visibility = View.VISIBLE

        back_icon.setOnClickListener(View.OnClickListener { finish() })

        dob.setOnClickListener(View.OnClickListener {
            password.hideKeyboard()
            showCalendar()
        })

        //Submit button click handling
        submit_btn.setOnClickListener(View.OnClickListener {

            val name = username.text.toString()
            val pass = password.text.toString()
            val phoneNo = phone.text.toString()
            val emailId = email.text.toString()
            val doBirth = dob.text.toString()

            email.hideKeyboard()

            if (TextUtils.isEmpty(name)) {
                username.error = getString(R.string.empty_username)
            } else if (TextUtils.isEmpty(pass)) {
                password.error = getString(R.string.empty_password)
            } else if (TextUtils.isEmpty(phoneNo)) {
                phone.error = getString(R.string.empty_phone)
            } else if (TextUtils.isEmpty(emailId)) {
                email.error = getString(R.string.empty_email)
            } else if (TextUtils.isEmpty(doBirth)) {
                dob.error = getString(R.string.empty_dob)
            } else if (!isValidPassword(pass)) {
                password.error = getString(R.string.err_invalid_password)
            } else if (!isValidEmail(emailId)) {
                email.error = getString(R.string.err_invalid_email)
            } else if (!isValidPhone(phoneNo)) {
                phone.error = getString(R.string.err_invalid_phone)
            } else if (getAge(doBirth, "dd-MM-yyyy") < 14) {
                showMessage(getString(R.string.invalid_dob), register_parent, true)
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), register_parent, true)
                    return@OnClickListener
                }
                progress_layout.visibility = View.VISIBLE
                userRegister = ADUserDetails(name, pass, phoneNo, emailId, doBirth)
                userRegister.myReferralCode = getReferralString()
                mFirebaseAuth = FirebaseAuth.getInstance()
                val mUserDataTable =
                    FirebaseDatabase.getInstance().reference.child(USER_TABLE_NAME)

                mUserDataTable.addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(data: DataSnapshot) {

                        if (data.exists()) {
                            var elve = ADUserDetails()
                            var count: Long = 0
                            val size = data.childrenCount
                            for (user in data.children) {
                                elve = user.getValue(ADUserDetails::class.java)!!

                                if (elve.emailAddress.equals(emailId) || (elve.phoneNumber.isEmpty() &&
                                            elve.phoneNumber.equals(phoneNo))) {
                                    progress_layout.visibility = View.GONE
                                    showMessage(
                                        getString(R.string.registration_duplicate),
                                        register_parent,
                                        true)
                                    break
                                } else {
                                    count++
                                }
                            }

                            if (count == size) {
                                registerUser()
                            }

                        } else {
                            registerUser()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showMessage(getString(R.string.registration_failed), register_parent, true)
                        progress_layout.visibility = View.GONE
                    }
                })
            }
        })

        //Done button click handling
        done_btn.setOnClickListener(View.OnClickListener {
            var dash = Intent(applicationContext, ADDashboardActivity::class.java)
            startToNextScreen(dash, true, false)
            finish()
        })

        //Skip button pressed in referral code
        referral_skip.setOnClickListener(View.OnClickListener {
            sendVerificationCodes(SEND_ALL_VERIFICATION)
        })

        //Referral filled and submit pressed
        ref_submit_btn.setOnClickListener(View.OnClickListener {

            val code = referral_code.text.toString().trim()

            if (TextUtils.isEmpty(code)) {
                showMessage(getString(R.string.empty_referral_code), register_parent, true)
            } else {
                userRegister.usedReferralCode = code
                if (code.equals(ADConstants.ADMIN_REFERRAL_CODE)) {
                    userRegister.can_add_data = true
                }
                sendVerificationCodes(SEND_ALL_VERIFICATION)
            }
        })

        //Final submit button click
        final_submit.setOnClickListener(View.OnClickListener {
            progress_layout.visibility = View.VISIBLE
            verifyMobileNum()
        })


        verify_mobile.setOnClickListener(View.OnClickListener {
            verify_layout.visibility = View.GONE
            register_layout.visibility = View.VISIBLE
        })

        verify_email.setOnClickListener(View.OnClickListener {
            verify_layout.visibility = View.GONE
            register_layout.visibility = View.VISIBLE
        })

        resend_otp.setOnClickListener(View.OnClickListener {
            sendVerificationCodes(SEND_MOBILE_VERIFICATION)
        })

        resend_mail.setOnClickListener(View.OnClickListener {
            sendVerificationCodes(SEND_EMAIL_VERIFICATION)
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
                }
            }

        })
    }

    private fun registerUser() {
        mFirebaseAuth.createUserWithEmailAndPassword(
            userRegister.emailAddress,
            userRegister.password
        )
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    //Registration OK
                    val user = mFirebaseAuth.currentUser
                    if (user != null) {
                        mUserId = user.uid
                    }
                    if (has_referral.isChecked) {
                        register_layout.visibility = View.GONE
                        referral_code_layout.visibility = View.VISIBLE
                    } else {
                        sendVerificationCodes(SEND_ALL_VERIFICATION)
                    }

                    progress_layout.visibility = View.GONE
                } else {
                    progress_layout.visibility = View.GONE
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        showMessage(
                            getString(R.string.registration_duplicate),
                            register_parent,
                            true
                        )
                    } else {
                        showMessage(
                            getString(R.string.registration_failed),
                            register_parent,
                            true
                        )
                    }
                }
            }
    }

    override fun onStop() {
        super.onStop()
        if (!mIsRegistered && ::mFirebaseAuth.isInitialized) {
            mFirebaseAuth.currentUser?.delete()
        }
    }

    private fun sendVerificationCodes(case: Int) {
        register_layout.visibility = View.GONE
        referral_code_layout.visibility = View.GONE
        verify_layout.visibility = View.VISIBLE
        val user = mFirebaseAuth.currentUser

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
            }

            override fun onCodeSent(
                verificationId: String?,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:" + verificationId!!)
                storedVerificationId = verificationId
                verify_mobile.text = userRegister.phoneNumber
                progress_layout.visibility = View.GONE
            }
        }

        if (user != null) {

            if (case == SEND_EMAIL_VERIFICATION) {
                user.sendEmailVerification()
                    .addOnCompleteListener(this) { task ->
                        progress_layout.visibility = View.GONE
                        verify_email.text = userRegister.emailAddress
                    }
            } else if (case == SEND_MOBILE_VERIFICATION) {
                PhoneAuthProvider.getInstance()
                    .verifyPhoneNumber(
                        "+91" + userRegister.phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        callbacks
                    )
            } else {

                user.sendEmailVerification()
                    .addOnCompleteListener(this) { task ->
                        progress_layout.visibility = View.GONE
                        verify_email.text = userRegister.emailAddress
                    }

                PhoneAuthProvider.getInstance()
                    .verifyPhoneNumber(
                        "+91" + userRegister.phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        callbacks
                    )
            }
        }
    }

    private fun verifyMobileNum() {
        val otp = pinview.value.toString()

        if (TextUtils.isEmpty(otp)) {
            showMessage(getString(R.string.empty_pin), register_parent, true)
        } else if (otp.length < 6) {
            showMessage(getString(R.string.invalid_pin), register_parent, true)
        } else {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        saveData(userRegister)
                    } else {
                        progress_layout.visibility = View.GONE
                    }
                }
        }
    }

    /**
     * Method to register user data
     */
    fun saveData(user: ADUserDetails) {


        mFirebaseDatabase.child(mUserId).setValue(user)
            .addOnSuccessListener {

                progress_layout.visibility = View.GONE
                var pre = MySharedPreference(applicationContext)
                pre.saveBoolean(getString(R.string.logged_in), true)
                pre.saveBoolean(getString(R.string.registered), true)
                register_layout.visibility = View.GONE
                register_layout.visibility = View.GONE
                referral_code_layout.visibility = View.GONE
                verify_layout.visibility = View.GONE
                congrats_layout.visibility = View.VISIBLE

                var con: String = getString(R.string.congrats_1)

                val hint = java.lang.String.format(con, user.userName)
                mIsRegistered = true
                congrats_text.text = CommonUtils.getHtmlText(hint)


                celebration_view.visibility = View.VISIBLE
                celebration_view.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(ADConstants.ANIMATION_TIME_TO_LIVE)
                    .addShapes(Shape.RECT, Shape.CIRCLE)
                    .addSizes(Size(ADConstants.ANIMATION_SIZE))
                    .setPosition(-50f, celebration_view.width + 50f, -50f, -50f)
                    .streamFor(ADConstants.ANIMATION_COUNT, ADConstants.ANIMATION_EMITTING_TIME)
            }
            .addOnFailureListener {
                Log.i("Test", it.message)
                showMessage(getString(R.string.registration_failed), register_parent, true)
                progress_layout.visibility = View.GONE
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
        dpd.datePicker.maxDate = System.currentTimeMillis() + 1000

        dpd.show()
    }
}