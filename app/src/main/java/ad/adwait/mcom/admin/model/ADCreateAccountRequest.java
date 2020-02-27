package ad.adwait.mcom.admin.model;

import ad.adwait.mcom.utils.ADBaseModel;

import com.google.gson.annotations.SerializedName;

import ad.adwait.mcom.utils.ADBaseModel;

public class ADCreateAccountRequest extends ADBaseModel {



    @SerializedName("beneficiary_name")
    private String beneficiaryName;
    @SerializedName("beneficiary_email")
    private String beneficiaryEmail;
    @SerializedName("business_name")
    private String businessName;
    @SerializedName("business_type")
    private String businessType;
    @SerializedName("ifsc_code")
    private String IFSCCode;
    @SerializedName("account_type")
    private String accountType;
    @SerializedName("account_number")
    private long accountNumber;

    public ADCreateAccountRequest(String beneficiaryName, String beneficiaryEmail,
                                  String businessName, String businessType, String IFSCCode,
                                  String accountType, long accountNumber) {
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryEmail = beneficiaryEmail;
        this.businessName = businessName;
        this.businessType = businessType;
        this.IFSCCode = IFSCCode;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryEmail() {
        return beneficiaryEmail;
    }

    public void setBeneficiaryEmail(String beneficiaryEmail) {
        this.beneficiaryEmail = beneficiaryEmail;
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

    public String getIFSCCode() {
        return IFSCCode;
    }

    public void setIFSCCode(String IFSCCode) {
        this.IFSCCode = IFSCCode;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "ADCreateAccountRequest{" +
                "beneficiaryName='" + beneficiaryName + '\'' +
                ", beneficiaryEmail='" + beneficiaryEmail + '\'' +
                ", businessName='" + businessName + '\'' +
                ", businessType='" + businessType + '\'' +
                ", IFSCCode='" + IFSCCode + '\'' +
                ", accountType='" + accountType + '\'' +
                ", accountNumber=" + accountNumber +
                '}';
    }

}