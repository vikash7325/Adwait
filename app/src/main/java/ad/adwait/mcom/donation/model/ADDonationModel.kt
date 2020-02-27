package ad.adwait.mcom.donation.model

import ad.adwait.mcom.utils.ADBaseModel
import android.os.Parcel
import android.os.Parcelable

class ADDonationModel(
    val orderId: String = "", val receiptId: String = "",
    val transactionId: String = "", val paymentMethod: String = "",
    val date: String = "", val amount: Int = 0, val status: Boolean,
    val childId: String = "", val userName: String = "", val userId: String = ""
) : ADBaseModel(), Parcelable {


    constructor() : this("","","","","",0,false,"","",""){}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeString(receiptId)
        parcel.writeString(transactionId)
        parcel.writeString(paymentMethod)
        parcel.writeString(date)
        parcel.writeInt(amount)
        parcel.writeString(status.toString())
        parcel.writeString(childId)
        parcel.writeString(userName)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
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