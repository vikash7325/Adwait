package android.adwait.com.subscription.model;

import android.adwait.com.utils.ADBaseModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ADStoredSubResponse extends ADBaseModel {
    @SerializedName("msg")
    private String message;
    @SerializedName("success")
    private boolean successFlag;

    private List<ADStoredSubData> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(boolean successFlag) {
        this.successFlag = successFlag;
    }

    public List<ADStoredSubData> getData() {
        return data;
    }

    public void setData(List<ADStoredSubData> data) {
        this.data = data;
    }


}
