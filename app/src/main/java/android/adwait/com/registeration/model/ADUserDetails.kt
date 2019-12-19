package android.adwait.com.registeration.model

import android.adwait.com.utils.ADBaseModel
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

class ADUserDetails(
    val userName: String = "",
    val password: String = "",
    var phoneNumber: String = "",
    val emailAddress: String = "",
    var date_of_birth: String = ""
) : ADBaseModel(), Parcelable {
    var usedReferralCode: String = ""
    var myReferralCode: String = ""
    var childId: String = ""
    var can_add_data: Boolean = false
    var referralPoints: String = "100"
    var lastTransaction: ADLastCheckout = ADLastCheckout()


    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userName" to userName,
            "password" to password,
            "phoneNumber" to phoneNumber,
            "date_of_birth" to date_of_birth
        )
    }

    @Exclude
    fun toMap2(): Map<String, Any?> {
        return mapOf(
            "lastTransaction" to lastTransaction
        )
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
        usedReferralCode = parcel.readString()
        myReferralCode = parcel.readString()
        childId = parcel.readString()
        can_add_data = parcel.readByte() != 0.toByte()
        referralPoints = parcel.readString()
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userName)
        parcel.writeString(password)
        parcel.writeString(phoneNumber)
        parcel.writeString(emailAddress)
        parcel.writeString(date_of_birth)
        parcel.writeString(usedReferralCode)
        parcel.writeString(myReferralCode)
        parcel.writeString(childId)
        parcel.writeByte(if (can_add_data) 1 else 0)
        parcel.writeString(referralPoints)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ADUserDetails> {
        override fun createFromParcel(parcel: Parcel): ADUserDetails {
            return ADUserDetails(parcel)
        }

        override fun newArray(size: Int): Array<ADUserDetails?> {
            return arrayOfNulls(size)
        }
    }
}