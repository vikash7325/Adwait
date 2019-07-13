package android.adwait.com.wish_corner.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.utils.ADBaseFragment
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_wish_corner.*

class ADWishCornerFragment : ADBaseFragment() {

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
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })


        (activity as ADBaseActivity).showMessage(
            getString(R.string.development),
            wish_parent,
            false
        )
    }
}