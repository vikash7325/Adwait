package ad.adwait.mcom.my_mentee.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.CommonUtils
import and.com.polam.utils.MySharedPreference
import ad.adwait.mcom.R
import ad.adwait.mcom.admin.model.ADAddChildModel
import ad.adwait.mcom.admin.model.ADMoneySplitUp
import ad.adwait.mcom.dashboard.adapter.ADHomePageAdapter
import ad.adwait.mcom.dashboard.view.ADDashboardActivity
import ad.adwait.mcom.my_mentee.model.ADMessageModel
import ad.adwait.mcom.registeration.model.ADUserDetails
import ad.adwait.mcom.utils.ADBaseFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
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
    private var splitData: ADMoneySplitUp = ADMoneySplitUp()
    private lateinit var childData: ADAddChildModel
    private lateinit var mProgressDialog: AlertDialog

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
                    (activity as ADDashboardActivity).menuAction(ad.adwait.mcom.utils.ADConstants.MENU_DONATION, "")
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
        mProgressDialog = (activity as ADBaseActivity).showProgressDialog("", false)
        fetchUserData()

        view.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if (event?.action == MotionEvent.ACTION_UP) {
                    val imm =
                        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
                }
                return true
            }

        })
    }

    private fun addMessage(message: String) {
        if (message.isEmpty()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.empty_message),
                mentee_parent,
                true
            )
        } else {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(typed_msg.windowToken, 0)

            if (!(activity as ADBaseActivity).isNetworkAvailable()) {
                (activity as ADBaseActivity).showMessage(
                    getString(R.string.no_internet),
                    mentee_parent,
                    true
                )
                return
            }

            mProgressDialog.show()
            var today =
                MySharedPreference(activity as ADBaseActivity).getValueString(getString(R.string.current_date))
                    .toString()


            val message = ADMessageModel(today, message, menteeDetails.userName, false)
            val mChildTable = FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).CHILD_TABLE_NAME)
            var key = mChildTable.push().key.toString()
            mChildTable.child(menteeDetails.childId).child(menteeDetails.userName).child(key)
                .setValue(message)
                .addOnSuccessListener {

                    var message = java.lang.String.format(
                        getString(R.string.mentee_success_msg), childData.childName
                    )
                    typed_msg.setText("")
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
                    showAlert(message)
                }
                .addOnFailureListener {
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
                    (activity as ADBaseActivity).showMessage(
                        getString(R.string.message_failed),
                        mentee_parent,
                        false
                    )
                }
        }
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(activity as ADBaseActivity)
        val view = View.inflate(context, R.layout.success_msg_layout, null)
        val msg = view.findViewById<TextView>(R.id.message)
        msg.text = message


        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        val doneBtn = view.findViewById<Button>(R.id.done_btn)
        doneBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.setCancelable(false)
        alertDialog.show()
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
            mProgressDialog.show()

            (activity as ADBaseActivity).getUserDetails(object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.e(TAG, "Mentee fetched.")
                    if (data.exists()) {
                        menteeDetails = data.getValue(ADUserDetails::class.java)!!
                        if (menteeDetails != null) {
                            fetchChildData(menteeDetails.childId, false)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Mentee fetch Error : " + error.message)
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
                }
            })
        } else {

        }
    }

    private fun fetchChildData(childId: String, amtCollected: Boolean) {

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            (activity as ADBaseActivity).showMessage(
                getString(R.string.no_internet),
                mentee_parent,
                true
            )
            return
        }
        if (childId.isEmpty()) {
            (activity as ADBaseActivity).hideProgress(mProgressDialog)
            return
        }
        if ((activity as ADBaseActivity).isLoggedInUser()) {
            progress.setProgress(0)

            (activity as ADBaseActivity).getChildDetails(childId, object : ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    Log.e(TAG, "Child Fetched.")
                    if (data.exists()) {
                        if (data != null) {

                            val bannerData = data.child("videosAndImages")
                            fetchChildBanners(bannerData)
                            childData = data.getValue(ADAddChildModel::class.java)!!
                            val name = childData.childName
                            val imageUrl = childData.childImage
                            val dob = childData.dateOfBirth
                            val monthlyAmount = childData.amountNeeded.toString()
                            val guardian = childData.guardianName
                            val location = childData.city
                            var ngoName = childData.NGOName
                            if (ngoName.isEmpty()) {
                                ngoName = "NA"
                            }
                            val school = childData.schoolName
                            val dream = childData.careerInterest
                            val talent = childData.hobbies

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
                                "dd-MM-yyyy"
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
                                    getString(R.string.month_yr)
                                ).toString()

                            if (amtCollected) {
                                monthYr =
                                    MySharedPreference(activity as ADBaseActivity).getValueString(
                                        getString(R.string.next_month_yr)
                                    ).toString()
                            }

                            var collectedAmount =
                                data.child("contribution").child(monthYr).child("collected_amt")
                                    .value.toString()

                            if (collectedAmount.isEmpty() || collectedAmount.equals("null")) {
                                collectedAmount = "0"
                            }
                            if (monthlyAmount.toInt() > 0 && monthlyAmount.toInt() == collectedAmount.toInt()) {
                                fetchChildData(childId, true)
                                return
                            }
                            splitData = childData.splitDetails

                            val text = java.lang.String.format(
                                getString(R.string.fund_raised_msg),
                                collectedAmount,
                                monthlyAmount, monthYr
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

                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error : " + error.message)
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)
                }
            })
        } else {

        }
    }

    private fun fetchChildBanners(bannerData: DataSnapshot) {
        var dataUrl: ArrayList<String> = ArrayList<String>()
        for (child in bannerData.children) {
            dataUrl.add(child.value as String)
        }
        mentee_pager.offscreenPageLimit = dataUrl.size
        val pageAdapter =
            ADHomePageAdapter((activity as ADBaseActivity).applicationContext, dataUrl)
        mentee_pager.adapter = pageAdapter
        mentee_indicator.setViewPager(mentee_pager)
        pageAdapter.registerDataSetObserver(mentee_indicator.dataSetObserver)
    }
}