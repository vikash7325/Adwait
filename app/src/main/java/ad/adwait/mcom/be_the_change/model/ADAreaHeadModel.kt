package ad.adwait.mcom.be_the_change.model

import ad.adwait.mcom.utils.ADBaseModel

class ADAreaHeadModel(
    val userId: String,
    val name: String,
    val phone: String,
    val email: String,
    val location: String,
    val city: String,
    val state: String
) : ADBaseModel() {
}