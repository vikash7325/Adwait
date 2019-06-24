package android.adwait.com.static_pages.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.utils.ADBaseFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_hx.*

class ADHxFragment : ADBaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_hx, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        click_here.setOnClickListener(View.OnClickListener {
            (activity as ADBaseActivity).showMessage(getString(R.string.development), hx_parent, false)
        })
    }
}