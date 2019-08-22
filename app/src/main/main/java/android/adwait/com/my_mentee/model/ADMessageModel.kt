package android.adwait.com.my_mentee.model

import android.adwait.com.utils.ADBaseModel

class ADMessageModel(val messagedate:String,val messgae: String, val userName:String,
val isRead:Boolean) : ADBaseModel() {
    var messageType:Int =1
}