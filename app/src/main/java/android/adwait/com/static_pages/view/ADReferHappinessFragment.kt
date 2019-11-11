package android.adwait.com.static_pages.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.registeration.model.ADUserDetails
import android.adwait.com.utils.ADBaseFragment
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_referral.*


class ADReferHappinessFragment : ADBaseFragment() {

    private var mReferralCode = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_referral, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val baseActivity = activity as ADBaseActivity
        val pagerHeight: Int = (baseActivity.getScreenDetails(true) * 0.25).toInt()

        var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pagerHeight)
        referral_header.layoutParams = params

        whatsapp_layout.setOnClickListener(View.OnClickListener {
            openSharingApps(
                getString(R.string.referral_message),
                "com.whatsapp",
                getString(R.string.whatsapp)
            )
        })

        facebook_layout.setOnClickListener(View.OnClickListener {
            openSharingApps(
                getString(R.string.referral_message),
                "com.facebook.katana",
                getString(R.string.facebook)
            )
        })

        twitter_layout.setOnClickListener(View.OnClickListener {
            openSharingApps(
                getString(R.string.referral_message),
                "com.twitter.android",
                getString(R.string.twitter)
            )
        })

        others.setOnClickListener(View.OnClickListener {
            openSharingApps(
                getString(R.string.referral_message),
                "",
                getString(R.string.others)
            )
        })

        copy.setOnClickListener(View.OnClickListener {
            val clipBoardMan: ClipboardManager =
                (activity as ADBaseActivity).getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val data = ClipData.newPlainText("Referral Code", mReferralCode)
            clipBoardMan.primaryClip = data
            Toast.makeText(activity, "Referral code copied!!", Toast.LENGTH_SHORT)
                .show()
        })

        progress_layout.visibility = View.VISIBLE

        (activity as ADBaseActivity).getUserDetails(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {
                progress_layout.visibility = View.GONE

                if (data.exists()) {
                    var userData = data.getValue(ADUserDetails::class.java)
                    if (userData != null) {
                        your_earning.setText(userData.referralPoints)
                        mReferralCode = userData.myReferralCode
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                progress_layout.visibility = View.GONE
            }

        })

    }

    fun openSharingApps(message: String, packageName: String, appName: String) {

        val pm = activity?.packageManager
        val refMessage = java.lang.String.format(message, mReferralCode)
        try {

            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = "text/plain"
            if (!packageName.isEmpty()) {
                pm?.getPackageInfo(packageName, PackageManager.GET_META_DATA)
                waIntent.setPackage(packageName)
            }
            waIntent.putExtra(Intent.EXTRA_TEXT, refMessage)
            waIntent.putExtra(Intent.EXTRA_SUBJECT, "Way to share your Happiness!!")
            startActivity(Intent.createChooser(waIntent, "Share with"))
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(activity, appName + " not Installed", Toast.LENGTH_SHORT)
                .show()
        }

    }
}