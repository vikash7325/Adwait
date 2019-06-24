package android.adwait.com.login.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.registeration.model.ADUserDetails
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_forget_password)

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
                    showMessage(getString(R.string.no_internet), forget_parent,true)
                    return@OnClickListener
                }
                progressBar.visibility = View.VISIBLE
                verifyMobileNumber(mobile)
            }
        }
        )

        //OTP Submit
        otp_submit.setOnClickListener(View.OnClickListener {
            val otp = pinview.value.toString()
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, otp)
            if (TextUtils.isEmpty(otp)) {
                showMessage(getString(R.string.empty_pin), forget_parent,true)
            } else if (otp.length < 6) {
                showMessage(getString(R.string.invalid_pin), forget_parent,true)
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), forget_parent,true)
                    return@OnClickListener
                }
                progressBar.visibility = View.VISIBLE
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
                    showMessage(getString(R.string.no_internet), forget_parent,true)
                    return@OnClickListener
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userpassword)
                    .addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser

                            if (user != null) {
                                progressBar.visibility = View.VISIBLE

                                user.updatePassword(newPassword)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            showMessage(getString(R.string.mobile_reset_success), forget_parent,true)
                                            passwordReset()
                                        } else {
                                            progressBar.visibility = View.GONE
                                            showMessage(getString(R.string.unable_to_reset_password), forget_parent,true)
                                        }
                                    }
                            }
                        } else {
                            progressBar.visibility = View.GONE
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
                    showMessage(getString(R.string.no_internet), forget_parent,true)
                    return@OnClickListener
                }
                progressBar.visibility = View.VISIBLE
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailId)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            showMessage(getString(R.string.email_success), forget_parent,true)
                            passwordReset()
                        } else {
                            progressBar.visibility = View.GONE
                            showMessage(getString(R.string.unable_to_send_reset_mail), forget_parent,true)
                        }
                    }
            }
        })

        cancel.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    private fun passwordReset() {
        progressBar.visibility = View.GONE
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

        override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d(TAG, "onCodeSent:" + verificationId!!)
            storedVerificationId = verificationId
            showMessage(getString(R.string.otp_sent), forget_parent,false)
            progressBar.visibility = View.GONE
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    var user = FirebaseAuth.getInstance().currentUser
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success" + user!!.displayName)

                    mobile_layout.visibility = View.GONE
                    new_password_layout.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        showMessage(getString(R.string.invalid_otp), forget_parent,true)
                    }
                    progressBar.visibility = View.GONE
                }
            }
    }


    private fun verifyMobileNumber(mobileNo: String) {
        var mFirebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference(USER_TABLE_NAME)

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
                                            showMessage(getString(R.string.num_not_registered), forget_parent,true)
                                            progressBar.visibility = View.GONE
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e(TAG, "Error : " + error.message)
                                        progressBar.visibility = View.GONE
                                    }
                                })
                        }
                        break
                    } else {
                        count++
                    }
                }
                if (count == data.childrenCount) {
                    progressBar.visibility = View.GONE
                    showMessage(getString(R.string.num_not_registered), forget_parent,true)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                progressBar.visibility = View.GONE
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
        window.setLayout((getScreenDetails(false) * 0.9).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
    }
}