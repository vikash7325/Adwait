package ad.adwait.mcom.admin.model;

import ad.adwait.mcom.utils.ADBaseModel;
import com.google.gson.annotations.SerializedName;

import ad.adwait.mcom.utils.ADBaseModel;

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
