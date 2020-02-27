package ad.adwait.mcom.static_pages.model

import ad.adwait.mcom.utils.ADBaseModel

class ADContact(
    val userName: String = "",
    var subject: String = "",
    val contactDetails: String = "",
    var message: String = ""
) : ADBaseModel() {
}