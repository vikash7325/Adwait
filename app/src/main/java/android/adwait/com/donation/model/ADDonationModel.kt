package android.adwait.com.donation.model

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.utils.ADBaseModel
import android.os.Parcel
import android.os.Parcelable

class ADDonationModel(
    val transactionId: String = "", val paymentMethod: String = "",
    val date: String = "", val amount: String = "", val status: Boolean,
    val childName: String = "", val userName: String = ""
) : ADBaseModel(), Parcelable {


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(transactionId)
        parcel.writeString(paymentMethod)
        parcel.writeString(date)
        parcel.writeString(amount)
        parcel.writeString(status.toString())
        parcel.writeString(childName)
        parcel.writeString(userName)
    }

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    companion object CREATOR : Parcelable.Creator<ADDonationModel> {
        override fun createFromParcel(parcel: Parcel): ADDonationModel {
            return ADDonationModel(parcel)
        }

        override fun newArray(size: Int): Array<ADDonationModel?> {
            return arrayOfNulls(size)
        }
    }
}