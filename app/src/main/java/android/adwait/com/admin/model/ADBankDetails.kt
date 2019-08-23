package android.adwait.com.admin.model

import android.adwait.com.utils.ADBaseModel
import android.os.Parcel
import android.os.Parcelable

class ADBankDetails(
    val accountNumber: String = "",
    val ifsccode: String = "",
    val holderName: String = "",
    val accountType: String = "" ) : ADBaseModel(),Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountNumber)
        parcel.writeString(ifsccode)
        parcel.writeString(holderName)
        parcel.writeString(accountType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ADBankDetails> {
        override fun createFromParcel(parcel: Parcel): ADBankDetails {
            return ADBankDetails(parcel)
        }

        override fun newArray(size: Int): Array<ADBankDetails?> {
            return arrayOfNulls(size)
        }
    }
}