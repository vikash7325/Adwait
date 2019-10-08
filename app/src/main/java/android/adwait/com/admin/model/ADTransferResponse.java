package android.adwait.com.admin.model;

import android.adwait.com.utils.ADBaseModel;
import com.google.gson.annotations.SerializedName;

public class ADTransferResponse extends ADBaseModel {

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

    public ADTransferData getData() {
        return data;
    }

    public void setData(ADTransferData data) {
        this.data = data;
    }

    private ADTransferData data;
}
