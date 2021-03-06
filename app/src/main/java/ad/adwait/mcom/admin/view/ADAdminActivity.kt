package ad.adwait.mcom.admin.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import ad.adwait.mcom.R
import ad.adwait.mcom.login.view.ADLoginActivity
import ad.adwait.mcom.utils.ADCommonResponseListener
import ad.adwait.mcom.wish_corner.view.ADAddWishesActivity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.View
import com.example.myapplication.admin.adapter.AdSectionsPagerAdapter
import com.example.myapplication.admin.view.ADFragmentChild
import com.example.myapplication.admin.view.ADFragmentWishes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_admin.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ADAdminActivity : ADBaseActivity() {

    private var mIsFabShown = false
    private lateinit var sectionsPagerAdapter: AdSectionsPagerAdapter
    private var mLastMonth = ""
    private var mRetryCount = 0
    private var messageData = HashMap<String, ad.adwait.mcom.admin.model.ADTransferData>();
    private lateinit var mProgressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        mRetryCount = 0

        sectionsPagerAdapter =
            AdSectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: androidx.viewpager.widget.ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        logout.setOnClickListener(View.OnClickListener {
            MySharedPreference(applicationContext).saveBoolean(
                getString(R.string.superAdmin),
                false
            )
            MySharedPreference(applicationContext).saveBoolean(getString(R.string.logged_in), false)
            MySharedPreference(applicationContext).saveStrings(getString(R.string.userId), "")
            var login = Intent(applicationContext, ADLoginActivity::class.java)
            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(login)
            finish()
        })

        hideFABMenu()
        fab.setOnClickListener { view ->
            if (mIsFabShown) {
                hideFABMenu()
            } else {
                showFABMenu()
            }
        }

        sync.setOnClickListener(View.OnClickListener {
            syncLastMonthData()
        })


        wish_fab.setOnClickListener(View.OnClickListener {
            startActivityForResult(Intent(this, ADAddWishesActivity::class.java), 1235)
            hideFABMenu()
        })
        child_fab.setOnClickListener(View.OnClickListener {
            startActivityForResult(Intent(this, ADAddChildActivity::class.java), 1235)
            hideFABMenu()
        })

        mProgressDialog = showProgressDialog("", false)
    }

    public fun getPreviousMonth() {
        showHideProgress(true)

        val currentDate =
            MySharedPreference(this).getValueString(getString(R.string.current_date)).toString()

        if (currentDate == null || currentDate.length == 0 || currentDate.equals("null")) {
            getServerDate(ad.adwait.mcom.utils.ADConstants.KEY_GET_CURRENT_DATE, object : ADCommonResponseListener {
                override fun onSuccess(data: Any?) {
                    getDate(data.toString())
                }

                override fun onError(data: Any?) {
                    showHideProgress(false)
                }
            })
        } else {
            getDate(currentDate)
        }
    }

    fun getDate(currentDate: String) {
        getNextDate(
            ad.adwait.mcom.utils.ADConstants.KEY_GET_PREVIOUS_MONTH_YR,
            currentDate,
            object : ADCommonResponseListener {
                override fun onSuccess(data: Any?) {
                    checkSyncingForLastMonth(data.toString())
                }

                override fun onError(data: Any?) {
                    showHideProgress(false)
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1235) {
            if (resultCode == Activity.RESULT_OK) {

                var childFragment = sectionsPagerAdapter.getRegisteredFragment(0) as ADFragmentChild
                childFragment.getData(false)
                var wishFragment = sectionsPagerAdapter.getRegisteredFragment(1) as ADFragmentWishes
                wishFragment.getData()

            }
        }
    }

    private fun checkSyncingForLastMonth(lastMonth: String) {


        if (!isNetworkAvailable()) {
            showMessage(getString(R.string.no_internet), admin_parent, true)
            return
        }
        showHideProgress(true)
        mLastMonth = lastMonth
        val routingTable =
            FirebaseDatabase.getInstance().reference.child(ROUTING_TABLE_NAME)

        routingTable.child(lastMonth).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot != null) {
                    sync.visibility = View.GONE
                } else {
                    if (MySharedPreference(applicationContext).getValueBoolean(getString(R.string.is_update_failed))) {
                        saveUpdateToServer(
                            MySharedPreference(applicationContext).getValueString(
                                getString(R.string.transferred_date)
                            ).toString()
                        )
                    } else {
                        sync.visibility = View.VISIBLE
                    }
                }
                showHideProgress(false)
            }

            override fun onCancelled(dbError: DatabaseError) {
                Log.i("Testing==>", dbError.message)
                showHideProgress(false)
            }
        })

    }

    private fun syncLastMonthData() {

        showHideProgress(true)
        val apiService = ad.adwait.mcom.rest_api.ApiClient.getClient().create(ad.adwait.mcom.rest_api.ApiInterface::class.java)

        val call = apiService.transferAmount(ad.adwait.mcom.admin.model.ADTransferRequest())

        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>?, response: Response<JsonElement>?) {

                if (response != null && response.isSuccessful) {
                    if (response.body() != null) {
                        val jsonElement = response.body()
                        val gson = Gson()

                        if (jsonElement.isJsonObject()) {
                            val data = gson.fromJson(response.body(), ad.adwait.mcom.admin.model.ADTransferData::class.java)
                            if (!data.isSuccessFlag) {
                                showHideProgress(false)
                                showMessage(data.message, admin_parent, true)
                            }
                        } else {
                            messageData = gson.fromJson(
                                response.body(),
                                object : TypeToken<HashMap<String, ad.adwait.mcom.admin.model.ADTransferData>>() {}.type
                            )
                            saveUpdateToServer(
                                MySharedPreference(applicationContext).getValueString(
                                    getString(R.string.current_date)
                                ).toString()
                            )
                        }
                    }
                } else {
                    val error = Gson().fromJson(
                        response?.errorBody()?.charStream(),
                        ad.adwait.mcom.admin.model.RestError::class.java
                    )
                    Log.i("Testing ==> ", error.toString())
                    showMessage(error.error.description, null, false)
                   showHideProgress(false)
                }
            }

            override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                showHideProgress(false)
                showMessage("Something went wrong. Try again later", null, false)
                Log.i("Testing ==> ", t?.message.toString())
            }
        })
    }

    private fun saveUpdateToServer(date: String) {

        if (!isNetworkAvailable()) {
            showMessage(getString(R.string.no_internet), admin_parent, true)
            return
        }
        showHideProgress(true)
        val routingTable =
            FirebaseDatabase.getInstance().reference.child(ROUTING_TABLE_NAME)
        routingTable.child(mLastMonth)
            .setValue(ad.adwait.mcom.admin.model.ADRoutingDetails(date, true))
            .addOnSuccessListener {
                showMessage("Amount has been sent to respective accounts.", null, false)
                sync.visibility = View.GONE
                MySharedPreference(applicationContext).saveBoolean(
                    getString(R.string.is_update_failed),
                    false
                )
                showHideProgress(false)
                val messagesActivity =
                    Intent(applicationContext, ADRoutingMessagesActivity::class.java)
                messagesActivity.putExtra("messageData", messageData)
                startActivity(messagesActivity)
            }
            .addOnFailureListener {
                if (mRetryCount < 3) {
                    mRetryCount++
                    saveUpdateToServer(date)
                } else {
                    showHideProgress(false)
                    showMessage(it.message.toString(), null, false)
                    Log.i("Testing ==> ", it?.message.toString())

                    MySharedPreference(applicationContext).saveStrings(
                        getString(R.string.transferred_date),
                        MySharedPreference(applicationContext).getValueString(getString(R.string.current_date)).toString()
                    )

                    MySharedPreference(applicationContext).saveBoolean(
                        getString(R.string.is_update_failed),
                        true
                    )
                    val messagesActivity =
                        Intent(applicationContext, ADRoutingMessagesActivity::class.java)
                    messagesActivity.putExtra("messageData", messageData)
                    startActivity(messagesActivity)
                }
            }
        Log.i("Testing ==> ", "saveUpdateToServer called")
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

    public fun showHideProgress(show: Boolean) {
        if (show) {
            mProgressDialog.show()
        } else {
            if (mProgressDialog.isShowing)
                mProgressDialog.hide()
        }
    }

}
