package ad.adwait.mcom.login.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.CommonUtils
import and.com.polam.utils.MySharedPreference
import ad.adwait.mcom.R
import ad.adwait.mcom.admin.view.ADAdminActivity
import ad.adwait.mcom.dashboard.view.ADDashboardActivity
import ad.adwait.mcom.registeration.model.ADUserDetails
import ad.adwait.mcom.registeration.view.ADRegistrationActivity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class ADLoginActivity : ADBaseActivity() {

    private val TAG: String = "ADLoginActivity"
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: DatabaseReference
    private val RC_SIGN_IN = 2345
    private lateinit var mCallBackListener: CallbackManager
    private var mUserId: String = ""
    private lateinit var mProgressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mProgressDialog = showProgressDialog("", false)

        mFirebaseAuth = FirebaseAuth.getInstance()

        mCallBackListener = CallbackManager.Factory.create()

        sign_up_btn.setOnClickListener(View.OnClickListener {
            var register = Intent(applicationContext, ADRegistrationActivity::class.java)
            startToNextScreen(register, false, false)
        })

        skip_btn.setOnClickListener(View.OnClickListener {
            launchHomeScreen(false, "")
        })

        forget_password.setOnClickListener(View.OnClickListener {
            var forgot = Intent(applicationContext, ADForgetActivity::class.java)
            startActivity(forgot)
        })

        password.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                login_btn.performClick()
                return@OnKeyListener true
            }
            false
        })

        login_btn.setOnClickListener(View.OnClickListener {

            var name = username.text.toString().trim()
            var pass = password.text.toString().trim()

            login_btn.hideKeyboard()

            if (TextUtils.isEmpty(name)) {
                username.setError(getString(R.string.empty_username));
            } else if (TextUtils.isEmpty(pass)) {
                password.setError(getString(R.string.empty_password));
            } else if (!isValidPassword(pass)) {
                password.setError(getString(R.string.err_invalid_password));
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), login_parent, true)
                    return@OnClickListener
                }
                 mProgressDialog.show()
                if (name.equals(ad.adwait.mcom.utils.ADConstants.SUPER_ADMIN_NAME) && pass.equals(ad.adwait.mcom.utils.ADConstants.SUPER_ADMIN_PASS)) {
                    hideProgress(mProgressDialog)
                    var admin = Intent(applicationContext, ADAdminActivity::class.java)
                    MySharedPreference(applicationContext).saveBoolean(
                        getString(R.string.superAdmin),
                        true
                    )
                    startToNextScreen(admin, true, false)
                    finish()
                    return@OnClickListener
                }

                mFirebaseAuth.signInWithEmailAndPassword(name, pass)
                    .addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {

                            val user = mFirebaseAuth.currentUser

                            if (user != null) {
                                mFirebaseDatabase = FirebaseDatabase.getInstance().reference

                                mFirebaseDatabase.child(USER_TABLE_NAME).child(user.uid)
                                    .child("password")
                                    .setValue(pass)
                                    .addOnSuccessListener {
                                        launchHomeScreen(true, user.uid)
                                    }.addOnFailureListener {
                                        showMessage(
                                            "Not updating due to some issue" + it.message,
                                            login_parent,
                                            true
                                        )
                                        hideProgress(mProgressDialog)
                                    }
                            }
                        } else {
                            showMessage(getString(R.string.invalid_user_pass), login_parent, true)
                            hideProgress(mProgressDialog)
                        }
                    }
            }
        })

        google.setOnClickListener(View.OnClickListener {
            if (!isNetworkAvailable()) {
                showMessage(getString(R.string.no_internet), login_parent, true)
                return@OnClickListener
            }
            startGoogleSignin()
        })

        username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isValidEmail(s.toString())) {
                    username.error = getString(R.string.err_invalid_email)
                } else {
                    username.error = null
                }
            }
        })


//        facebook_btn.setReadPermissions(Arrays.asList("email"))
//
//        facebook_btn.registerCallback(mCallBackListener, object : FacebookCallback<LoginResult> {
//            override fun onSuccess(loginResult: LoginResult) {
//                Log.d(TAG, "facebook:onSuccess:$loginResult")
//                handleFacebookAccessToken(loginResult.accessToken)
//            }
//
//            override fun onCancel() {
//                Log.d(TAG, "facebook:onCancel")
//            }
//
//            override fun onError(error: FacebookException) {
//                Log.d(TAG, "facebook:onError", error)
//            }
//        })


        done_btn.setOnClickListener(View.OnClickListener {
            launchHomeScreen(true, mUserId)
        })

        var googleTextView = google.getChildAt(0) as TextView

        googleTextView.text = "Login with Google"
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        if (!isNetworkAvailable()) {
            showMessage(getString(R.string.no_internet), login_parent, true)
            return
        }
        val credential = FacebookAuthProvider.getCredential(token.token)
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mFirebaseAuth.currentUser
                    if (user != null) {
                        var userRegister = ADUserDetails(
                            user.displayName.toString(),
                            "",
                            "",
                            user.email.toString(),
                            ""
                        )
                        checkUserInDb(userRegister, user.uid)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showMessage(getString(R.string.login_failed), login_parent, true)
                    LoginManager.getInstance().logOut();
                }
            }
    }

    private fun startGoogleSignin() {
         mProgressDialog.show()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        hideProgress(mProgressDialog)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
         mProgressDialog.show()

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mFirebaseAuth.currentUser
                    if (user != null) {
                        var userRegister = ADUserDetails(
                            user.displayName.toString(),
                            "",
                            "",
                            user.email.toString(),
                            ""
                        )
                        checkUserInDb(userRegister, user.uid)
                    } else {
                        hideProgress(mProgressDialog)
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showMessage(getString(R.string.login_failed), login_parent, true)
                    hideProgress(mProgressDialog)
                }

            }
    }

    /**
     * Method to register user data
     */
    fun checkUserInDb(user: ADUserDetails, userId: String) {

        val mUserDataTable =
            FirebaseDatabase.getInstance().reference.child(USER_TABLE_NAME).child(userId)

        mUserDataTable.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {

                if (data.exists()) {
                    hideProgress(mProgressDialog)
                    mUserId = userId
                    launchHomeScreen(true, userId)
                } else {
                    mFirebaseDatabase = FirebaseDatabase.getInstance().reference
                    user.myReferralCode = getReferralString()
                    mFirebaseDatabase.child(USER_TABLE_NAME).child(userId).setValue(user)
                        .addOnSuccessListener {
                            login_layout.visibility = View.GONE
                            congrats_layout.visibility = View.VISIBLE
                            hideProgress(mProgressDialog)
                            var con: String = getString(R.string.congrats_1)
                            skip_btn.visibility = View.INVISIBLE

                            val hint = java.lang.String.format(con, user.userName)
                            congrats_text.text = CommonUtils.getHtmlText(hint)
                            mUserId = userId
                            MySharedPreference(applicationContext).saveStrings(
                                getString(R.string.userId),
                                userId
                            )
                            MySharedPreference(applicationContext).saveBoolean(
                                getString(R.string.logged_in),
                                true
                            )
                            MySharedPreference(applicationContext).saveBoolean(
                                getString(R.string.registered),
                                true
                            )
                            celebration_view.visibility = View.VISIBLE
                            celebration_view.build()
                                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                                .setDirection(0.0, 359.0)
                                .setSpeed(1f, 5f)
                                .setFadeOutEnabled(true)
                                .setTimeToLive(ad.adwait.mcom.utils.ADConstants.ANIMATION_TIME_TO_LIVE)
                                .addShapes(Shape.RECT, Shape.CIRCLE)
                                .addSizes(Size(ad.adwait.mcom.utils.ADConstants.ANIMATION_SIZE))
                                .setPosition(-50f, celebration_view.width + 50f, -50f, -50f)
                                .streamFor(
                                    ad.adwait.mcom.utils.ADConstants.ANIMATION_COUNT,
                                    ad.adwait.mcom.utils.ADConstants.ANIMATION_EMITTING_TIME
                                )
                        }
                        .addOnFailureListener {
                            showMessage(getString(R.string.registration_failed), login_parent, true)
                            hideProgress(mProgressDialog)
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showMessage(getString(R.string.registration_failed), login_parent, true)
                hideProgress(mProgressDialog)
            }

        })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        } else {
            mCallBackListener.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun launchHomeScreen(values: Boolean, userId: String) {
        var pre = MySharedPreference(applicationContext)
        pre.saveBoolean(getString(R.string.logged_in), values)
        pre.saveBoolean(getString(R.string.registered), values)
        pre.saveStrings(getString(R.string.userId), userId)
        var dash = Intent(applicationContext, ADDashboardActivity::class.java)
        startToNextScreen(dash, true, false)
        finish()
    }
}