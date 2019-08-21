package android.adwait.com.admin.model

import android.adwait.com.utils.ADBaseModel

class ADBankDetails(
    val accountNumber: String = "",
    val IFSCCode: String = "",
    val holderName: String = "",
    val accountType: String = "" ) : ADBaseModel() {
}