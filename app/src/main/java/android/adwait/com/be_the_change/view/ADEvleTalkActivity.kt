package android.adwait.com.be_the_change.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.utils.ADConstants
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.View
import kotlinx.android.synthetic.main.activity_evle_talk.*
import kotlinx.android.synthetic.main.common_toolbar.*

class ADEvleTalkActivity : ADBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evle_talk)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        pledge_submit.setOnClickListener(View.OnClickListener {
            finish()
        })

        action_menu.setOnClickListener(View.OnClickListener {
            setResult(ADConstants.KEY_MENU)
            finish()
        })

        action_profile.setOnClickListener(View.OnClickListener {
            setResult(ADConstants.KEY_PROFILE)
            finish()
        })
    }
}