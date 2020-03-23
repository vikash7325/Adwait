package ad.adwait.mcom.static_pages.view

import ad.adwait.mcom.R
import ad.adwait.mcom.utils.ADBaseFragment
import and.com.polam.utils.CommonUtils
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_hx.*

class ADHxFragment : ADBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_hx, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        click_here.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_url)))
            startActivity(intent)
        }

        point2.setText(CommonUtils.getHtmlText(getString(R.string.point2)))
        point3.setText(CommonUtils.getHtmlText(getString(R.string.point3)))

    }
}