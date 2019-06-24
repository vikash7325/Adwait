package android.adwait.com.my_mentee.model

import android.adwait.com.utils.ADBaseModel
import android.os.Parcel
import android.os.Parcelable

class ADChildModel(
    val name: String = "",
    val date_of_birth: String = "",
    val mentor: String = "",
    val address: String = "",
    val amount_needed: Long = 0,
    val career_interest: String = "",
    val guardian_name: String = "",
    val hobby: String = "",
    val pincode: String = "",
    val school_name: String = "",
    val userName: String = "",
    val child_image: String = ""
) : ADBaseModel(),Parcelable {

    constructor(parcel: Parcel) : this(
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
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(date_of_birth)
        parcel.writeString(mentor)
        parcel.writeString(address)
        parcel.writeLong(amount_needed)
        parcel.writeString(career_interest)
        parcel.writeString(guardian_name)
        parcel.writeString(hobby)
        parcel.writeString(pincode)
        parcel.writeString(school_name)
        parcel.writeString(userName)
        parcel.writeString(child_image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ADChildModel> {
        override fun createFromParcel(parcel: Parcel): ADChildModel {
            return ADChildModel(parcel)
        }

        override fun newArray(size: Int): Array<ADChildModel?> {
            return arrayOfNulls(size)
        }
    }
}