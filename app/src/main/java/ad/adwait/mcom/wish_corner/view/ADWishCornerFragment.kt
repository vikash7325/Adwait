package ad.adwait.mcom.wish_corner.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import ad.adwait.mcom.R
import ad.adwait.mcom.registeration.model.ADUserDetails
import ad.adwait.mcom.utils.ADBaseFragment
import ad.adwait.mcom.wish_corner.adapter.AdWishListAdapter
import ad.adwait.mcom.wish_corner.model.ADEventsModel
import ad.adwait.mcom.wish_corner.model.ADWishModel
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_wish_corner.*
import java.text.SimpleDateFormat


class ADWishCornerFragment : ADBaseFragment() {

    var mUpComingEvent = ArrayList<ADEventsModel>()
    var mPastEvent = ArrayList<ADEventsModel>()
    lateinit var eventsAdapter: ad.adwait.mcom.wish_corner.adapter.ADEventsAdapter
    private lateinit var mProgressDialog: AlertDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_wish_corner, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout.addTab(tabLayout.newTab().setText(R.string.upcoming))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.recent))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.text.toString().equals(getString(R.string.upcoming))) {
                    eventsAdapter.setmEventsData(mUpComingEvent)
                } else {
                    eventsAdapter.setmEventsData(mPastEvent)
                }
                showError(eventsAdapter.itemCount)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        events_list.layoutManager = (
                androidx.recyclerview.widget.LinearLayoutManager(
                    activity as ADBaseActivity,
                    androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                    false
                ))
        fetchWishes()
        checkUserForAdmin()

        add_wishes.setOnClickListener(View.OnClickListener {

            val add = Intent((activity as ADBaseActivity), ADAddWishesActivity::class.java)
            startActivityForResult(add, 454)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 454) {
            if (resultCode == Activity.RESULT_OK) {
                fetchWishes()
            }
        }
    }

    private fun fetchWishes() {
        val mWishesTable =
            FirebaseDatabase.getInstance().reference.child("Wish_Corner")
        mProgressDialog = (activity as ADBaseActivity).showProgressDialog("", false)
        mProgressDialog.show()

        mWishesTable.addListenerForSingleValueEvent(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var wishItem = ADWishModel()
                var mData = ArrayList<ADWishModel>()

                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children) {

                        wishItem = data.getValue(ADWishModel::class.java)!!
                        mData.add(wishItem)
                        val adapter = AdWishListAdapter(activity as ADBaseActivity, mData)
                        wishes_list.adapter = adapter
                        wish_layout.visibility = View.VISIBLE
                        if (adapter.count > 4) {
                            checkHeight()
                        }
                    }
                } else {
                    wish_layout.visibility = View.GONE
                }
                fetchEvents()
            }

            override fun onCancelled(error: DatabaseError) {
                fetchEvents()
            }
        })
    }

    private fun checkHeight() {
        val screenHeight: Int = ((activity as ADBaseActivity).getScreenDetails(true) * 0.3).toInt()
        val params = events_layout.layoutParams
        params.height = screenHeight
        wish_layout.layoutParams = params
    }

    private fun fetchEvents() {
        mUpComingEvent = ArrayList<ADEventsModel>()
        mPastEvent = ArrayList<ADEventsModel>()
        val eventsTable =
            FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).WISH_EVENTS_TABLE_NAME)
        eventsTable.addListenerForSingleValueEvent(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var eventItem = ADEventsModel()

                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children) {

                        eventItem = data.getValue(ADEventsModel::class.java)!!

                        var today =
                            MySharedPreference(activity as ADBaseActivity).getValueString(
                                getString(
                                    R.string.current_date
                                )
                            )
                                .toString()
                        val todayDate = SimpleDateFormat("dd-MMM-yyyy").parse(today)
                        val eventDate = SimpleDateFormat("dd-MM-yyyy").parse(eventItem.date)

                        if (eventDate.compareTo(todayDate) >= 0) {
                            mUpComingEvent.add(eventItem)
                        } else {
                            mPastEvent.add(eventItem)
                        }
                        eventsAdapter = ad.adwait.mcom.wish_corner.adapter.ADEventsAdapter(
                            activity,
                            mUpComingEvent
                        )
                        events_list.adapter = eventsAdapter
                        showError(eventsAdapter.itemCount)
                        events_layout.visibility = View.VISIBLE
                    }
                } else {
                    events_layout.visibility = View.GONE
                }
                (activity as ADBaseActivity).hideProgress(mProgressDialog)
            }

            override fun onCancelled(error: DatabaseError) {
                (activity as ADBaseActivity).hideProgress(mProgressDialog)
            }
        })
    }

    private fun showError(count: Int) {
        if (count == 0) {
            error_msg.visibility = View.VISIBLE
            events_list.visibility = View.INVISIBLE
        } else {
            events_list.visibility = View.VISIBLE
            error_msg.visibility = View.GONE
        }
    }

    private fun checkUserForAdmin() {
        (activity as ADBaseActivity).getUserDetails(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {

                if (data.exists()) {
                    val menteeDetails = data.getValue(ADUserDetails::class.java)!!

                    if (menteeDetails.can_add_data) {
                        add_wishes.visibility = View.VISIBLE
                    } else {
                        add_wishes.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.v("checkUserForAdmin", p0.message)
            }
        })
    }
}