package android.adwait.com.donation;

import android.adwait.com.donation.model.ADSubscriptionData;
import android.adwait.com.utils.ADBaseModel;

import com.google.gson.annotations.SerializedName;

public class ADSubscriptionResponse extends ADBaseModel {

    @SerializedName("msg")
    private String message;
    @SerializedName("success")
    private boolean successFlag;

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

    public ADSubscriptionData getData() {
        return data;
    }

    public void setData(ADSubscriptionData data) {
        this.data = data;
    }

    private ADSubscriptionData data;


}
