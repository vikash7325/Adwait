package ad.adwait.mcom.donation.model;

import ad.adwait.mcom.utils.ADBaseModel;

import com.google.gson.annotations.SerializedName;

public class ADPaymentMethodResponse extends ADBaseModel {

    @SerializedName("msg")
    private String message;
    @SerializedName("success")
    private boolean successFlag;
    private ADPaymentMethodData data;

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

    public ADPaymentMethodData getData() {
        return data;
    }

    public void setData(ADPaymentMethodData data) {
        this.data = data;
    }

}
