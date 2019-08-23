package android.adwait.com.admin.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.login.view.ADLoginActivity
import android.adwait.com.wish_corner.view.ADAddWishesActivity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import com.example.myapplication.admin.adapter.AdSectionsPagerAdapter
import com.example.myapplication.admin.view.ADFragmentChild
import com.example.myapplication.admin.view.ADFragmentWishes
import kotlinx.android.synthetic.main.activity_admin.*

class ADAdminActivity : ADBaseActivity() {

    private var mIsFabShown = false
    private lateinit var sectionsPagerAdapter: AdSectionsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        sectionsPagerAdapter =
            AdSectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        logout.setOnClickListener(View.OnClickListener {
            MySharedPreference(applicationContext).saveBoolean(getString(R.string.superAdmin),false)
            MySharedPreference(applicationContext).saveBoolean(getString(R.string.logged_in), false)
            MySharedPreference(applicationContext).saveStrings(getString(R.string.userId), "")
            var login = Intent(applicationContext, ADLoginActivity::class.java)
            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(login)
            finish()})

        hideFABMenu()
        fab.setOnClickListener { view ->
            if (mIsFabShown) {
                hideFABMenu()
            } else {
                showFABMenu()
            }
        }

        wish_fab.setOnClickListener(View.OnClickListener {
            startActivityForResult(Intent(this, ADAddWishesActivity::class.java), 1235)
            hideFABMenu()
        })
        child_fab.setOnClickListener(View.OnClickListener {
            startActivityForResult(Intent(this, ADAddChildActivity::class.java), 1235)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1235) {
            if (resultCode == Activity.RESULT_OK) {

                var childFragment = sectionsPagerAdapter.getRegisteredFragment(0) as ADFragmentChild
                childFragment.getData()
                var wishFragment = sectionsPagerAdapter.getRegisteredFragment(1) as ADFragmentWishes
                wishFragment.getData()

            }
        }
    }

}
