package ad.adwait.mcom.donation.model

import ad.adwait.mcom.utils.ADBaseModel

class ADSignVerifyRequest(var order_id: String = "", var signature: String = ""):ADBaseModel() {
}