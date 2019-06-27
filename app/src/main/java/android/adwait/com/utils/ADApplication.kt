package android.adwait.com.utils

import android.app.Application

class ADApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ADFontUtils.replaceFont(getApplicationContext(), "SERIF", "fonts/opensans_Reg.ttf");
    }
}