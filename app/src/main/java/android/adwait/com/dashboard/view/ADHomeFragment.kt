package android.adwait.com.dashboard.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.CommonUtils
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.admin.model.ADAddChildModel
import android.adwait.com.admin.model.ADMoneySplitUp
import android.adwait.com.dashboard.adapter.ADHomePageAdapter
import android.adwait.com.my_mentee.view.ADMonthlySplitActivity
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.utils.ADBaseFragment
import android.adwait.com.utils.ADCommonResponseListener
import android.adwait.com.utils.ADConstants
import android.adwait.com.utils.ADViewClickListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ebanx.swipebtn.OnStateChangeListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*

class ADHomeFragment : ADBaseFragment(), View.OnClickListener {

    private val TAG: String = "ADHomeFragment"
    private var mScreenWidth = 0
    private lateinit var menteeDetails: ADUserDetails
    private var mHasChild = true
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val baseActivity = activity as ADBaseActivity
        mScreenWidth = baseActivity.getScreenDetails(false)

        swipe_btn.setOnStateChangeListener(OnStateChangeListener {

            if (!mHasChild) {
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
                    (activity as ADDashboardActivity).menuAction(ADConstants.MENU_DONATION, "")
                } else {
                    (activity as ADDashboardActivity).fireLogin()
                }
            } else {
                happy_icon.visibility = View.VISIBLE
            }
        })

        if ((activity as ADBaseActivity).isLoggedInUser() && !MySharedPreference((activity as ADBaseActivity)).getValueBoolean(
                getString(R.string.already_logged)
            )
        ) {


            progress_image.visibility = View.VISIBLE
            progress_bar.visibility = View.GONE
            MySharedPreference((activity as ADBaseActivity)).saveBoolean(
                getString(R.string.already_logged),
                true
            )
        } else {
            progress_image.visibility = View.GONE
            progress_bar.visibility = View.VISIBLE
        }

        fetchUserData()
        fetchBanner()

        login_btn.setOnClickListener(View.OnClickListener {
            (activity as ADDashboardActivity).fireLogin()
        })

        mentee_layout.setOnClickListener(View.OnClickListener {
            if (mHasChild) {
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_MY_MENTEE, "")
            } else {
                (activity as ADBaseActivity).showAlertDialog(
                    getString(R.string.no_child_mapped),
                    "",
                    "Ok",
                    null
                )
            }
        })

        info_icon.setOnClickListener(View.OnClickListener {

            if (splitData.toString().length == 0) {
            } else {
                val intent = Intent(activity, ADMonthlySplitActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("splitData", splitData)
                intent.putExtra("data", bundle)
                startActivity(intent)
            }
        })

        tile1.setOnClickListener(this)
        tile2.setOnClickListener(this)
        tile3.setOnClickListener(this)
        tile4.setOnClickListener(this)
        tile5.setOnClickListener(this)
        tile6.setOnClickListener(this)
        tile_1_text.setText(CommonUtils.getHtmlText(getString(R.string.h_club)))
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.tile1 -> {
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_HX_CLUB, "")
            }
            R.id.tile2 -> {
                if (!mHasChild) {
                    (activity as ADBaseActivity).showAlertDialog(
                        getString(R.string.no_child_mapped),
                        "",
                        "Ok",
                        null
                    )
                    return
                }
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_MY_MENTEE, "")

            }
            R.id.tile3 -> {
                if (!mHasChild) {
                    (activity as ADBaseActivity).showAlertDialog(
                        getString(R.string.no_child_mapped),
                        "",
                        "Ok",
                        null
                    )
                    return
                }
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_WISH_CORNER, "")
            }
            R.id.tile4 -> {
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_OUR_PARTNERS, "")
            }
            R.id.tile5 -> {
                if (!mHasChild) {
                    (activity as ADBaseActivity).showAlertDialog(
                        getString(R.string.no_child_mapped),
                        "",
                        "Ok",
                        null
                    )
                    return
                }
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_BE_THE_CHANGE, "")
            }
            R.id.tile6 -> {
                if (!mHasChild) {
                    (activity as ADBaseActivity).showAlertDialog(
                        getString(R.string.no_child_mapped),
                        "",
                        "Ok",
                        null
                    )
                    return
                }
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_OUR_CAUSE, "")
            }
        }
    }

    private fun fetchUserData() {

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.no_internet),
                home_parent,
                true
            )
            return
        }
        if ((activity as ADBaseActivity).isLoggedInUser()) {
            progress_layout?.visibility = View.VISIBLE

            child_details_layout.visibility = View.VISIBLE
            guest_layout.visibility = View.GONE
            (activity as ADBaseActivity).getUserDetails(object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.i(TAG, "Mentee fetched.")
                    if (data.exists()) {
                        menteeDetails = data.getValue(ADUserDetails::class.java)!!
                        if (menteeDetails != null) {

                            MySharedPreference(activity as ADBaseActivity).saveStrings(
                                getString(R.string.userName),
                                menteeDetails.userName
                            )
                            if (menteeDetails.phoneNumber.isEmpty() && menteeDetails.date_of_birth.isEmpty()) {
                                (activity as ADBaseActivity).showAlertDialog(
                                    getString(R.string.no_phone_num),
                                    getString(R.string.update_now),
                                    getString(R.string.cancel),
                                    ADViewClickListener {
                                        (activity as ADDashboardActivity).menuAction(
                                            ADConstants.MENU_PROFILE,
                                            ""
                                        )
                                    })
                            }

                            (activity as ADBaseActivity).getServerDate(ADConstants.KEY_GET_CURRENT_MONTH_YR,
                                object : ADCommonResponseListener {
                                    override fun onSuccess(data: Any?) {
                                        (activity as ADBaseActivity).getNextDate(
                                            ADConstants.KEY_GET_NEXT_MONTH_YR,
                                            data.toString(),
                                            null
                                        )
                                        (activity as ADBaseActivity).getNextDate(
                                            ADConstants.KEY_GET_PREVIOUS_MONTH_YR,
                                            data.toString(),
                                            null
                                        )
                                        fetchChildData(menteeDetails.childId, data.toString())
                                    }

                                    override fun onError(data: Any?) {
                                        hideProgress()
                                    }
                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "Mentee fetch Error : " + error.message)
                    hideProgress()
                }
            })
        } else {
            child_details_layout.visibility = View.GONE
            guest_layout.visibility = View.VISIBLE
        }
    }

    private fun fetchChildData(childId: String, monthYr: String) {

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.no_internet), home_parent, true
            )
            return
        }
        if (childId.isEmpty() || childId.equals("null")) {
            mapChildToUser(monthYr)
            return
        }
        if ((activity as ADBaseActivity).isLoggedInUser()) {

            child_details_layout.visibility = View.VISIBLE
            guest_layout.visibility = View.GONE
            (activity as ADBaseActivity).getChildDetails(childId, object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.i(TAG, "Child Fetched.")
                    if (data.exists() || data != null) {

                        val childData = data.getValue(ADAddChildModel::class.java)!!
                        child_name?.setText(childData.childName)
                        val imageUrl = childData.childImage

                        if (!imageUrl.isEmpty()) {
                            Glide.with(activity as ADBaseActivity).load(imageUrl)
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
                        hideProgress()

                        if (monthlyAmount.toInt() > 0 && monthlyAmount.toInt() == collectedAmount.toInt()) {
                            MySharedPreference(activity as ADBaseActivity).saveStrings(
                                getString(R.string.previous_month_yr),
                                monthYr
                            )

                            progress_layout?.visibility = View.VISIBLE

                            (activity as ADBaseActivity).getNextDate(
                                ADConstants.KEY_GET_NEXT_MONTH_YR, monthYr,
                                object : ADCommonResponseListener {
                                    override fun onSuccess(data: Any?) {
                                        MySharedPreference(activity as ADBaseActivity).saveStrings(
                                            getString(R.string.month_yr),
                                            data.toString()
                                        )
                                        fetchChildData(childId, data.toString())
                                    }

                                    override fun onError(data: Any?) {
                                        hideProgress()
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
                    } else {
                        hideProgress()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "Error : " + error.message)
                    hideProgress()
                }
            })
        } else {
            child_details_layout.visibility = View.GONE
            guest_layout.visibility = View.VISIBLE
            hideProgress()
        }
    }

    private fun fetchBanner() {

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.no_internet),
                home_parent,
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

                    val homeAdapter =
                        ADHomePageAdapter((activity as ADBaseActivity).applicationContext, dataUrl)
                    home_pager.adapter = homeAdapter
                    home_indicator.setViewPager(home_pager)
                    homeAdapter.registerDataSetObserver(home_indicator.dataSetObserver)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "Banner fetch Error : " + error.message)
            }
        }, "Home_Banner")
    }

    private fun mapChildToUser(monthYr: String) {
        val mChildDataTable =
            FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).CHILD_TABLE_NAME)
                .orderByKey()

        var lastMonth =
            MySharedPreference(activity as ADBaseActivity).getValueString(
                getString(R.string.previous_month_yr)
            ).toString()
        var noContributionList = ArrayList<DataSnapshot>()

        mChildDataTable.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() || dataSnapshot == null) {

                    var isChildMapped = false
                    for (data in dataSnapshot.children) {

                        //Check for contribution data in child
                        if (data.hasChild("contribution")) {

                            //If the child has contribution for previous month and the collected amount is >= to needed amount proceed to next
                            if (data.child("contribution").hasChild(lastMonth)) {
                                val monthlyAmount = data.child("amountNeeded").value.toString()
                                var collectedAmount =
                                    data.child("contribution").child(lastMonth)
                                        .child("collected_amt")
                                        .value.toString()

                                if (collectedAmount.isEmpty() || collectedAmount.equals("null")) {
                                    collectedAmount = "0"
                                }

                                if (collectedAmount.toInt() < monthlyAmount.toInt()) {
                                    updateUserWithChild(data.key.toString(), monthYr)
                                    isChildMapped = true
                                    break
                                } else {
                                    continue
                                }
                            }
                        } else {
                            noContributionList.add(data)
                        }
                    }

                    //If child mapping is not done
                    if (!isChildMapped) {
                        if (noContributionList == null || noContributionList.size == 0) {
                            for (data in dataSnapshot.children) {
                                updateUserWithChild(data.key.toString(), monthYr)
                                break
                            }
                        } else {
                            updateUserWithChild(noContributionList.get(0).key.toString(), monthYr)
                        }
                    }
                } else {
                    hideProgress()
                }

            }

            override fun onCancelled(dataError: DatabaseError) {
                Log.i(TAG, "Error : " + dataError.message)
                hideProgress()
            }
        })
    }

    private fun updateUserWithChild(childId: String, monthYr: String) {
        val preference = MySharedPreference((activity as ADBaseActivity).applicationContext)
        val userId = preference.getValueString(getString(R.string.userId)).toString()
        val updateTable =
            FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).USER_TABLE_NAME).child(userId)
        updateTable.child("childId").setValue(childId)
            .addOnCompleteListener((activity as ADBaseActivity)) { task ->
                if (task.isSuccessful) {
                    fetchChildData(childId, monthYr)
                } else {
                    hideProgress()
                    Log.i(TAG, "Error : " + "Something went wrong while updating child")
                }
            }
    }

    fun hideProgress() {

        if (progress_image.visibility == View.VISIBLE) {
            Thread.sleep(5000)
            progress_layout?.visibility = View.GONE
        } else {
            progress_layout?.visibility = View.GONE
        }
    }
}