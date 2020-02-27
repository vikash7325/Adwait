package ad.adwait.mcom.donation.model;

import ad.adwait.mcom.utils.ADBaseModel;
import com.google.gson.annotations.SerializedName;

public class ADSignVerifyResponse extends ADBaseModel {

    @SerializedName("msg")
    private String message;
    @SerializedName("success")
    private boolean successFlag;
    private ADSignVerifyData data;

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

    public ADSignVerifyData getData() {
        return data;
    }

    public void setData(ADSignVerifyData data) {
        this.data = data;
    }

}
