package and.com.polam.utils

import android.content.Context

class MySharedPreference(val context: Context) {
    val PREFERENCENAME = "adwait_L0c_data"

    val sharedPreference = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE)


    fun saveStrings(key: String, values: String) {
        val editor = sharedPreference.edit()
        editor.putString(key, values)
        editor.apply()
    }

    fun getValueString(Key: String): String? {
        return sharedPreference.getString(Key, null)
    }


    fun saveBoolean(key: String, values: Boolean) {
        val editor = sharedPreference.edit()
        editor.putBoolean(key, values)
        editor.apply()
    }

    fun getValueBoolean(Key: String): Boolean {
        return sharedPreference.getBoolean(Key, false)
    }
}