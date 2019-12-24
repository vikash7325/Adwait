package android.adwait.com.my_contribution.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.donation.model.ADDonationModel
import android.adwait.com.my_contribution.adapter.ADContributionAdapter
import android.adwait.com.utils.ADBaseFragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_my_contributions.*

class ADMyContributionsFragment : ADBaseFragment() {

    private lateinit var mProgressDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_my_contributions, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserDetails()
    }

    public fun getUserDetails() {
        val preference = MySharedPreference(activity!!.applicationContext)
        val userId = preference.getValueString(getString(R.string.userId)).toString()
        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            return
        }

        if ((activity as ADBaseActivity).isLoggedInUser()) {

            mProgressDialog = (activity as ADBaseActivity).showProgressDialog("", false)
            mProgressDialog.show()

            val mContributionData = ArrayList<ADDonationModel>()
            FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).CONTRIBUTION_TABLE_NAME)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.exists()) {

                            for (data in dataSnapshot.children) {

                                val list = data.children

                                list.forEach {
                                    val item = it.getValue(ADDonationModel::class.java)
                                    if (item?.userId.equals(userId)) {
                                        mContributionData.add(item!!)
                                    }
                                }
                            }

                            val adapter =
                                ADContributionAdapter(
                                    (activity as ADBaseActivity),
                                    mContributionData)
                            contribution_list.setAdapter(adapter)
                            contribution_layout.visibility = View.VISIBLE
                            no_contribution.visibility = View.GONE
                            (activity as ADBaseActivity).hideProgress(mProgressDialog)

                        }else{
                            contribution_layout.visibility = View.GONE
                            no_contribution.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        (activity as ADBaseActivity).hideProgress(mProgressDialog)
                        contribution_layout.visibility = View.GONE
                        no_contribution.visibility = View.VISIBLE     }
                })

        }
    }
}