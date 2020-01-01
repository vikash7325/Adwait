package android.adwait.com.donation.model

import android.adwait.com.utils.ADBaseModel

class ADStoredSubRequest(
    var receipt_no: String = "", var order_amount: Int = 0
    , var order_currency: String = "INR"
) : ADBaseModel() {
    override fun toString(): String {
        return "ADCreateOrderRequest(receipt_no='$receipt_no', order_amount=$order_amount, order_currency='$order_currency')"
    }
}