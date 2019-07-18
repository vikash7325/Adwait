package android.adwait.com.wish_corner.model

import android.adwait.com.utils.ADBaseModel

class ADWishModel(
    var name: String ="", var birthday: String ="", var age: Int=0, var ngo: String ="",
    var wishItem: String ="", var price: Int=0, var donatedBy: String ="", var fulFilled: String ="", var addedBy: String =""
) : ADBaseModel() {
}