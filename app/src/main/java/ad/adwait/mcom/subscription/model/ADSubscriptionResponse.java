package ad.adwait.mcom.subscription.model;

import ad.adwait.mcom.utils.ADBaseModel;

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

    public ADStoredSubData getData() {
        return data;
    }

    public void setData(ADStoredSubData data) {
        this.data = data;
    }

    private ADStoredSubData data;


}
