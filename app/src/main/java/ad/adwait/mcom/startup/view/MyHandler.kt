package ad.adwait.mcom.startup.view

import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

class MyHandler(activity: ADSplashActivity) : Handler() {
    private val mActivity: WeakReference<ADSplashActivity>

    init {
        mActivity = WeakReference<ADSplashActivity>(activity)
    }

    override fun handleMessage(msg: Message) {
        val activity = mActivity.get()
        if (activity != null) {

        }
    }
}