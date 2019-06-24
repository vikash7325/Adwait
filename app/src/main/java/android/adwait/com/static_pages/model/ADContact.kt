package android.adwait.com.static_pages.model

import android.adwait.com.utils.ADBaseModel

class ADContact(
    val userName: String = "",
    var subject: String = "",
    val contactDetails: String = "",
    var message: String = ""
) : ADBaseModel() {
}