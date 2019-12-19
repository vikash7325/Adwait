package android.adwait.com.dashboard.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.be_the_change.view.ADBeTheChangeFragment
import android.adwait.com.dashboard.adapter.ADNavigationListAdapter
import android.adwait.com.donation.view.ADDonationFragment
import android.adwait.com.login.view.ADLoginActivity
import android.adwait.com.my_contribution.view.ADMyContributionsFragment
import android.adwait.com.my_mentee.view.ADMyMenteeFragment
import android.adwait.com.profile.view.ADMyProfileFragment
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.static_pages.view.*
import android.adwait.com.utils.ADConstants
import android.adwait.com.utils.ADViewClickListener
import android.adwait.com.wish_corner.view.ADWishCornerFragment
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        getServerDate(ADConstants.KEY_GET_CURRENT_MONTH_YR, null)
        getServerDate(ADConstants.KEY_GET_CURRENT_DATE, null)

        supportActionBar?.setDisplayShowHomeEnabled(false)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
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
            menuAction(ADConstants.MENU_PROFILE, "")
        })

        action_menu.setOnClickListener(View.OnClickListener {
            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            if (drawerLayout.isDrawerOpen(Gravity.END)) {
                drawerLayout.closeDrawer(Gravity.END)
            } else {
                drawerLayout.openDrawer(Gravity.END)
            }
        })
        val homeFragment = ADHomeFragment()
        mSelectedMenu = ADConstants.MENU_HOME

        nav_header.setOnClickListener(View.OnClickListener {
            menuAction(ADConstants.MENU_PROFILE, "")
        })

        val adapter = ADNavigationListAdapter(this)
        navigation_list.adapter = adapter

        navigation_list.setOnItemClickListener { parent, view, position, id ->
            menuAction(
                position,
                ""
            )
        }
        fetchUserData()
        addOrReplaceFragment(true, R.id.home_container, homeFragment, "")

        home_logo.setOnClickListener(View.OnClickListener {
            if (mSelectedMenu != ADConstants.MENU_HOME) {
                menuAction(ADConstants.MENU_HOME, "")
            }
        })

        getPackageVersion()
    }

    fun getPackageVersion() {
        val info = packageManager.getPackageInfo(packageName, 0)
        version.setText("Version : " + info.versionName + "  ")
    }

    fun fetchUserData() {
        if (isLoggedInUser()) {
            getUserDetails(object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.e(TAG, "onDataChange called : ")
                    if (data.exists()) {
                        val user = data.getValue(ADUserDetails::class.java)!!
                        if (user != null) {
                            nav_header_title.setText("Hello, " + user.userName)
                            mUserName = user.userName
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error : " + error.message)
                }
            })

        } else {
            nav_header_title.setText("Hello, Guest")
            mUserName = "Guest"
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END)
        } else {
            if (mSelectedMenu == ADConstants.MENU_HOME) {
                var message =
                    java.lang.String.format(getString(R.string.exit_message), mUserName)
                showAlertDialog(message,
                    getString(R.string.exit),
                    getString(R.string.cancel),
                    ADViewClickListener {
                        super.onBackPressed()
                    })
            } else {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    super.onBackPressed()
                } else {
                    menuAction(ADConstants.MENU_HOME, "")
                }
            }
        }
    }

    public fun menuAction(option: Int, addToStack: String) {
        mSelectedMenu = option
        when (option) {
            /*Home*/
            ADConstants.MENU_HOME -> {
                addOrReplaceFragment(false, R.id.home_container, homeFragment, addToStack)
            }

            /*My Mentee*/
            ADConstants.MENU_MY_MENTEE -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val menteeFragment = ADMyMenteeFragment()
                addOrReplaceFragment(false, R.id.home_container, menteeFragment, addToStack)
            }

            /*Be the change*/
            ADConstants.MENU_BE_THE_CHANGE -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val changeFragment = ADBeTheChangeFragment()
                addOrReplaceFragment(false, R.id.home_container, changeFragment, addToStack)
            }

            /*Wish Corner*/
            ADConstants.MENU_WISH_CORNER -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val cornerFragment = ADWishCornerFragment()
                addOrReplaceFragment(false, R.id.home_container, cornerFragment, addToStack)
            }

            /*Hx Club*/
            ADConstants.MENU_HX_CLUB -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val adHxFragment = ADHxFragment()
                addOrReplaceFragment(false, R.id.home_container, adHxFragment, addToStack)
            }

            /*My Contributions*/
            ADConstants.MENU_MY_CONTRIBUTION -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }

                val adMyContributions = ADMyContributionsFragment()
                addOrReplaceFragment(false, R.id.home_container, adMyContributions, addToStack)
            }

            /*Our Partners*/
            ADConstants.MENU_OUR_PARTNERS -> {
                val adPartnersFragment = ADPartnersFragment()
                addOrReplaceFragment(false, R.id.home_container, adPartnersFragment, addToStack)
            }

            /*Refer Happiness*/
            ADConstants.MENU_REFER_HAPPINESS -> {
                val happinessFragment = ADReferHappinessFragment()
                addOrReplaceFragment(false, R.id.home_container, happinessFragment, addToStack)
            }

            /*Our Cause*/
            ADConstants.MENU_OUR_CAUSE -> {
                val intent = Intent(this, ADOurCauseActivity::class.java)
                startActivityForResult(intent, ADConstants.KEY_REQUEST)
            }

            /*Contact us*/
            ADConstants.MENU_CONTACT_US -> {
                val contactUsFragment = ADContactUsFragment()
                addOrReplaceFragment(false, R.id.home_container, contactUsFragment, addToStack)
            }

            /*Contact us*/
            ADConstants.MENU_LOGOUT -> {
                registerLogoutListener()
                FirebaseAuth.getInstance().signOut()
            }

            /*Donation*/
            ADConstants.MENU_DONATION -> {
                val adDonationFragment = ADDonationFragment()
                addOrReplaceFragment(false, R.id.home_container, adDonationFragment, addToStack)
            }

            /*Profile*/
            ADConstants.MENU_PROFILE -> {
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

    public fun fireLogin() {
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

        if (requestCode == ADConstants.KEY_REQUEST) {
            if (resultCode == ADConstants.KEY_MENU) {
                drawer_layout.openDrawer(Gravity.END)
            } else if (resultCode == ADConstants.KEY_PROFILE) {
                menuAction(ADConstants.MENU_PROFILE, "")
            }
        }
    }
}
