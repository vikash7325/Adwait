package android.adwait.com.my_contribution.view

import android.adwait.com.R
import android.adwait.com.utils.ADBaseFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ADMyContributions : ADBaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_my_contributions, container, false)

        return view
    }
}