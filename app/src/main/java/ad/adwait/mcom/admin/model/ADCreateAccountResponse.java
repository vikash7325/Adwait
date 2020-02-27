package ad.adwait.mcom.admin.model;

import ad.adwait.mcom.utils.ADBaseModel;
import com.google.gson.annotations.SerializedName;

import ad.adwait.mcom.utils.ADBaseModel;

public class ADCreateAccountResponse extends ADBaseModel {

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

    public ADCreateAccountData getData() {
        return data;
    }

    public void setData(ADCreateAccountData data) {
        this.data = data;
    }

    private ADCreateAccountData data;
}
