package ad.adwait.mcom.static_pages.view

import and.com.polam.utils.ADBaseActivity
import ad.adwait.mcom.R
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.View
import kotlinx.android.synthetic.main.common_toolbar.*

class ADOurCauseActivity : ADBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_our_causes)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        action_menu.setOnClickListener(View.OnClickListener {
            setResult(ad.adwait.mcom.utils.ADConstants.KEY_MENU)
            finish()
        })

        action_profile.setOnClickListener(View.OnClickListener {
            setResult(ad.adwait.mcom.utils.ADConstants.KEY_PROFILE)
            finish()
        })
    }

}