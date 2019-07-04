package and.com.polam.utils

import android.adwait.com.R
import android.adwait.com.login.view.ADLoginActivity
import android.adwait.com.utils.ADBaseFragment
import android.adwait.com.utils.ADCommonResponseListener
import android.adwait.com.utils.ADViewClickListener
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.functions.FirebaseFunctions
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

open class ADBaseActivity : AppCompatActivity() {

    val USER_TABLE_NAME: String = "Elve_details";
    val CHILD_TABLE_NAME: String = "Children_Details";
    val CONTRIBUTION_TABLE_NAME: String = "Contribution";
    private val TAG: String = "ADBaseActivity";
    private lateinit var auth: FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAuth = FirebaseAuth.getInstance()

        auth = FirebaseAuth.AuthStateListener { firebaseAuth ->

            var user = FirebaseAuth.getInstance().currentUser

            if (user == null) {

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
                googleSignInClient.signOut()
                LoginManager.getInstance().logOut()
                MySharedPreference(applicationContext).saveBoolean(getString(R.string.logged_in), false)
                MySharedPreference(applicationContext).saveStrings(getString(R.string.userId), "")
                var login = Intent(applicationContext, ADLoginActivity::class.java)
                login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                mFirebaseAuth.removeAuthStateListener(auth)
                startActivity(login)
                finish()
            }
        }

    }

    public fun getUserDetails(listener: ValueEventListener): Boolean {
        val preference = MySharedPreference(applicationContext)
        if (!isNetworkAvailable()) {
            return false
        }

        if (isLoggedInUser()) {
            val userId = preference.getValueString(getString(R.string.userId)).toString()
            val mUserDataTable =
                FirebaseDatabase.getInstance().reference.child(USER_TABLE_NAME).child(userId)

            mUserDataTable.addListenerForSingleValueEvent(listener)
        }
        return true
    }

    public fun getChildDetails(childId: String, listener: ValueEventListener): Boolean {
        if (!isNetworkAvailable()) {
            return false
        }

        if (isLoggedInUser()) {
            val mChildDataTable =
                FirebaseDatabase.getInstance().reference.child(CHILD_TABLE_NAME).child(childId)

            mChildDataTable.addListenerForSingleValueEvent(listener)
        }
        return true
    }

    public fun isLoggedInUser(): Boolean {
        return MySharedPreference(applicationContext).getValueBoolean(getString(R.string.logged_in))
    }

    public fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    public fun startToNextScreen(intent: Intent, startFresh: Boolean, isAnimate: Boolean) {
        if (startFresh) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        if (isAnimate) {
            this.overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }
    }

    public fun addOrReplaceFragment(isAdd: Boolean, id: Int, fragment: ADBaseFragment, addToStack: String) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        if (isAdd) {
            transaction.add(id, fragment)
        } else {
            transaction.replace(id, fragment)
        }

        if (!addToStack.isEmpty()) {
            transaction.addToBackStack(addToStack)
        }

        transaction.commit()
    }

    public fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    public fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    public fun isValidPhone(phone: String): Boolean {
        val mobile = "^[6-9][0-9]{9}$"
        val pat = Pattern.compile(mobile)
        return pat.matcher(phone).matches()
    }

    public fun isValidContactDetails(contact: String): Boolean {
        var isValid = false
        val email = "^[A-Za-z][A-Za-z0-9]*([._-]?[A-Za-z0-9]+)@[A-Za-z].[A-Za-z]{0,3}?.[A-Za-z]{0,2}$"
        val emailPat = Pattern.compile(email)

        val  mobile= "^[6-9][0-9]{9}$"
        val mobilePat = Pattern.compile(mobile)

        val  mobile1= "[0-9]+"
        val mobilePat2 = Pattern.compile(mobile1)
        if (mobilePat2.matcher(contact).matches()) {
            if (mobilePat.matcher(contact).matches()) {
                isValid = true
            }
        } else if (emailPat.matcher(contact).matches()) {
            isValid = true
        }
        return isValid
    }

    public fun getScreenDetails(isHeight: Boolean): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

        if (isHeight) {
            return height
        } else {
            return width
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    public fun showMessage(msg: String, view: View?, isError: Boolean) {

        if (view == null) {
            Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
        } else {
            val snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null)

            if (isError) {
                val sbv = snackBar.view
                sbv.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }
            snackBar.show()
        }
    }

    public fun registerLogoutListener() {
        mFirebaseAuth.addAuthStateListener(auth)
    }

    public fun getReferralString(): String {
        val characters: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val referral = StringBuilder()
        val rnd = Random()
        while (referral.length < 8) { // length of the random string.
            val index = characters.get(rnd.nextInt(characters.length))
            referral.append(index)
        }
        return referral.toString()
    }

    fun getAge(dobString: String, format: String): Int {

        var date: Date? = null
        val sdf = SimpleDateFormat(format)
        try {
            date = sdf.parse(dobString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (date == null) return 0

        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.time = date

        val year = dob.get(Calendar.YEAR)
        val month = dob.get(Calendar.MONTH)
        val day = dob.get(Calendar.DAY_OF_MONTH)

        dob.set(year, month + 1, day)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    fun showAlertDialog(message: String, btnPositive: String, btnNegative: String, listener: ADViewClickListener) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage(message)

        builder.setPositiveButton(btnPositive) { dialog, which ->
            listener.onViewClicked()
        }
        builder.setNegativeButton(btnNegative) { dialog, which ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun getServerDate(functionName: String, listener : ADCommonResponseListener?=null): String {
        var serverDate: String = ""
        FirebaseFunctions.getInstance().getHttpsCallable(functionName).call()
            .addOnSuccessListener {
                if (functionName.equals("getCurrentDate")) {
                    serverDate = it.data.toString()
                    MySharedPreference(this).saveStrings(getString(R.string.current_date), serverDate)
                } else {
                    serverDate = it.data.toString()
                    MySharedPreference(this).saveStrings(getString(R.string.month_yr), serverDate)
                }
                if (listener!=null) {
                    listener.onSuccess(serverDate)
                }
            }
            .addOnFailureListener {
                Log.v(TAG, "getServerDate Exception -> " + it.message)
                if (listener!=null) {
                    listener.onError(it)
                }
            }

        return serverDate
    }
}
