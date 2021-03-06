package and.com.polam.utils

import ad.adwait.mcom.R
import ad.adwait.mcom.login.view.ADLoginActivity
import ad.adwait.mcom.utils.ADBaseFragment
import ad.adwait.mcom.utils.ADCommonResponseListener
import ad.adwait.mcom.utils.ADViewClickListener
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

open class ADBaseActivity : AppCompatActivity() {

    val USER_TABLE_NAME: String = "Elve_details";
    val CHILD_TABLE_NAME: String = "Children_Details";
    val CONTRIBUTION_TABLE_NAME: String = "Contribution";
    val SUBSCRIPTION_TABLE_NAME: String = "Subscriptions";
    val WISH_CORNER_TABLE_NAME: String = "Wish_Corner";
    val ROUTING_TABLE_NAME: String = "Routing_details";
    val WISH_EVENTS_TABLE_NAME: String = "Wish_Corner_Events";
    val BANNER_TABLE_NAME: String = "Banners";
    val REMOTE_CONFIG_TIME: Long = 3600

    private val TAG: String = "ADBaseActivity";
    private lateinit var auth: FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAuth = FirebaseAuth.getInstance()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        auth = FirebaseAuth.AuthStateListener { firebaseAuth ->

            var user = FirebaseAuth.getInstance().currentUser

            if (user == null) {

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
                googleSignInClient.signOut()
                LoginManager.getInstance().logOut()
                MySharedPreference(applicationContext).saveBoolean(
                    getString(R.string.logged_in),
                    false
                )
                MySharedPreference(applicationContext).saveStrings(getString(R.string.userId), "")
                MySharedPreference(applicationContext).saveStrings(
                    getString(R.string.subscription_id),
                    ""
                )
                var login = Intent(applicationContext, ADLoginActivity::class.java)
                login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                mFirebaseAuth.removeAuthStateListener(auth)
                startActivity(login)
                finish()
            }
        }

    }

    fun getUserDetails(listener: ValueEventListener): Boolean {
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

    fun getChildDetails(childId: String, listener: ValueEventListener): Boolean {
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


    fun getBannerDetails(listener: ValueEventListener, childName: String) {
        if (!isNetworkAvailable()) {
            return
        }

        val mUserDataTable =
            FirebaseDatabase.getInstance().reference.child(BANNER_TABLE_NAME).child(childName)

        mUserDataTable.addListenerForSingleValueEvent(listener)
    }

    fun isLoggedInUser(): Boolean {
        return MySharedPreference(applicationContext).getValueBoolean(getString(R.string.logged_in))
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    fun startToNextScreen(intent: Intent, startFresh: Boolean, isAnimate: Boolean) {
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

    fun addOrReplaceFragment(
        isAdd: Boolean,
        id: Int,
        fragment: ADBaseFragment,
        addToStack: String
    ) {
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

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        val mobile = "^[6-9][0-9]{9}$"
        val pat = Pattern.compile(mobile)
        return pat.matcher(phone).matches()
    }

    fun isValidContactDetails(contact: String): Boolean {
        var isValid = false
        val email = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$"
        val emailPat = Pattern.compile(email)

        val mobile = "^[6-9][0-9]{9}$"
        val mobilePat = Pattern.compile(mobile)

        val mobile1 = "[0-9]+"
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

    fun getScreenDetails(isHeight: Boolean): Int {
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        if (ev?.action == MotionEvent.ACTION_UP) {
            val view = currentFocus

            if (view is TextInputEditText) {
                val scrCoords = IntArray(2)
                view!!.getLocationOnScreen(scrCoords)
                val x = ev.getRawX() + view.left - scrCoords[0]
                val y = ev.getRawY() + view.top - scrCoords[1]
                if (ev.getAction() === MotionEvent.ACTION_UP && (x < view.left || x >= view.right || y < view.top || y > view.bottom)) {
                    view.hideKeyboard()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun showMessage(msg: String, view: View?, isError: Boolean) {

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

    fun registerLogoutListener() {
        mFirebaseAuth.addAuthStateListener(auth)
    }

    fun getReferralString(): String {
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

    fun showAlertDialog(
        message: String,
        btnPositive: String,
        btnNegative: String,
        listener: ADViewClickListener?
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage(message)

        if (!btnPositive.isEmpty()) {
            builder.setPositiveButton(btnPositive) { dialog, which ->
                if (listener != null) {
                    listener.onViewClicked()
                }
            }
        }
        if (!btnNegative.isEmpty()) {
            builder.setNegativeButton(btnNegative) { dialog, which ->
                dialog.dismiss()
            }
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun getServerDate(functionName: String, listener: ADCommonResponseListener? = null): String {
        var serverDate: String = ""

        FirebaseFunctions.getInstance().getHttpsCallable(functionName).call()
            .addOnSuccessListener {
                serverDate = it.data.toString()
                if (functionName.equals(ad.adwait.mcom.utils.ADConstants.KEY_GET_CURRENT_DATE)) {
                    MySharedPreference(this).saveStrings(
                        getString(R.string.current_date),
                        serverDate
                    )
                } else if (functionName.equals(ad.adwait.mcom.utils.ADConstants.KEY_GET_CURRENT_MONTH_YR)) {
                    MySharedPreference(this).saveStrings(getString(R.string.month_yr), serverDate)
                }

                if (listener != null) {
                    listener.onSuccess(serverDate)
                }
            }
            .addOnFailureListener {
                Log.v(TAG, "getServerDate Exception -> " + it.message)
                if (listener != null) {
                    listener.onError(it)
                }
            }

        return serverDate
    }

    fun getNextDate(
        functionName: String,
        date: String,
        listener: ADCommonResponseListener? = null
    ): String {
        var serverDate: String = ""

        val data = hashMapOf(
            "month" to changeData(date)
        )

        FirebaseFunctions.getInstance().getHttpsCallable(functionName).call(data)
            .addOnSuccessListener {
                serverDate = it.data.toString()
                if (functionName.equals(ad.adwait.mcom.utils.ADConstants.KEY_GET_PREVIOUS_MONTH_YR)) {
                    MySharedPreference(this).saveStrings(
                        getString(R.string.previous_month_yr),
                        serverDate
                    )
                } else {
                    MySharedPreference(this).saveStrings(
                        getString(R.string.next_month_yr),
                        serverDate
                    )
                }
                if (listener != null) {
                    listener.onSuccess(serverDate)
                }
            }
            .addOnFailureListener {
                Log.v(TAG, "getServerDate Exception -> " + it.message)
                if (listener != null) {
                    listener.onError(it)
                }
            }

        return serverDate
    }

    fun changeData(cdate: String): String {

        var fromFormat = SimpleDateFormat("dd-MMM-yyyy")
        val toFormat = SimpleDateFormat("yyyy-MM-dd")

        if (cdate.contains(",")) {
            fromFormat = SimpleDateFormat("MMMM,yyyy")
        }

        val currentDate = fromFormat.parse(cdate)

        val converted = toFormat.format(currentDate)
        Log.v(TAG, "converted -> " + cdate)
        Log.v(TAG, "converted -> " + converted)
        return converted
    }

    fun showProgressDialog(message: String, showChildMapping: Boolean): AlertDialog {

        var displayMessage = message
        val builder = AlertDialog.Builder(this)
        val view = View.inflate(applicationContext, R.layout.progress_dialog_layout, null)

        val normalLayout = view.findViewById<LinearLayout>(R.id.nrml_progress_bar)
        val mappingLayout = view.findViewById<LinearLayout>(R.id.child_mapping)
        val loading_text = view.findViewById<TextView>(R.id.loading_text)
        if (displayMessage == null || displayMessage.length == 0) {
            displayMessage = getString(R.string.loading)
        }
        if (showChildMapping) {
            normalLayout.visibility = View.GONE
            mappingLayout.visibility = View.VISIBLE
        } else {
            normalLayout.visibility = View.VISIBLE
            mappingLayout.visibility = View.GONE
        }

        loading_text.setText(displayMessage)
        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return alertDialog
    }

    fun hideProgress(mProgressDialog: AlertDialog) {
        if (mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
    }

    fun getPackageVersion(): String {
        val info = packageManager.getPackageInfo(packageName, 0)
        return info.versionName
    }

    fun checkAppVersion(listener: ADCommonResponseListener?) {

        var newVersion = ""
        var forceUpdate = false
        var remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(REMOTE_CONFIG_TIME)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    newVersion = remoteConfig.getString("new_version")
                    forceUpdate = remoteConfig.getBoolean("force_update")
                    if (compareVersionNames(getPackageVersion(), newVersion) > 0) {

                        if (forceUpdate) {
                            listener?.onSuccess(getString(R.string.new_version))
                        } else {
                            listener?.onError(getString(R.string.new_version))
                        }

                    } else {
                        listener?.onError(null)
                    }
                } else {
                    Log.d(TAG, "Config params Failed")
                }
            }
    }

    private fun compareVersionNames(oldVersionName: String, newVersionName: String): Int {
        var res = 0;

        var oldNumbers = oldVersionName.split(".");
        var newNumbers = newVersionName.split(".");

        // To avoid IndexOutOfBounds
        val maxIndex = Math.min(oldNumbers.size, newNumbers.size);

        for (i in 0..maxIndex - 1) {
            var oldVersionPart = Integer.valueOf(oldNumbers[i]);
            var newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = 1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = -1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.size != newNumbers.size) {
            res = 1;
        }

        return res;
    }

}
