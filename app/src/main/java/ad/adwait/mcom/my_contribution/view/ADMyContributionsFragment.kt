package ad.adwait.mcom.my_contribution.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import ad.adwait.mcom.R
import ad.adwait.mcom.donation.model.ADDonationModel
import ad.adwait.mcom.my_contribution.adapter.ADContributionAdapter
import ad.adwait.mcom.utils.ADBaseFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_my_contributions.*

class ADMyContributionsFragment : ADBaseFragment() {

    private lateinit var mProgressDialog: AlertDialog
    private lateinit var listener: ValueEventListener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_my_contributions, container, false)

        listener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val mContributionData = ArrayList<ADDonationModel>()
                    val preference = MySharedPreference(activity!!.applicationContext)
                    val userId = preference.getValueString(getString(R.string.userId)).toString()
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
                            mContributionData
                        )
                    contribution_list.setAdapter(adapter)
                    contribution_layout.visibility = View.VISIBLE
                    no_contribution.visibility = View.GONE
                    (activity as ADBaseActivity).hideProgress(mProgressDialog)

                } else {
                    contribution_layout.visibility = View.GONE
                    no_contribution.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                (activity as ADBaseActivity).hideProgress(mProgressDialog)
                contribution_layout.visibility = View.GONE
                no_contribution.visibility = View.VISIBLE
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserDetails()
    }

    public fun getUserDetails() {

        if (!(activity as ADBaseActivity).isNetworkAvailable()) {
            return
        }

        if ((activity as ADBaseActivity).isLoggedInUser()) {

            mProgressDialog = (activity as ADBaseActivity).showProgressDialog("", false)
            mProgressDialog.show()

            FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).CONTRIBUTION_TABLE_NAME)
                .addValueEventListener(listener)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseDatabase.getInstance()
            .reference.child((activity as ADBaseActivity).CONTRIBUTION_TABLE_NAME)
            .removeEventListener(listener)
    }
}