package android.adwait.com.wish_corner.model

import android.adwait.com.utils.ADBaseModel

class ADEventsModel(
    var eventName: String ="", var date: String ="", var venue: String ="",
    var description: String ="", var organizedBy: String ="", var imageUrl: String ="") : ADBaseModel() {
}