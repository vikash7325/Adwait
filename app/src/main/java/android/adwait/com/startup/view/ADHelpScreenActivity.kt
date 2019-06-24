package android.adwait.com.startup.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.login.view.ADPreSignActivity
import android.adwait.com.startup.adapter.ADHelpPagerAdapter
import android.adwait.com.startup.model.ADHelpPagerModel
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.activity_help_screen.*

class ADHelpScreenActivity : ADBaseActivity() {

    private var mCurrentPage: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_screen)


        var data: MutableList<ADHelpPagerModel> = mutableListOf<ADHelpPagerModel>()

        var dataObj = ADHelpPagerModel()
        dataObj.setImage_drawables(R.drawable.donate_befikar)
        dataObj.setHeader("#DonateBefikar")
        dataObj.setTextData(getString(R.string.help1))
        data.add(dataObj)

        dataObj = ADHelpPagerModel()
        dataObj.setImage_drawables(R.drawable.care_beyond_horizons)
        dataObj.setHeader("#CareBeyondHorizons")
        dataObj.setTextData(getString(R.string.help2))
        data.add(dataObj)

        dataObj = ADHelpPagerModel()
        dataObj.setImage_drawables(R.drawable.hassi_banirahe)
        dataObj.setHeader("#HassiBanirahe")
        dataObj.setTextData(getString(R.string.help3))
        data.add(dataObj)

        dataObj = ADHelpPagerModel()
        dataObj.setImage_drawables(R.drawable.unbox)
        dataObj.setHeader("#UnboxTheBlackbox")
        dataObj.setTextData(getString(R.string.help4))
        data.add(dataObj)


        var myAdapter = ADHelpPagerAdapter(applicationContext, data)
        pager.adapter = myAdapter;

        skip.setOnClickListener(View.OnClickListener {

            var pref = MySharedPreference(applicationContext)
            pref.saveBoolean(getString(R.string.help_screen), true)

            var dash = Intent(applicationContext, ADPreSignActivity::class.java)
            startToNextScreen(dash, true,false)
            finish()
        })

        next.setOnClickListener(View.OnClickListener {
            if(mCurrentPage>=4){
             skip.performClick()
            }else {
                pager.setCurrentItem(mCurrentPage, true)
            }
        })

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mCurrentPage = position + 1
            }
        })

        indicator.setViewPager(pager)
        myAdapter.registerDataSetObserver(indicator.dataSetObserver)
    }
}