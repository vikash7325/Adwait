package android.adwait.com.admin.model

import android.adwait.com.utils.ADBaseModel

class ADMoneySplitUp(
    val educationAmount: Int = 0,
    val foodAmount: Int = 0,
    val hobbiesAmount: Int = 0,
    val necessityAmount: Int = 0,
    val extraAmount: Int = 0
    ) : ADBaseModel() {
}