package ad.adwait.mcom.login.view

import ad.adwait.mcom.R
import ad.adwait.mcom.registeration.model.ADUserDetails
import and.com.polam.utils.ADBaseActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_forget_password.*
import java.util.concurrent.TimeUnit


class ADForgetActivity : ADBaseActivity() {
    private val TAG: String = "ADForgetActivity"
    private var storedVerificationId: String = ""
    private var userEmail: String = ""
    private var userpassword: String = ""
    private var isDeleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_forget_password)
        storedVerificationId = ""
        setFinishOnTouchOutside(false)

        via_Email.setOnClickListener(View.OnClickListener {
            email_layout.visibility = View.VISIBLE
            options_layout.visibility = View.GONE
        })

        via_phone.setOnClickListener(View.OnClickListener {
            mobile_layout.visibility = View.VISIBLE
            options_layout.visibility = View.GONE
        })

        //Request OTP
        request_otp.setOnClickListener(View.OnClickListener {
            val mobile = phone_num.text.toString().trim()

            phone_num.hideKeyboard()
            if (TextUtils.isEmpty(mobile)) {
                phone_num.setError(getString(R.string.empty_phone));
            } else if (!isValidPhone(mobile)) {
                phone_num.setError(getString(R.string.err_invalid_phone));
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), null, true)
                    return@OnClickListener
                }
                progress_layout.visibility = View.VISIBLE
                verifyMobileNumber(mobile)
            }
        }
        )

        pin1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isDeleted) {
                    isDeleted = false
                    return
                }
                if (s.toString().trim().length == 1) {
                    pin2.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (count > after) {
                    isDeleted = true
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        pin2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isDeleted) {
                    isDeleted = false
                    return
                }
                if (s.toString().trim().length == 1) {
                    pin3.requestFocus()
                } else {
                    pin1.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (count > after) {
                    isDeleted = true
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        pin3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isDeleted) {
                    isDeleted = false
                    return
                }
                if (s.toString().trim().length == 1) {
                    pin4.requestFocus()
                } else {
                    pin2.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (count > after) {
                    isDeleted = true
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        pin4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isDeleted) {
                    isDeleted = false
                    return
                }
                if (s.toString().trim().length == 1) {
                    pin5.requestFocus()
                } else {
                    pin3.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (count > after) {
                    isDeleted = true
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        pin5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isDeleted) {
                    isDeleted = false
                    return
                }
                if (s.toString().trim().length == 1) {
                    pin6.requestFocus()
                } else {
                    pin4.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (count > after) {
                    isDeleted = true
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        pin6.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isDeleted) {
                    isDeleted = false
                    return
                }
                if (s.toString().trim().length == 1) {
                    pin6.hideKeyboard()
                } else {
                    pin5.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (count > after) {
                    isDeleted = true
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        pin2.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (pin2.text.toString().length == 0) {
                    isDeleted = true
                    Thread.sleep(50)
                    pin1.setText("")
                    pin1.requestFocus()
                }
            }
            false
        }
        pin3.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (pin3.text.toString().length == 0) {
                    isDeleted = true
                    Thread.sleep(50)
                    pin2.setText("")
                    pin2.requestFocus()
                }
            }
            false
        }
        pin4.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (pin4.text.toString().length == 0) {
                    isDeleted = true
                    Thread.sleep(50)
                    pin3.setText("")
                    pin3.requestFocus()
                }
            }
            false
        }
        pin5.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (pin5.text.toString().length == 0) {
                    isDeleted = true
                    Thread.sleep(50)
                    pin4.setText("")
                    pin4.requestFocus()
                }
            }
            false
        }
        pin6.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (pin6.text.toString().length == 0) {
                    isDeleted = true
                    Thread.sleep(50)
                    pin5.setText("")
                    pin5.requestFocus()
                }
            }
            false
        }


        //OTP Submit
        otp_submit.setOnClickListener(View.OnClickListener {
            val otp = pin1.text.toString() + pin2.text.toString() + pin3.text.toString() +
                    pin4.text.toString() + pin5.text.toString() + pin6.text.toString()
            if (TextUtils.isEmpty(otp)) {
                showMessage(getString(R.string.empty_pin), null, true)
            } else if (otp.length < 6) {
                showMessage(getString(R.string.invalid_pin), null, true)
            } else if (storedVerificationId.isEmpty()) {
                showMessage(getString(R.string.no_pin_sent), null, true)
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), null, true)
                    return@OnClickListener
                }
                progress_layout.visibility = View.VISIBLE
                val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, otp)
                signInWithPhoneAuthCredential(credential)
            }
        })

        //Submit password
        password_submit.setOnClickListener(View.OnClickListener {

            val newPassword = new_password.text.toString().trim()
            val confirmPassword = confirm_password.text.toString().trim()

            if (TextUtils.isEmpty(newPassword)) {
                new_password.setError(getString(R.string.empty_password))
            } else if (TextUtils.isEmpty(confirmPassword)) {
                confirm_password.setError(getString(R.string.empty_password))
            } else if (!isValidPassword(newPassword)) {
                new_password.setError(getString(R.string.err_invalid_password))
            } else if (!isValidPassword(confirmPassword)) {
                confirm_password.setError(getString(R.string.err_invalid_password))
            } else if (!newPassword.equals(confirmPassword)) {
                new_password.setError(getString(R.string.password_mismatch))
                confirm_password.setError(getString(R.string.password_mismatch))
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), null, true)
                    return@OnClickListener
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userpassword)
                    .addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser

                            if (user != null) {
                                progress_layout.visibility = View.VISIBLE

                                user.updatePassword(newPassword)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            showMessage(
                                                getString(R.string.mobile_reset_success),
                                                null,
                                                true
                                            )
                                            passwordReset()
                                        } else {
                                            progress_layout.visibility = View.GONE
                                            showMessage(
                                                getString(R.string.unable_to_reset_password),
                                                null,
                                                true
                                            )
                                        }
                                    }
                            }
                        } else {
                            progress_layout.visibility = View.GONE
                        }
                    }
            }
        })

        //Email submit
        email_submit.setOnClickListener(View.OnClickListener {
            var emailId = email.text.toString().trim()

            if (TextUtils.isEmpty(emailId)) {
                email.setError(getString(R.string.empty_email))
            } else if (!isValidEmail(emailId)) {
                email.setError(getString(R.string.err_invalid_email))
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), null, true)
                    return@OnClickListener
                }
                progress_layout.visibility = View.VISIBLE
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailId)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            showMessage(getString(R.string.email_success), null, true)
                            passwordReset()
                        } else {
                            progress_layout.visibility = View.GONE
                            showMessage(
                                getString(R.string.unable_to_send_reset_mail),
                                null,
                                true
                            )
                        }
                    }
            }
        })

        cancel.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    private fun passwordReset() {
        progress_layout.visibility = View.GONE
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:$credential")

            val code = credential.getSmsCode()

            if (code != null) {
                Log.d(TAG, "code:$code")
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }
        }

        override fun onCodeSent(
            verificationId: String?,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent:" + verificationId!!)
            storedVerificationId = verificationId
            showMessage(getString(R.string.otp_sent), null, false)
            progress_layout.visibility = View.GONE
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    var user = FirebaseAuth.getInstance().currentUser
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success" + user!!.displayName)
                    storedVerificationId = ""

                    mobile_layout.visibility = View.GONE
                    new_password_layout.visibility = View.VISIBLE
                    progress_layout.visibility = View.GONE
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        showMessage(getString(R.string.invalid_otp), null, true)
                    }
                    storedVerificationId = ""
                    progress_layout.visibility = View.GONE
                }
            }
    }


    private fun verifyMobileNumber(mobileNo: String) {
        var mFirebaseDatabase: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(USER_TABLE_NAME)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {

                var count: Long = 0
                for (it in data.children) {
                    val d1 = it.getValue(ADUserDetails::class.java)
                    if (d1?.phoneNumber.equals(mobileNo)) {
                        val key = it.key
                        if (key != null) {
                            mFirebaseDatabase.child(key)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.e(TAG, "onDataChange called : ")
                                        if (snapshot.exists()) {
                                            var user = snapshot.getValue(ADUserDetails::class.java)
                                            if (user != null) {
                                                Log.e(TAG, " user : " + user?.emailAddress)
                                                userEmail = user.emailAddress
                                                userpassword = user.password
                                                requestOTP(mobileNo)
                                            }
                                        } else {
                                            showMessage(
                                                getString(R.string.num_not_registered),
                                                null,
                                                true
                                            )
                                            progress_layout.visibility = View.GONE
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e(TAG, "Error : " + error.message)
                                        progress_layout.visibility = View.GONE
                                    }
                                })
                        }
                        break
                    } else {
                        count++
                    }
                }
                if (count == data.childrenCount) {
                    progress_layout.visibility = View.GONE
                    showMessage(getString(R.string.num_not_registered), null, true)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                progress_layout.visibility = View.GONE
            }
        }

        mFirebaseDatabase.addListenerForSingleValueEvent(eventListener)

    }

    private fun requestOTP(mobile: String) {
        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber("+91" + mobile, 60, TimeUnit.SECONDS, this, callbacks)
    }

    override fun onStart() {
        super.onStart()
        val window = this.getWindow()
        window.setLayout(
            (getScreenDetails(false) * 0.9).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}