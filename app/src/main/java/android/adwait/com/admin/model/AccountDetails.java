package android.adwait.com.admin.model;

import android.adwait.com.utils.ADBaseModel;

import com.google.gson.annotations.SerializedName;

public class AccountDetails extends ADBaseModel {
    @SerializedName("business_name")
    private String businessName;
    @SerializedName("business_type")
    private String businessType;

    public AccountDetails(String businessName, String businessType) {
        this.businessName = businessName;
        this.businessType = businessType;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

}
