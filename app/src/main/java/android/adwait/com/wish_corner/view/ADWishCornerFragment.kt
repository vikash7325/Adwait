package android.adwait.com.wish_corner.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.utils.ADBaseFragment
import android.adwait.com.wish_corner.adapter.ADEventsAdapter
import android.adwait.com.wish_corner.adapter.AdWishListAdapter
import android.adwait.com.wish_corner.model.ADEventsModel
import android.adwait.com.wish_corner.model.ADWishModel
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
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
    lateinit var eventsAdapter: ADEventsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
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
                if (tab?.text.toString().equals(getString(R.string.upcoming))){
                    eventsAdapter.setmEventsData(mUpComingEvent)
                }else{
                    eventsAdapter.setmEventsData(mPastEvent)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
        events_list.layoutManager = (
                LinearLayoutManager(
                    activity as ADBaseActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false))
        fetchWishes()
    }

    private fun fetchWishes() {
        val mWishesTable =
            FirebaseDatabase.getInstance().reference.child("Wish_Corner")
        progress_layout.visibility = View.VISIBLE
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


    private fun fetchEvents() {
        mUpComingEvent = ArrayList<ADEventsModel>()
        mPastEvent = ArrayList<ADEventsModel>()
        val eventsTable =
            FirebaseDatabase.getInstance().reference.child("Wish_Corner_Events")
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
                        eventsAdapter = ADEventsAdapter(activity,mUpComingEvent)
                        events_list.adapter = eventsAdapter
                        events_layout.visibility = View.VISIBLE
                    }
                } else {
                    events_layout.visibility = View.GONE
                }
                progress_layout.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                progress_layout.visibility = View.GONE
            }
        })
    }


    private fun checkUserForAdmin() {
        (activity as ADBaseActivity).getUserDetails(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {

                if (data.exists()) {
                    val menteeDetails = data.getValue(ADUserDetails::class.java)!!

                    if (menteeDetails.isAdmin.equals("Yes")) {

                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}