package android.adwait.com.admin.model;

import android.adwait.com.utils.ADBaseModel;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ADTransferData extends ADBaseModel implements Parcelable {

    @SerializedName("msg")
    private String message;
    @SerializedName("success")
    private boolean successFlag;

    protected ADTransferData(Parcel in) {
        message = in.readString();
        successFlag = in.readByte() != 0;
    }

    public static final Creator<ADTransferData> CREATOR = new Creator<ADTransferData>() {
        @Override
        public ADTransferData createFromParcel(Parcel in) {
            return new ADTransferData(in);
        }

        @Override
        public ADTransferData[] newArray(int size) {
            return new ADTransferData[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(boolean successFlag) {
        this.successFlag = successFlag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeByte((byte) (successFlag ? 1 : 0));
    }
}
