package ad.adwait.mcom.admin.model

import ad.adwait.mcom.utils.ADBaseModel
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

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
    var businessName: String = "",
    var businessType: String = "",
    var childImage: String = ""
) : ADBaseModel(), Parcelable {

    var keyId: String = ""
    var bankDetails: ADBankDetails = ADBankDetails()
    var splitDetails: ADMoneySplitUp = ADMoneySplitUp()
    var videosAndImages: ADChildVideos = ADChildVideos()

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
        keyId = parcel.readString()
        bankDetails = parcel.readParcelable(ADBankDetails::class.java.classLoader)
        splitDetails = parcel.readParcelable(ADMoneySplitUp::class.java.classLoader)
        videosAndImages = parcel.readParcelable(ADChildVideos::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(childName)
        parcel.writeString(dateOfBirth)
        parcel.writeString(mentorName)
        parcel.writeString(address)
        parcel.writeString(email)
        parcel.writeLong(amountNeeded)
        parcel.writeString(careerInterest)
        parcel.writeString(guardianName)
        parcel.writeString(hobbies)
        parcel.writeString(pincode)
        parcel.writeString(city)
        parcel.writeString(schoolName)
        parcel.writeString(NGOName)
        parcel.writeString(accountId)
        parcel.writeString(businessName)
        parcel.writeString(businessType)
        parcel.writeString(childImage)
        parcel.writeString(keyId)
        parcel.writeParcelable(bankDetails, flags)
        parcel.writeParcelable(splitDetails, flags)
        parcel.writeParcelable(videosAndImages, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ADAddChildModel> {
        override fun createFromParcel(parcel: Parcel): ADAddChildModel {
            return ADAddChildModel(parcel)
        }

        override fun newArray(size: Int): Array<ADAddChildModel?> {
            return arrayOfNulls(size)
        }
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "childName" to childName,
            "dateOfBirth" to dateOfBirth,
            "mentorName" to mentorName,
            "address" to address,
            "email" to email,
            "amountNeeded" to amountNeeded,
            "careerInterest" to careerInterest,
            "guardianName" to guardianName,
            "hobbies" to hobbies,
            "pincode" to pincode,
            "city" to city,
            "schoolName" to schoolName,
            "NGOName" to NGOName,
            "businessName" to businessName,
            "businessType" to businessType,
            "childImage" to childImage,
            "bankDetails" to bankDetails,
            "splitDetails" to splitDetails
        )
    }
}