package android.adwait.com.admin.model

import android.adwait.com.utils.ADBaseModel

class ADAddChildModel(
    var childName: String = "",
    var dateOfBirth: String = "",
    var mentorName: String = "",
    var address: String = "",
    var email: String = "",
    var amountNeeded: Long = 0,
    var careerInterest: String = "",
    var guardianName: String = "",
    var hobbies: String = "",
    var pincode: String = "",
    var city: String = "",
    var schoolName: String = "",
    var NGOName: String = "",
    var accountId: String = "",
    var childImage: String = ""
) : ADBaseModel() {

    var bankDetails: ADBankDetails = ADBankDetails()
    var splitDetails: ADMoneySplitUp = ADMoneySplitUp()

}