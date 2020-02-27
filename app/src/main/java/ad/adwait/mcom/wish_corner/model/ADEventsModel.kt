package ad.adwait.mcom.wish_corner.model

import ad.adwait.mcom.utils.ADBaseModel

class ADEventsModel(
    var eventName: String ="", var date: String ="", var venue: String ="",
    var description: String ="", var organizedBy: String ="", var imageUrl: String ="") : ADBaseModel() {
}