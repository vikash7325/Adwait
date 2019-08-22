package android.adwait.com.be_the_change.model

import android.adwait.com.utils.ADBaseModel

class ADDayOutModel(
        val userId: String, val name: String, val date: String, val contactDetails: String,
        val message: String
) : ADBaseModel() {
}