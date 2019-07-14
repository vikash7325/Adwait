package android.adwait.com.my_mentee.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.CommonUtils
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.dashboard.adapter.ADHomePageAdapter
import android.adwait.com.dashboard.view.ADDashboardActivity
import android.adwait.com.my_mentee.model.ADMessageModel
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.utils.ADBaseFragment
import android.adwait.com.utils.ADConstants
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ebanx.swipebtn.OnStateChangeListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_my_mentee.*
import kotlinx.android.synthetic.main.item_message_received.*

class ADMyMenteeFragment : ADBaseFragment() {

    private val TAG: String = "ADMyMenteeFragment"
    private lateinit var menteeDetails: ADUserDetails

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_my_mentee, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_btn.setOnStateChangeListener(OnStateChangeListener {
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

        send_btn.setOnClickListener(View.OnClickListener {
            addMessage(typed_msg.text.toString())
        })
        typed_msg.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                addMessage(typed_msg.text.toString())
                true
            } else {
                false
            }
        }
        fetchUserData()

        val data =
            arrayOf(R.drawable.home_banner_1, R.drawable.home_banner_2, R.drawable.superhero_kids)

        val pageAdapter = ADHomePageAdapter((activity as ADBaseActivity).applicationContext, data)
        mentee_pager.adapter = pageAdapter
        mentee_indicator.setViewPager(mentee_pager)
        pageAdapter.registerDataSetObserver(mentee_indicator.dataSetObserver)
    }

    private fun addMessage(message: String) {
        if (message.isEmpty()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.empty_message),
                mentee_parent,
                true
            )
        } else {

            if (!(activity as ADBaseActivity).isNetworkAvailable()) {
                (activity as ADBaseActivity).showMessage(
                    getString(R.string.no_internet),
                    mentee_parent,
                    true
                )
                return
            }

            progress_layout.visibility = View.VISIBLE
            var today =
                MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.current_date))
                    .toString()


            val message = ADMessageModel(today, message, menteeDetails.userName, false)
            FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).CHILD_TABLE_NAME)
                .child(menteeDetails.childId).child(menteeDetails.userName).setValue(message)
                .addOnSuccessListener {

                    var message = java.lang.String.format(
                        getString(R.string.message_sent),
                        menteeDetails.userName
                    )
                    typed_msg.setText("")
                    progress_layout.visibility = View.GONE
                    (activity as ADBaseActivity).showAlertDialog(
                        message, "",
                        "Close", null
                    )
                }
                .addOnFailureListener {
                    progress_layout.visibility = View.GONE
                    (activity as ADBaseActivity).showMessage(
                        getString(R.string.message_failed),
                        mentee_parent,
                        false
                    )
                }
        }
    }


    private fun fetchUserData() {

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.no_internet),
                mentee_parent,
                true
            )
            return
        }
        if ((activity as ADBaseActivity).isLoggedInUser()) {
            progress_layout?.visibility = View.VISIBLE

            (activity as ADBaseActivity).getUserDetails(object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.e(TAG, "Mentee fetched.")
                    if (data.exists()) {
                        menteeDetails = data.getValue(ADUserDetails::class.java)!!
                        if (menteeDetails != null) {
                            fetchChildData(menteeDetails.childId,false)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Mentee fetch Error : " + error.message)
                    progress_layout?.visibility = View.GONE
                }
            })
        } else {

        }
    }

    private fun fetchChildData(childId: String,amtCollected:Boolean) {

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.no_internet),
                mentee_parent,
                true
            )
            return
        }
        if (childId.isEmpty()) {
            progress_layout.visibility = View.GONE
            return
        }
        if ((activity as ADBaseActivity).isLoggedInUser()) {

            (activity as ADBaseActivity).getChildDetails(childId, object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.e(TAG, "Child Fetched.")
                    progress_layout?.visibility = View.GONE
                    if (data.exists()) {
                        if (data != null) {

                            val name = data.child("name").value.toString()
                            val imageUrl = data.child("child_image").value.toString()
                            val dob = data.child("date_of_birth").value.toString()
                            val monthlyAmount = data.child("amount_needed").value.toString()
                            val guardian = data.child("guardian_name").value.toString()
                            val location = data.child("city").value.toString()
                            var ngoName = data.child("ngo_name").value.toString()
                            if (ngoName.isEmpty()) {
                                ngoName = "NA"
                            }
                            val school = data.child("school_name").value.toString()
                            val dream = data.child("career_interest").value.toString()
                            val talent = data.child("hobby").value.toString()

                            var elveName = menteeDetails.userName
                            if (elveName.contains(" ")) {
                                elveName = elveName.substring(0, elveName.indexOf(" "))
                            }
                            val message = java.lang.String.format(
                                getString(R.string.evle_msg),
                                elveName,
                                name
                            )
                            text_message_body.setText(message)
                            child_name?.setText(
                                CommonUtils.getHtmlText(
                                    java.lang.String.format(
                                        getString(R.string.mente_child_name),
                                        name
                                    )
                                )
                            )

                            guardian_name?.setText(
                                CommonUtils.getHtmlText(
                                    java.lang.String.format(
                                        getString(R.string.mente_guardian),
                                        guardian
                                    )
                                )
                            )

                            men_location?.setText(
                                CommonUtils.getHtmlText(
                                    java.lang.String.format(
                                        getString(R.string.mentee_location),
                                        location
                                    )
                                )
                            )

                            ngo_name?.setText(
                                CommonUtils.getHtmlText(
                                    java.lang.String.format(
                                        getString(R.string.mentee_ngo),
                                        ngoName
                                    )
                                )
                            )

                            tile_1_text.setText(dob)
                            tile_2_text.setText(talent)
                            tile_3_text.setText(dream)
                            tile_4_text.setText(school)

                            if (!imageUrl.isEmpty()) {
                                Glide.with(activity as ADBaseActivity).load(imageUrl)
                                    .placeholder(R.drawable.ic_guest_user).diskCacheStrategy(
                                        DiskCacheStrategy.SOURCE
                                    ).into(child_image)
                            }
                            val age: String = (activity as ADBaseActivity).getAge(
                                dob,
                                "dd-MMM-yyyy"
                            ).toString() + " Years"

                            child_age?.setText(
                                CommonUtils.getHtmlText(
                                    java.lang.String.format(
                                        getString(R.string.mente_child_age),
                                        age
                                    )
                                )
                            )

                            var monthYr =
                                MySharedPreference(activity as ADBaseActivity).getValueString(
                                    getString(R.string.month_yr)).toString()
                            if (amtCollected){
                                monthYr =
                                    MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.next_month_yr))
                                        .toString()
                            }

                            var collectedAmount =
                                data.child("contribution").child(monthYr).child("collected_amt")
                                    .value.toString()

                            if (collectedAmount.isEmpty() || collectedAmount.equals("null")) {
                                collectedAmount = "0"
                            }
                            if (monthlyAmount.toInt() > 0 && monthlyAmount.toInt() == collectedAmount.toInt()){
                                fetchChildData(childId,true)
                                return
                            }

                            val text = java.lang.String.format(
                                getString(R.string.fund_raised_msg),
                                collectedAmount,
                                monthlyAmount,
                                monthYr
                            )
                            fund_details?.setText(text)

                            if (!collectedAmount.isEmpty() && collectedAmount != null && !collectedAmount.equals(
                                    "null"
                                ) &&
                                monthlyAmount != null && !monthlyAmount.equals("null") && monthlyAmount.toInt() > 0
                            ) {
                                val percent =
                                    ((collectedAmount.toInt()) * 100 / monthlyAmount.toInt())
                                progress.setProgress(percent)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error : " + error.message)
                    progress_layout?.visibility = View.GONE
                }
            })
        } else {

        }
    }
}