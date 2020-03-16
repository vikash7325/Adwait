package ad.adwait.mcom.profile.view

import ad.adwait.mcom.R
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_adterms.*
import kotlinx.android.synthetic.main.common_toolbar.*

class ADTermsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adterms)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        action_profile.visibility = View.GONE
        action_menu.visibility = View.GONE
        webview.loadUrl("file:///android_asset/terms.html");

    }

}
