package android.adwait.com.login.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.dashboard.view.ADDashboardActivity
import android.adwait.com.registeration.view.ADRegistrationActivity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_presignup.*


class ADPreSignActivity : ADBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(android.adwait.com.R.layout.activity_presignup)


        sign_in_btn.setOnClickListener(View.OnClickListener {
            var login = Intent(applicationContext, ADLoginActivity::class.java)
            startToNextScreen(login, false, false)
        })

        sign_up_btn.setOnClickListener(View.OnClickListener {
            var register = Intent(applicationContext, ADRegistrationActivity::class.java)
            startToNextScreen(register, false, false)
        })

        skip_btn.setOnClickListener(View.OnClickListener {
            var dash = Intent(applicationContext, ADDashboardActivity::class.java)
            startToNextScreen(dash, true, false)
            finish()
        })

        be_our_elve.setPaintFlags(be_our_elve.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
    }
}