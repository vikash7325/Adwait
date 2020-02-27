package ad.adwait.mcom.subscription.model

import ad.adwait.mcom.utils.ADBaseModel

class ADSubscriptionRequest(
    var amount: Int = 0,
    var account_id: String = "",
    var customer_id: String = "",
    var user_id: String = "",
    var user_name: String = "",
    var child_id: String = ""
) : ADBaseModel() {
    override fun toString(): String {
        return "ADSubscriptionRequest(amount=$amount, account_id='$account_id', customer_id='$customer_id', user_id='$user_id', user_name='$user_name', child_id='$child_id')"
    }

}