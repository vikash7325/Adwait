package android.adwait.com.donation.model

import android.adwait.com.utils.ADBaseModel

class ADCreateOrderRequest(
    var receipt_no: String = "", var order_amount: Int = 0
    , var order_currency: String = "INR"
) : ADBaseModel() {
}