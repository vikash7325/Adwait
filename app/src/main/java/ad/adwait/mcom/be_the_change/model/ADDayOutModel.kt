package ad.adwait.mcom.be_the_change.model

import ad.adwait.mcom.utils.ADBaseModel

class ADDayOutModel(
        val userId: String, val name: String, val date: String, val contactDetails: String,
        val message: String
) : ADBaseModel() {
}