package ad.adwait.mcom.be_the_change.model

import ad.adwait.mcom.utils.ADBaseModel

class ADCampusambassadorModel(
        val userId: String, val areaOfInterest: String, val areaOfExpert: String, val location: String,
        val phone: String
) : ADBaseModel() {
}