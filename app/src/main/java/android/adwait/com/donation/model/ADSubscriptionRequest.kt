package android.adwait.com.donation.model

import android.adwait.com.utils.ADBaseModel

class ADSubscriptionRequest(
    var amount: Int = 0,
    var account_id: String = "",
    var customer_id: String = ""
) : ADBaseModel() {
}