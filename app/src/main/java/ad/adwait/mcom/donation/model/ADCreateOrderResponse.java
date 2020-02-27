package ad.adwait.mcom.donation.model;

import ad.adwait.mcom.utils.ADBaseModel;
import com.google.gson.annotations.SerializedName;

public class ADCreateOrderResponse extends ADBaseModel {
    @SerializedName("msg")
    private String message;
    @SerializedName("success")
    private boolean successFlag;

    private ADCreateOrderData data;

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

    public ADCreateOrderData getData() {
        return data;
    }

    public void setData(ADCreateOrderData data) {
        this.data = data;
    }


}
