package android.adwait.com.admin.model

import android.adwait.com.utils.ADBaseModel
import android.os.Parcel
import android.os.Parcelable

class ADChildVideos(var banner1: String = "", var banner2: String = "", var video1: String = "") :
    ADBaseModel(),
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(banner1)
        dest?.writeString(banner2)
        dest?.writeString(video1)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ADChildVideos> {
        override fun createFromParcel(parcel: Parcel): ADChildVideos {
            return ADChildVideos(parcel)
        }

        override fun newArray(size: Int): Array<ADChildVideos?> {
            return arrayOfNulls(size)
        }
    }
}