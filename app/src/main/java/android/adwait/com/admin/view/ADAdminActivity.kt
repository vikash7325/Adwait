package android.adwait.com.admin.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.wish_corner.view.ADAddWishesActivity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import com.example.myapplication.admin.adapter.AdSectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_admin.*

class ADAdminActivity : ADBaseActivity() {

    private var mIsFabShown = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val sectionsPagerAdapter =
            AdSectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        hideFABMenu()
        fab.setOnClickListener { view ->
            if (mIsFabShown) {
                hideFABMenu()
            } else {
                showFABMenu()
            }
        }

        wish_fab.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, ADAddWishesActivity::class.java))
            hideFABMenu()
        })
        child_fab.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, ADAddChildActivity::class.java))
            hideFABMenu()
        })
    }

    private fun hideFABMenu() {
        mIsFabShown = false
        wish_fab.animate().translationY(resources.getDimension(R.dimen.standard_105))
        child_fab.animate().translationY(resources.getDimension(R.dimen.standard_105))
    }

    private fun showFABMenu() {
        mIsFabShown = true
        wish_fab.animate().translationY(0f)
        child_fab.animate().translationY(0f)
    }

}
