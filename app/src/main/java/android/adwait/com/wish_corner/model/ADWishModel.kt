package android.adwait.com.wish_corner.model

import android.adwait.com.utils.ADBaseModel
import android.os.Parcel
import android.os.Parcelable

class ADWishModel(
    var name: String = "",
    var birthday: String = "",
    var age: Int = 0,
    var ngo: String = "",
    var wishItem: String = "",
    var price: Int = 0,
    var donatedBy: String = "",
    var fulFilled: String = "",
    var addedBy: String = "",
    var childAim: String = ""
) : ADBaseModel(), Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(birthday)
        parcel.writeInt(age)
        parcel.writeString(ngo)
        parcel.writeString(wishItem)
        parcel.writeInt(price)
        parcel.writeString(donatedBy)
        parcel.writeString(fulFilled)
        parcel.writeString(addedBy)
        parcel.writeString(keyId)
        parcel.writeString(childAim)
    }

    override fun describeContents(): Int {
        return 0
    }

    var keyId: String = ""

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
        keyId = parcel.readString()
    }

    companion object CREATOR : Parcelable.Creator<ADWishModel> {
        override fun createFromParcel(parcel: Parcel): ADWishModel {
            return ADWishModel(parcel)
        }

        override fun newArray(size: Int): Array<ADWishModel?> {
            return arrayOfNulls(size)
        }
    }
}