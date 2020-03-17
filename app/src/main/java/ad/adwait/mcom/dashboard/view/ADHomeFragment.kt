package ad.adwait.mcom.dashboard.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.CommonUtils
import and.com.polam.utils.MySharedPreference
import ad.adwait.mcom.R
import ad.adwait.mcom.admin.model.ADAddChildModel
import ad.adwait.mcom.admin.model.ADMoneySplitUp
import ad.adwait.mcom.dashboard.adapter.ADHomePageAdapter
import ad.adwait.mcom.my_mentee.view.ADMonthlySplitActivity
import ad.adwait.mcom.utils.ADBaseFragment
import ad.adwait.mcom.utils.ADCommonResponseListener
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ebanx.swipebtn.OnStateChangeListener
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.fragment_home.*

class ADHomeFragment : ADBaseFragment(), View.OnClickListener {

    private val TAG: String = "ADHomeFragment"
    private var splitData: ADMoneySplitUp = ADMoneySplitUp()

    companion object {
        private var homeFragment = ADHomeFragment()

        fun getInstance(): ADHomeFragment {
            if (homeFragment == null) {
                homeFragment = ADHomeFragment()
            }
            return homeFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_home, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_btn.setOnStateChangeListener(OnStateChangeListener {

            if (!(activity as ADDashboardActivity).mHasChild) {
                (activity as ADBaseActivity).showAlertDialog(
                    getString(R.string.no_child_mapped),
                    "",
                    "Ok",
                    null
                )
                return@OnStateChangeListener
            }
            if (it) {
                happy_icon.visibility = View.GONE
                if ((activity as ADBaseActivity).isLoggedInUser()) {
                    (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_DONATION, "")
                } else {
                    (activity as ADDashboardActivity).fireLogin()
                }
            } else {
                happy_icon.visibility = View.VISIBLE
            }
        })

        login_btn.setOnClickListener{
            (activity as ADDashboardActivity).fireLogin()
        }

        mentee_layout.setOnClickListener{
            if ((activity as ADDashboardActivity).mHasChild) {
                (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_MY_MENTEE, "")
            } else {
                (activity as ADBaseActivity).showAlertDialog(
                    getString(R.string.no_child_mapped),
                    "",
                    "Ok",
                    null
                )
            }
        }

        info_icon.setOnClickListener{

            if (splitData.toString().length == 0) {
            } else {
                val intent = Intent(activity, ADMonthlySplitActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("splitData", splitData)
                intent.putExtra("data", bundle)
                startActivity(intent)
            }
        }

        tile1.setOnClickListener(this)
        tile2.setOnClickListener(this)
        tile3.setOnClickListener(this)
        tile4.setOnClickListener(this)
        tile5.setOnClickListener(this)
        tile6.setOnClickListener(this)
        tile_1_text.setText(CommonUtils.getHtmlText(getString(R.string.h_club)))

        val handler = Handler(Looper.getMainLooper())

        handler.post(Runnable {
            (activity as ADDashboardActivity).checkData()
        })

    }

    fun populateChildData(data: DataSnapshot, monthYr: String, childId: String) {

        if ((activity as ADBaseActivity).isLoggedInUser()) {
            showHideGuestLayou(false)
        } else {
            showHideGuestLayou(true)
        }
        val childData = data.getValue(ADAddChildModel::class.java)!!
        child_name?.setText(childData.childName)
        val imageUrl = childData.childImage

        if (!imageUrl.isEmpty()) {
            Glide.with(context).load(imageUrl)
                .placeholder(R.drawable.ic_guest_user).diskCacheStrategy(
                    DiskCacheStrategy.SOURCE
                ).into(child_image)
        }
        val dob = childData.dateOfBirth

        val age: String =
            (activity as ADBaseActivity).getAge(dob, "dd-MM-yyyy").toString()
        child_age?.setText(age + " Years")

        val monthlyAmount = childData.amountNeeded.toString()

        var collectedAmount =
            data.child("contribution").child(monthYr).child("collected_amt")
                .value.toString()

        if (collectedAmount.isEmpty() || collectedAmount.equals("null")) {
            collectedAmount = "0"
        }

        if (monthlyAmount.toInt() > 0 && monthlyAmount.toInt() == collectedAmount.toInt()) {
            MySharedPreference((activity as ADBaseActivity).applicationContext).saveStrings(
                getString(R.string.previous_month_yr), monthYr
            )

            (activity as ADBaseActivity).getNextDate(
                ad.adwait.mcom.utils.ADConstants.KEY_GET_NEXT_MONTH_YR, monthYr,
                object : ADCommonResponseListener {
                    override fun onSuccess(data: Any?) {
                        MySharedPreference((activity as ADBaseActivity).applicationContext).saveStrings(
                            getString(R.string.month_yr), data.toString()
                        )
                        (activity as ADDashboardActivity).fetchChildData(childId, data.toString(),false)
                    }

                    override fun onError(data: Any?) {
                    }
                })
            return
        }

        val text = java.lang.String.format(
            getString(R.string.fund_raised_msg),
            collectedAmount,
            monthlyAmount, monthYr
        )
        fund_details?.setText(text)
        splitData = childData.splitDetails

        if (!collectedAmount.isEmpty() && collectedAmount != null && !collectedAmount.equals(
                "null"
            ) &&
            monthlyAmount != null && !monthlyAmount.equals("null") && monthlyAmount.toInt() > 0
        ) {
            val percent =
                ((collectedAmount.toInt()) * 100 / monthlyAmount.toInt())
            progress.setProgress(percent)
        }

        (activity as ADDashboardActivity).hideProgress()

    }

    fun populateBanner(data: DataSnapshot) {
        var dataUrl: ArrayList<String> = ArrayList<String>()
        for (child in data.children) {
            dataUrl.add(child.value as String)
        }

        val homeAdapter =
            ADHomePageAdapter((activity as ADBaseActivity).applicationContext, dataUrl)
        home_pager.adapter = homeAdapter
        home_indicator.setViewPager(home_pager)
        homeAdapter.registerDataSetObserver(home_indicator.dataSetObserver)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.tile1 -> {
                (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_HX_CLUB, "")
            }
            R.id.tile2 -> {
                if (!(activity as ADDashboardActivity).mHasChild) {
                    (activity as ADBaseActivity).showAlertDialog(
                        getString(R.string.no_child_mapped),
                        "",
                        "Ok",
                        null
                    )
                    return
                }
                (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_MY_MENTEE, "")

            }
            R.id.tile3 -> {
                if (!(activity as ADDashboardActivity).mHasChild) {
                    (activity as ADBaseActivity).showAlertDialog(
                        getString(R.string.no_child_mapped),
                        "",
                        "Ok",
                        null
                    )
                    return
                }
                (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_WISH_CORNER, "")
            }
            R.id.tile4 -> {
                (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_OUR_PARTNERS, "")
            }
            R.id.tile5 -> {
                if (!(activity as ADDashboardActivity).mHasChild) {
                    (activity as ADBaseActivity).showAlertDialog(
                        getString(R.string.no_child_mapped),
                        "",
                        "Ok",
                        null
                    )
                    return
                }
                (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_BE_THE_CHANGE, "")
            }
            R.id.tile6 -> {
                if (!(activity as ADDashboardActivity).mHasChild) {
                    (activity as ADBaseActivity).showAlertDialog(
                        getString(R.string.no_child_mapped),
                        "",
                        "Ok",
                        null
                    )
                    return
                }
                (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_OUR_CAUSE, "")
            }
        }
    }

    fun showHideGuestLayou(isShowGuest: Boolean) {
        if (isShowGuest) {
            child_details_layout.visibility = View.GONE
            guest_layout.visibility = View.VISIBLE
        } else {
            child_details_layout.visibility = View.VISIBLE
            guest_layout.visibility = View.GONE
        }
    }
}