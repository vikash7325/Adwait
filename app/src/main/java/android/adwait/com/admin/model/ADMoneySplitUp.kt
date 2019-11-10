package android.adwait.com.admin.model

import android.adwait.com.utils.ADBaseModel
import android.os.Parcel
import android.os.Parcelable

class ADMoneySplitUp(
    val educationAmount: Int = 0,
    val foodAmount: Int = 0,
    val hobbiesAmount: Int = 0,
    val necessityAmount: Int = 0,
    val extraAmount: Int = 0
) : ADBaseModel(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(educationAmount)
        parcel.writeInt(foodAmount)
        parcel.writeInt(hobbiesAmount)
        parcel.writeInt(necessityAmount)
        parcel.writeInt(extraAmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "ADMoneySplitUp(educationAmount=$educationAmount, foodAmount=$foodAmount, hobbiesAmount=$hobbiesAmount, necessityAmount=$necessityAmount, extraAmount=$extraAmount)"
    }

    companion object CREATOR : Parcelable.Creator<ADMoneySplitUp> {
        override fun createFromParcel(parcel: Parcel): ADMoneySplitUp {
            return ADMoneySplitUp(parcel)
        }

        override fun newArray(size: Int): Array<ADMoneySplitUp?> {
            return arrayOfNulls(size)
        }
    }
}