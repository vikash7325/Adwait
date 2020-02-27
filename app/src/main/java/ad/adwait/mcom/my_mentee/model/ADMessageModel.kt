package ad.adwait.mcom.my_mentee.model

import ad.adwait.mcom.utils.ADBaseModel

class ADMessageModel(val messagedate:String,val messgae: String, val userName:String,
val isRead:Boolean) : ADBaseModel() {
    var messageType:Int =1
}