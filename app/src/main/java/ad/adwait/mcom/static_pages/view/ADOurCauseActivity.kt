package ad.adwait.mcom.static_pages.view

import ad.adwait.mcom.R
import and.com.polam.utils.ADBaseActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_our_causes.*
import kotlinx.android.synthetic.main.common_toolbar.*

class ADOurCauseActivity : ADBaseActivity(), View.OnClickListener {


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
        click_for_more1.setOnClickListener(this)
        click_for_more2.setOnClickListener(this)
        click_for_more3.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.adwait.com"))
        startActivity(intent)
    }

}