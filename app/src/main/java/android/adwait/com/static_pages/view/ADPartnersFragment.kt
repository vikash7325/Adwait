package android.adwait.com.static_pages.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.dashboard.view.ADDashboardActivity
import android.adwait.com.utils.ADBaseFragment
import android.adwait.com.utils.ADConstants
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_partners.*

class ADPartnersFragment : ADBaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_partners, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previous_btn.setOnClickListener {
            page2.visibility = View.GONE
            page1.visibility = View.VISIBLE
        }

        next_btn.setOnClickListener {
            page1.visibility = View.GONE
            page2.visibility = View.VISIBLE
        }

        click_here.setOnClickListener{
            (activity as ADDashboardActivity).menuAction(ADConstants.MENU_CONTACT_US, "")

        }
    }
}