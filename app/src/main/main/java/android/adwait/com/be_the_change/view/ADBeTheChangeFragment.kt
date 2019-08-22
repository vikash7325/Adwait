package android.adwait.com.be_the_change.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.dashboard.adapter.ADHomePageAdapter
import android.adwait.com.utils.ADBaseFragment
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_be_the_change.*
import android.graphics.Paint


class ADBeTheChangeFragment : ADBaseFragment(), View.OnClickListener {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_be_the_change, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spannedString = SpannableString(getString(R.string.be_our_elve))
        val red = ForegroundColorSpan(Color.RED)

        spannedString.setSpan(red, 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannedString.setSpan(StrikethroughSpan(), 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanned_text.setText(spannedString)
        header_1.setPaintFlags(header_1.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

        val baseActivity = activity as ADBaseActivity
        val height: Int = (baseActivity.getScreenDetails(true) * 0.15).toInt()
        val pagerHeight: Int = (baseActivity.getScreenDetails(true) * 0.25).toInt()

        var params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)

        tile_layout_1.layoutParams = params
        tile_layout_2.layoutParams = params

        params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pagerHeight)
        bethe_change_pager.layoutParams = params

        val data =
            arrayOf(R.drawable.home_banner_1, R.drawable.home_banner_2, R.drawable.superhero_kids)

        val pageAdapter = ADHomePageAdapter((activity as ADBaseActivity).applicationContext, data)
        bethe_change_pager.adapter = pageAdapter
        change_indicator.setViewPager(bethe_change_pager)
        pageAdapter.registerDataSetObserver(change_indicator.dataSetObserver)

        tile1.setOnClickListener(this)
        tile2.setOnClickListener(this)
        tile3.setOnClickListener(this)
        tile4.setOnClickListener(this)
        tile5.setOnClickListener(this)
        tile6.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var nextScreen: Intent = Intent()
        when (v?.id) {
            R.id.tile1 -> {
                nextScreen = Intent(activity, ADPledgeBdayActivity::class.java)
            }
            R.id.tile2 -> {
                nextScreen = Intent(activity, ADOrganizeEventActivity::class.java)
            }
            R.id.tile3 -> {
                nextScreen = Intent(activity, ADEvleTalkActivity::class.java)
            }
            R.id.tile4 -> {
                nextScreen = Intent(activity, ADCampusambassadorActivity::class.java)
            }
            R.id.tile5 -> {
                //  nextScreen = Intent(activity, ADPledgeBdayActivity::class.java)
                return
            }
            R.id.tile6 -> {
                nextScreen = Intent(activity, ADAdwaitsDayOutActivity::class.java)
            }
        }
        startActivity(nextScreen)
    }
}