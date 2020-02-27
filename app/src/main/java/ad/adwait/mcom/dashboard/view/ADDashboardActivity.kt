package ad.adwait.mcom.dashboard.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import ad.adwait.mcom.R
import ad.adwait.mcom.be_the_change.view.ADBeTheChangeFragment
import ad.adwait.mcom.dashboard.adapter.ADNavigationListAdapter
import ad.adwait.mcom.donation.view.ADDonationFragment
import ad.adwait.mcom.login.view.ADLoginActivity
import ad.adwait.mcom.my_contribution.view.ADMyContributionsFragment
import ad.adwait.mcom.my_mentee.view.ADMyMenteeFragment
import ad.adwait.mcom.profile.view.ADMyProfileFragment
import ad.adwait.mcom.registeration.model.ADUserDetails
import ad.adwait.mcom.static_pages.view.*
import ad.adwait.mcom.utils.ADCommonResponseListener
import ad.adwait.mcom.wish_corner.view.ADWishCornerFragment
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


class ADDashboardActivity : ADBaseActivity(), PaymentResultWithDataListener {

    private val TAG: String = "ADDashboardActivity"
    private var mUserName = ""
    private var mSelectedMenu = 0
    private val homeFragment = ADHomeFragment.getInstance()
    private lateinit var childData: DataSnapshot
    private lateinit var homeBannerData: DataSnapshot
    private var mChildId = ""
    private var monthYear = ""
    var mHasChild = true
    private lateinit var progressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        getServerDate(ad.adwait.mcom.utils.ADConstants.KEY_GET_CURRENT_MONTH_YR, null)
        getServerDate(ad.adwait.mcom.utils.ADConstants.KEY_GET_CURRENT_DATE, null)

        supportActionBar?.setDisplayShowHomeEnabled(false)

        val drawerLayout: androidx.drawerlayout.widget.DrawerLayout =
            findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setItemIconTintList(null)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        action_profile.setOnClickListener(View.OnClickListener {
            menuAction(ad.adwait.mcom.utils.ADConstants.MENU_PROFILE, "")
        })

        action_menu.setOnClickListener(View.OnClickListener {
            val drawerLayout: androidx.drawerlayout.widget.DrawerLayout =
                findViewById(R.id.drawer_layout)
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        })
        mSelectedMenu = ad.adwait.mcom.utils.ADConstants.MENU_HOME

        nav_header.setOnClickListener(View.OnClickListener {
            menuAction(ad.adwait.mcom.utils.ADConstants.MENU_PROFILE, "")
        })

        val adapter = ADNavigationListAdapter(this)
        navigation_list.adapter = adapter

        navigation_list.setOnItemClickListener { parent, view, position, id ->
            menuAction(
                position,
                ""
            )
        }
        addOrReplaceFragment(true, R.id.home_container, homeFragment, "")

        if (isLoggedInUser() &&
            !MySharedPreference(applicationContext).getValueBoolean(getString(R.string.already_logged))
        ) {
            progressDialog = showProgressDialog("", true)

            MySharedPreference(applicationContext).saveBoolean(
                getString(R.string.already_logged), true
            )
        } else {
            progressDialog = showProgressDialog("", false)
        }

        fetchUserData(false)
        fetchBanner()

        home_logo.setOnClickListener(View.OnClickListener {
            if (mSelectedMenu != ad.adwait.mcom.utils.ADConstants.MENU_HOME) {
                menuAction(ad.adwait.mcom.utils.ADConstants.MENU_HOME, "")
            }
        })

        version.setText("Version : " + getPackageVersion() + "  ")


        checkAppVersion(object : ADCommonResponseListener {
            override fun onSuccess(data: Any?) {
                showAlertDialog(data.toString(), getString(R.string.update), "",
                    ad.adwait.mcom.utils.ADViewClickListener { openPlayStore() })
            }

            override fun onError(data: Any?) {
                if (data is String) {
                    if (data != null) {

                        showAlertDialog(data.toString(),
                            getString(R.string.update),
                            getString(R.string.cancel),
                            ad.adwait.mcom.utils.ADViewClickListener { openPlayStore() })
                    }
                }
            }
        })

    }

    fun openPlayStore() {
        val appPackageName = packageName // getPackageName() from Context or Activity object
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }

    }

    fun fetchUserData(isSilentCall: Boolean) {

        if (!isNetworkAvailable()) {
            showMessage(
                getString(R.string.no_internet),
                drawer_layout,
                true
            )
            return
        }
        if (isLoggedInUser()) {

            if (!isSilentCall) {
                progressDialog.show()
            }

            getUserDetails(object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.i(TAG, "Mentee fetched.")
                    if (data.exists()) {
                        var menteeDetails = data.getValue(ADUserDetails::class.java)!!
                        if (menteeDetails != null) {

                            MySharedPreference(applicationContext).saveStrings(
                                getString(R.string.userName),
                                menteeDetails.userName
                            )

                            if (menteeDetails.subscriptionId != null && menteeDetails.subscriptionId.length > 0) {
                                MySharedPreference(applicationContext).saveStrings(
                                    getString(R.string.subscription_id),
                                    menteeDetails.subscriptionId
                                )
                            }
                            nav_header_title.setText("Hello " + menteeDetails.userName)
                            if (menteeDetails.phoneNumber.isEmpty() && menteeDetails.date_of_birth.isEmpty()) {
                                showAlertDialog(
                                    getString(R.string.no_phone_num),
                                    getString(R.string.update_now),
                                    getString(R.string.cancel),
                                    ad.adwait.mcom.utils.ADViewClickListener {
                                        menuAction(
                                            ad.adwait.mcom.utils.ADConstants.MENU_PROFILE, ""
                                        )
                                    })
                            }

                            getServerDate(
                                ad.adwait.mcom.utils.ADConstants.KEY_GET_CURRENT_MONTH_YR,
                                object : ADCommonResponseListener {
                                    override fun onSuccess(data: Any?) {
                                        getNextDate(
                                            ad.adwait.mcom.utils.ADConstants.KEY_GET_NEXT_MONTH_YR,
                                            data.toString(),
                                            null
                                        )
                                        getNextDate(
                                            ad.adwait.mcom.utils.ADConstants.KEY_GET_PREVIOUS_MONTH_YR,
                                            data.toString(),
                                            null
                                        )
                                        fetchChildData(
                                            menteeDetails.childId,
                                            data.toString(),
                                            isSilentCall
                                        )
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
            showSwipeHelpScreen()
        }
    }

    fun fetchChildData(childId: String, monthYr: String, isSilentCall: Boolean) {

        if (!isNetworkAvailable()) {
            showMessage(
                getString(R.string.no_internet), drawer_layout, true
            )
            return
        }
        if (childId.isEmpty() || childId.equals("null")) {
            mapChildToUser(monthYr)
            return
        }
        if (isLoggedInUser()) {

            if (!isSilentCall) {
                progressDialog.show()
            }
            mChildId = childId
            monthYear = monthYr
            getChildDetails(childId, object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.i(TAG, "Child Fetched.")
                    if (data.exists() || data != null) {
                        childData = data
                        if (homeFragment != null && homeFragment.isVisible) {
                            homeFragment.populateChildData(childData, monthYr, childId)
                        } else {
                            hideProgress()
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
            hideProgress()
        }
    }

    private fun fetchBanner() {

        if (!isNetworkAvailable()) {
            showMessage(
                getString(R.string.no_internet),
                drawer_layout,
                true
            )
            return
        }

        getBannerDetails(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {
                if (data.exists()) {
                    homeBannerData = data
                    homeFragment?.populateBanner(homeBannerData)
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
                .reference.child(CHILD_TABLE_NAME)
                .orderByKey()

        var lastMonth =
            MySharedPreference(applicationContext).getValueString(
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
                    mHasChild = false
                }

            }

            override fun onCancelled(dataError: DatabaseError) {
                Log.i(TAG, "Error : " + dataError.message)
                hideProgress()
                mHasChild = false
            }
        })
    }

    private fun updateUserWithChild(childId: String, monthYr: String) {
        val preference = MySharedPreference(applicationContext)
        val userId = preference.getValueString(getString(R.string.userId)).toString()
        val updateTable =
            FirebaseDatabase.getInstance()
                .reference.child(USER_TABLE_NAME).child(userId)
        updateTable.child("childId").setValue(childId)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    fetchChildData(childId, monthYr, true)
                } else {
                    hideProgress()
                    Log.i(TAG, "Error : " + "Something went wrong while updating child")
                }
            }
    }

    override fun onBackPressed() {
        val drawerLayout: androidx.drawerlayout.widget.DrawerLayout =
            findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            if (mSelectedMenu == ad.adwait.mcom.utils.ADConstants.MENU_HOME) {
                var message =
                    java.lang.String.format(getString(R.string.exit_message), mUserName)
                showAlertDialog(message,
                    getString(R.string.exit),
                    getString(R.string.cancel),
                    ad.adwait.mcom.utils.ADViewClickListener {
                        super.onBackPressed()
                    })
            } else {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    super.onBackPressed()
                } else {
                    menuAction(ad.adwait.mcom.utils.ADConstants.MENU_HOME, "")
                }
            }
        }
    }

    fun menuAction(option: Int, addToStack: String) {
        mSelectedMenu = option
        when (option) {
            /*Home*/
            ad.adwait.mcom.utils.ADConstants.MENU_HOME -> {
                addOrReplaceFragment(false, R.id.home_container, homeFragment, addToStack)
            }

            /*My Mentee*/
            ad.adwait.mcom.utils.ADConstants.MENU_MY_MENTEE -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val menteeFragment = ADMyMenteeFragment()
                addOrReplaceFragment(false, R.id.home_container, menteeFragment, addToStack)
            }

            /*Be the change*/
            ad.adwait.mcom.utils.ADConstants.MENU_BE_THE_CHANGE -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val changeFragment = ADBeTheChangeFragment()
                addOrReplaceFragment(false, R.id.home_container, changeFragment, addToStack)
            }

            /*Wish Corner*/
            ad.adwait.mcom.utils.ADConstants.MENU_WISH_CORNER -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val cornerFragment = ADWishCornerFragment()
                addOrReplaceFragment(false, R.id.home_container, cornerFragment, addToStack)
            }

            /*Hx Club*/
            ad.adwait.mcom.utils.ADConstants.MENU_HX_CLUB -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val adHxFragment = ADHxFragment()
                addOrReplaceFragment(false, R.id.home_container, adHxFragment, addToStack)
            }

            /*My Contributions*/
            ad.adwait.mcom.utils.ADConstants.MENU_MY_CONTRIBUTION -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }

                val adMyContributions = ADMyContributionsFragment()
                addOrReplaceFragment(false, R.id.home_container, adMyContributions, addToStack)
            }

            /*Our Partners*/
            ad.adwait.mcom.utils.ADConstants.MENU_OUR_PARTNERS -> {
                val adPartnersFragment = ADPartnersFragment()
                addOrReplaceFragment(false, R.id.home_container, adPartnersFragment, addToStack)
            }

            /*Refer Happiness*/
            ad.adwait.mcom.utils.ADConstants.MENU_REFER_HAPPINESS -> {
                val happinessFragment = ADReferHappinessFragment()
                addOrReplaceFragment(false, R.id.home_container, happinessFragment, addToStack)
            }

            /*Our Cause*/
            ad.adwait.mcom.utils.ADConstants.MENU_OUR_CAUSE -> {
                val intent = Intent(this, ADOurCauseActivity::class.java)
                startActivityForResult(intent, ad.adwait.mcom.utils.ADConstants.KEY_REQUEST)
            }

            /*Contact us*/
            ad.adwait.mcom.utils.ADConstants.MENU_CONTACT_US -> {
                val contactUsFragment = ADContactUsFragment()
                addOrReplaceFragment(false, R.id.home_container, contactUsFragment, addToStack)
            }

            /*Contact us*/
            ad.adwait.mcom.utils.ADConstants.MENU_LOGOUT -> {
                registerLogoutListener()
                FirebaseAuth.getInstance().signOut()
            }

            /*Donation*/
            ad.adwait.mcom.utils.ADConstants.MENU_DONATION -> {
                val adDonationFragment = ADDonationFragment()
                addOrReplaceFragment(false, R.id.home_container, adDonationFragment, addToStack)
            }

            /*Profile*/
            ad.adwait.mcom.utils.ADConstants.MENU_PROFILE -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val profileFragment = ADMyProfileFragment()
                addOrReplaceFragment(false, R.id.home_container, profileFragment, addToStack)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.END)
    }

    fun fireLogin() {
        var login = Intent(applicationContext, ADLoginActivity::class.java)
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(login)
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        val donationFragment =
            supportFragmentManager.findFragmentById(R.id.home_container) as ADDonationFragment
        donationFragment.onPaymentError(p0, p1, p2)
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        val donationFragment =
            supportFragmentManager.findFragmentById(R.id.home_container) as ADDonationFragment
        donationFragment.onPaymentSuccess(p0, p1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ad.adwait.mcom.utils.ADConstants.KEY_REQUEST) {
            if (resultCode == ad.adwait.mcom.utils.ADConstants.KEY_MENU) {
                drawer_layout.openDrawer(GravityCompat.END)
            } else if (resultCode == ad.adwait.mcom.utils.ADConstants.KEY_PROFILE) {
                menuAction(ad.adwait.mcom.utils.ADConstants.MENU_PROFILE, "")
            }
        }
    }

    fun checkData() {
        if (mChildId != null && monthYear != null && ::homeBannerData.isInitialized && ::childData.isInitialized) {
            homeFragment?.populateBanner(homeBannerData)
            homeFragment?.populateChildData(childData, monthYear, mChildId)
        }
    }

    fun hideProgress() {
        hideProgress(progressDialog)
        showSwipeHelpScreen()
    }

    private fun showSwipeHelpScreen() {

        var pref = MySharedPreference(applicationContext)
        if (pref.getValueBoolean(getString(R.string.swipe_tutorial))) {
            return
        }
        pref.saveBoolean(getString(R.string.swipe_tutorial), true)

        val builder = AlertDialog.Builder(this, android.R.style.Theme_Translucent_NoTitleBar)
        val view = View.inflate(applicationContext, R.layout.swipe_help_screen, null)
        val doneBtn = view.findViewById<Button>(R.id.done_btn)

        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        alertDialog.setCancelable(false)
        alertDialog.show()
        doneBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}
