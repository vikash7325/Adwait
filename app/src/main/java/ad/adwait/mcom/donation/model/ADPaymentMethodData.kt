package ad.adwait.mcom.donation.model

import ad.adwait.mcom.utils.ADBaseModel

class ADPaymentMethodData(
    var id: String = "", var entity: String = "", var currency: String = ""
    , var status: String = "", var method: String = "", var amount: Int = 0
) : ADBaseModel() {
}