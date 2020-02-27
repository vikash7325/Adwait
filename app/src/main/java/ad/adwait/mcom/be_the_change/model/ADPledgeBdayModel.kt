package ad.adwait.mcom.be_the_change.model

import ad.adwait.mcom.utils.ADBaseModel

class ADPledgeBdayModel(
        val userId: String, val name: String, val birthday: String, val contact: String
) : ADBaseModel() {
}