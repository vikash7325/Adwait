package android.adwait.com.admin.model;

import android.adwait.com.utils.ADBaseModel;

import com.google.gson.annotations.SerializedName;

public class ADBankAccount extends ADBaseModel {
    @SerializedName("ifsc_code")
    private String IFSCCode;
    @SerializedName("beneficiary_name")
    private String beneficiaryName;
    @SerializedName("account_type")
    private String accountType;
    @SerializedName("account_number")
    private String accountNumber;
    public ADBankAccount(String IFSCCode, String beneficiaryName, String accountType, String accountNumber) {
        this.IFSCCode = IFSCCode;
        this.beneficiaryName = beneficiaryName;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
    }

    public String getIFSCCode() {
        return IFSCCode;
    }

    public void setIFSCCode(String IFSCCode) {
        this.IFSCCode = IFSCCode;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

}
