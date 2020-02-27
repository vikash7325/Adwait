package ad.adwait.mcom.registeration.model

import ad.adwait.mcom.utils.ADBaseModel

class ADLastCheckout(
    val amount: Int = 0,
    val method: String = ""
) : ADBaseModel() {
}