package android.adwait.com.startup.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.admin.view.ADAdminActivity
import android.adwait.com.dashboard.view.ADDashboardActivity
import android.adwait.com.login.view.ADLoginActivity
import android.adwait.com.login.view.ADPreSignActivity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*

class ADSplashActivity : ADBaseActivity() {
    private val splashTime: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        var myHandler = MyHandler(this)
        if (!isNetworkAvailable()) {

            showMessage(getString(R.string.no_internet),splash_parent,true)
        } else {
            myHandler.postDelayed(Runnable() {
                kotlin.run {
                    var dash = Intent(applicationContext, ADAdminActivity::class.java)

                    var pref = MySharedPreference(applicationContext)

                    if (!pref.getValueBoolean(getString(R.string.help_screen))) {
                        dash = Intent(applicationContext, ADAdminActivity::class.java)
                    } else if (!pref.getValueBoolean(getString(R.string.registered))) {
                        dash = Intent(applicationContext, ADAdminActivity::class.java)
                    } else if (!isLoggedInUser()) {
                        dash = Intent(applicationContext, ADAdminActivity::class.java)
                    } else if (isLoggedInUser()) {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user == null) {
                            dash = Intent(applicationContext, ADAdminActivity::class.java)
                        }
                    }
                    startToNextScreen(dash, true, true)
                    finish()
                }
            }, splashTime)
        }
    }
}