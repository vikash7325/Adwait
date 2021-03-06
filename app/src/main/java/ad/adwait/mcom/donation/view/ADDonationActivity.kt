package ad.adwait.mcom.donation.view

import and.com.polam.utils.ADBaseActivity
import ad.adwait.mcom.R
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.View

class ADDonationActivity : ADBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.donation_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        if (intent != null && intent.hasExtra("amount")) {
            var bundle = Bundle();
            bundle.putInt("amount", intent.getIntExtra("amount", 0))
            val adDonationFragment = ADDonationFragment()
            adDonationFragment.arguments = bundle
            addOrReplaceFragment(false, R.id.donation_container, adDonationFragment, "")
        }
    }
}