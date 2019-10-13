package android.adwait.com.admin.model;

import android.adwait.com.utils.ADBaseModel;
import android.os.Parcel;
import android.os.Parcelable;

public class ADRoutingDetails extends ADBaseModel implements Parcelable {

    public String transferDate;
    public boolean transfered;

    public ADRoutingDetails(String transferDate, boolean transfered) {
        this.transferDate = transferDate;
        this.transfered = transfered;
    }


    protected ADRoutingDetails(Parcel in) {
        transferDate = in.readString();
        transfered = in.readByte() != 0;
    }

    public static final Creator<ADRoutingDetails> CREATOR = new Creator<ADRoutingDetails>() {
        @Override
        public ADRoutingDetails createFromParcel(Parcel in) {
            return new ADRoutingDetails(in);
        }

        @Override
        public ADRoutingDetails[] newArray(int size) {
            return new ADRoutingDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transferDate);
        dest.writeByte((byte) (transfered ? 1 : 0));
    }
}
