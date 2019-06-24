package android.adwait.com.dashboard.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.be_the_change.view.ADBeTheChangeFragment
import android.adwait.com.dashboard.adapter.ADNavigationListAdapter
import android.adwait.com.donation.view.ADDonationFragment
import android.adwait.com.login.view.ADLoginActivity
import android.adwait.com.my_contribution.view.ADMyContributions
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
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class ADDashboardActivity : ADBaseActivity() {

    private val TAG: String = "ADDashboardActivity"
    private var mUserName = ""
    private var mSelectedMenu=0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        getServerDate("getCurrentMonthAndYr")
        getServerDate("getCurrentDate")

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
            menuAction(ADConstants.MENU_PROFILE,"")
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
        addOrReplaceFragment(true, R.id.home_container, homeFragment, "")
        mSelectedMenu=0

        nav_header.setOnClickListener(View.OnClickListener {
            menuAction(ADConstants.MENU_PROFILE,"")
        })

        val adapter = ADNavigationListAdapter(this)
        navigation_list.adapter = adapter

        navigation_list.setOnItemClickListener { parent, view, position, id ->
            menuAction(position,"")
        }
        fetchUserData()
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
            if (mSelectedMenu == 0) {
                var message =
                    java.lang.String.format(getString(R.string.exit_message), mUserName)
                showAlertDialog(message,
                    getString(R.string.exit),
                    getString(R.string.cancel),
                    ADViewClickListener {
                        super.onBackPressed()
                    })
            } else {
                menuAction(ADConstants.MENU_HOME,"")
            }
        }
    }

    public fun menuAction(option: Int,addToStack:String) {
        when (option) {
            /*Home*/
            ADConstants.MENU_HOME -> {
                val homeFragment = ADHomeFragment()
                addOrReplaceFragment(false, R.id.home_container, homeFragment, addToStack)
                mSelectedMenu=option
            }

            /*My Mentee*/
            ADConstants.MENU_MY_MENTEE -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val menteeFragment = ADMyMenteeFragment()
                addOrReplaceFragment(false, R.id.home_container, menteeFragment, addToStack)
                mSelectedMenu=option
            }

            /*Be the change*/
            ADConstants.MENU_BE_THE_CHANGE -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val changeFragment = ADBeTheChangeFragment()
                addOrReplaceFragment(false, R.id.home_container, changeFragment, addToStack)
                mSelectedMenu=option
            }

            /*Wish Corner*/
            ADConstants.MENU_WISH_CORNER -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val cornerFragment = ADWishCornerFragment()
                addOrReplaceFragment(false, R.id.home_container, cornerFragment, addToStack)
                mSelectedMenu=option
            }

            /*Hx Club*/
            ADConstants.MENU_HX_CLUB -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val adHxFragment = ADHxFragment()
                addOrReplaceFragment(false, R.id.home_container, adHxFragment, addToStack)
                mSelectedMenu=option
            }

            /*My Contributions*/
            ADConstants.MENU_MY_CONTRIBUTION -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val adMyContributions = ADMyContributions()
                addOrReplaceFragment(false, R.id.home_container, adMyContributions, addToStack)
                mSelectedMenu=option
            }

            /*Our Partners*/
            ADConstants.MENU_OUR_PARTNERS -> {
                val adPartnersFragment = ADPartnersFragment()
                addOrReplaceFragment(false, R.id.home_container, adPartnersFragment, addToStack)
                mSelectedMenu=option
            }

            /*Refer Happiness*/
            ADConstants.MENU_REFER_HAPPINESS -> {
                val happinessFragment = ADReferHappinessFragment()
                addOrReplaceFragment(false, R.id.home_container, happinessFragment, addToStack)
                mSelectedMenu=option
            }

            /*Our Cause*/
            ADConstants.MENU_OUR_CAUSE -> {
                val intent = Intent(this, ADOurCauseActivity::class.java)
                startActivity(intent)
            }

            /*Contact us*/
            ADConstants.MENU_CONTACT_US -> {
                val contactUsFragment = ADContactUsFragment()
                addOrReplaceFragment(false, R.id.home_container, contactUsFragment, addToStack)
                mSelectedMenu=option
            }

            /*Donation*/
            ADConstants.MENU_DONATION -> {
                val adDonationFragment = ADDonationFragment()
                addOrReplaceFragment(false, R.id.home_container, adDonationFragment, addToStack)
                mSelectedMenu=option
            }

            /*Profile*/
            ADConstants.MENU_PROFILE -> {
                if (!isLoggedInUser()) {
                    fireLogin()
                    return
                }
                val profileFragment = ADMyProfileFragment()
                addOrReplaceFragment(false, R.id.home_container, profileFragment, addToStack)
                mSelectedMenu=option
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
}
