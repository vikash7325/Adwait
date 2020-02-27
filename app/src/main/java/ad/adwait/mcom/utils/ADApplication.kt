package ad.adwait.mcom.utils

import androidx.multidex.MultiDexApplication

class ADApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        ad.adwait.mcom.utils.ADFontUtils.replaceFont(getApplicationContext(), "SERIF", "fonts/opensans_Reg.ttf");
    }
}