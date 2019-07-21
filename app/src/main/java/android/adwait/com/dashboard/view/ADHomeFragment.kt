package android.adwait.com.dashboard.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.CommonUtils
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.dashboard.adapter.ADHomePageAdapter
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.utils.ADBaseFragment
import android.adwait.com.utils.ADCommonResponseListener
import android.adwait.com.utils.ADConstants
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
    private var mDetailsFilled = false
    private lateinit var menteeDetails: ADUserDetails
    private var mHasChild = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_home, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val baseActivity = activity as ADBaseActivity
        mScreenWidth = baseActivity.getScreenDetails(false)

        val data =
            arrayOf(R.drawable.home_banner_1, R.drawable.home_banner_2, R.drawable.superhero_kids)

        val homeAdapter = ADHomePageAdapter((activity as ADBaseActivity).applicationContext, data)
        home_pager.adapter = homeAdapter
        home_indicator.setViewPager(home_pager)
        homeAdapter.registerDataSetObserver(home_indicator.dataSetObserver)

        swipe_btn.setOnStateChangeListener(OnStateChangeListener {

            if (!mHasChild) {
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
                getString(R.string.already_logged))) {


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

        login_btn.setOnClickListener(View.OnClickListener {
            (activity as ADDashboardActivity).fireLogin()
        })

        mentee_layout.setOnClickListener(View.OnClickListener {
            if (mDetailsFilled) {
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_MY_MENTEE, "")
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
                    return
                }
                if (mDetailsFilled) {
                    (activity as ADDashboardActivity).menuAction(ADConstants.MENU_MY_MENTEE, "")
                } else {
                    (activity as ADDashboardActivity).menuAction(ADConstants.MENU_MY_MENTEE, "")
                }
            }
            R.id.tile3 -> {
                if (!mHasChild) {
                    return
                }
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_WISH_CORNER, "")
            }
            R.id.tile4 -> {
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_OUR_PARTNERS, "")
            }
            R.id.tile5 -> {
                if (!mHasChild) {
                    return
                }
                (activity as ADDashboardActivity).menuAction(ADConstants.MENU_BE_THE_CHANGE, "")
            }
            R.id.tile6 -> {
                if (!mHasChild) {
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
                true)
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

                            MySharedPreference(activity as ADBaseActivity).saveStrings(getString(R.string.userName),menteeDetails.userName)
                            if (menteeDetails.phoneNumber.isEmpty() && menteeDetails.date_of_birth.isEmpty()) {
                                mentee_layout.alpha = 0.5f
                                mDetailsFilled = false
                            } else {
                                mDetailsFilled = true
                            }
                            var monthYr =
                                MySharedPreference(activity as ADBaseActivity).getValueString(
                                    getString(R.string.month_yr)).toString()

                            if (monthYr.isEmpty() || monthYr.length == 0 || monthYr.toLowerCase().equals(
                                    "null")) {
                                (activity as ADBaseActivity).getServerDate("getCurrentMonthAndYr",
                                    object : ADCommonResponseListener {
                                        override fun onSuccess(data: Any?) {
                                            fetchChildData(menteeDetails.childId, data.toString())
                                        }

                                        override fun onError(data: Any?) {
                                            hideProgress()
                                        }
                                    })
                            } else {
                                fetchChildData(menteeDetails.childId, monthYr)
                            }
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
                    hideProgress()
                    if (data.exists()) {
                        if (data != null) {

                            child_name?.setText(data.child("name").value.toString())
                            val imageUrl = data.child("child_image").value.toString()

                            if (!imageUrl.isEmpty()) {
                                Glide.with(activity as ADBaseActivity).load(imageUrl)
                                    .placeholder(R.drawable.ic_guest_user).diskCacheStrategy(
                                        DiskCacheStrategy.SOURCE
                                    ).into(child_image)
                            }
                            val dob = data.child("date_of_birth").value.toString()

                            val age: String =
                                (activity as ADBaseActivity).getAge(dob, "dd-MMM-yyyy").toString()
                            child_age?.setText(age + " Years")

                            val monthlyAmount = data.child("amount_needed").value.toString()

                            var collectedAmount =
                                data.child("contribution").child(monthYr).child("collected_amt")
                                    .value.toString()

                            if (collectedAmount.isEmpty() || collectedAmount.equals("null")) {
                                collectedAmount = "0"
                            }

                            if (monthlyAmount.toInt() > 0 && monthlyAmount.toInt() == collectedAmount.toInt()){
                                MySharedPreference(activity as ADBaseActivity).saveStrings(getString(R.string.previous_month_yr), monthYr)

                                (activity as ADBaseActivity).getNextDate("getNextMonthAndYr",monthYr,
                                    object : ADCommonResponseListener {
                                        override fun onSuccess(data: Any?) {
                                            MySharedPreference(activity as ADBaseActivity).saveStrings(getString(R.string.month_yr), data.toString())
                                            fetchChildData(childId,data.toString())
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

                            if (!collectedAmount.isEmpty() && collectedAmount != null && !collectedAmount.equals(
                                    "null") &&
                                monthlyAmount != null && !monthlyAmount.equals("null") && monthlyAmount.toInt() > 0) {
                                val percent =
                                    ((collectedAmount.toInt()) * 100 / monthlyAmount.toInt())
                                progress.setProgress(percent)
                            }
                        }
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

    private fun mapChildToUser(monthYr: String) {
        val mChildDataTable =
            FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).CHILD_TABLE_NAME)
                .orderByKey()
        mChildDataTable.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (data in dataSnapshot.children) {

                    if (!data.hasChild("contribution")) {
                        updateUserWithChild(data.key.toString(), monthYr)
                        break
                    } else {
                        var lastMonth =
                            MySharedPreference(activity as ADBaseActivity).getValueString(
                                getString(R.string.previous_month_yr)).toString()

                        if (data.child("contribution").hasChild(lastMonth)) {
                            val monthlyAmount = data.child("amount_needed").value.toString()
                            var collectedAmount =
                                data.child("contribution").child(lastMonth).child("collected_amt")
                                    .value.toString()

                            if (collectedAmount.isEmpty() || collectedAmount.equals("null")) {
                                collectedAmount = "0"
                            }

                            if (collectedAmount.toInt() < monthlyAmount.toInt()) {
                                updateUserWithChild(data.key.toString(), monthYr)
                                break
                            }else{
                                continue
                            }
                        }
                    }
                }

                hideProgress()

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