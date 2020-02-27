package ad.adwait.mcom.be_the_change.view

import and.com.polam.utils.ADBaseActivity
import ad.adwait.mcom.R
import ad.adwait.mcom.dashboard.adapter.ADHomePageAdapter
import ad.adwait.mcom.utils.ADBaseFragment
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_be_the_change.*


class ADBeTheChangeFragment : ADBaseFragment(), View.OnClickListener {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        tile1.setOnClickListener(this)
        tile2.setOnClickListener(this)
        tile3.setOnClickListener(this)
        tile4.setOnClickListener(this)
        tile5.setOnClickListener(this)
        tile6.setOnClickListener(this)

        fetchBanner()
        header_1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            startActivity(intent)
        }
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
        activity?.startActivityForResult(nextScreen, ad.adwait.mcom.utils.ADConstants.KEY_REQUEST)
    }

    private fun fetchBanner() {

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.no_internet),
                change_parent,
                true
            )
            return
        }

        (activity as ADBaseActivity).getBannerDetails(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {
                if (data.exists()) {
                    var dataUrl: ArrayList<String> = ArrayList<String>()
                    for (child in data.children) {
                        dataUrl.add(child.value as String)
                    }

                    val pageAdapter = ADHomePageAdapter((activity as ADBaseActivity).applicationContext, dataUrl)
                    bethe_change_pager.adapter = pageAdapter
                    change_indicator.setViewPager(bethe_change_pager)
                    pageAdapter.registerDataSetObserver(change_indicator.dataSetObserver)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("ADBeTheChangeFragment", "Banner fetch Error : " + error.message)
            }
        },"Be_The_Change_Banner")
    }


}