package ad.adwait.mcom.be_the_change.model

import ad.adwait.mcom.utils.ADBaseModel

class ADOrganiseEventModel(
    val userId: String, val eventName: String, val eventLocation: String, val eventDate: String,
    val eventTime: String, val eventType: String, val eventDescription: String
) : ADBaseModel() {
    var eventBannerUrl: String = ""
    var eventPrivacy: String = "Public"
    var eventInviteOthers: Boolean = false
    var eventAutoCheckIn: Boolean = false
    var eventAllowGift: Boolean = false
    var eventDocumentsUrl: String = ""
}