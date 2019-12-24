package android.adwait.com.wish_corner.view

import android.adwait.com.R
import android.adwait.com.donation.view.ADDonationActivity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import kotlinx.android.synthetic.main.activity_wish_details.*

class AdWishDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_details)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        if (intent != null && intent.extras != null) {
            name?.setText(intent.getStringExtra("name"))

            var textFormatter =
                java.lang.String.format(getString(R.string.aim_hint), intent.getStringExtra("aim"))
            aim.setText(textFormatter)

            textFormatter =
                java.lang.String.format(getString(R.string.wish_hint), intent.getStringExtra("name"),intent.getStringExtra("gift"))
            wish.setText(textFormatter)

            textFormatter =
                java.lang.String.format(getString(R.string.hint_for_donor), intent.getStringExtra("name"))
            hint.setText(textFormatter)

            textFormatter = getString(R.string.rupees) + " " + intent.getIntExtra("amount",0)
            wish_amount.setText(textFormatter)
        }

        proceed_btn.setOnClickListener {
            var donation = Intent(this, ADDonationActivity::class.java)
            donation.putExtra("amount", intent.getIntExtra("amount",0))
            startActivity(donation)
            finish()
        }
    }
}
