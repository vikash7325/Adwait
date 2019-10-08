package android.adwait.com.utils

import android.support.multidex.MultiDexApplication

class ADApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        ADFontUtils.replaceFont(getApplicationContext(), "SERIF", "fonts/opensans_Reg.ttf");
    }
}