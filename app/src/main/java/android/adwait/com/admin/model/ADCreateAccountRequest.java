package android.adwait.com.admin.model;

import android.adwait.com.utils.ADBaseModel;

import com.google.gson.annotations.SerializedName;

public class ADCreateAccountRequest extends ADBaseModel {


    @SerializedName("name")
    private String userName;
    @SerializedName("email")
    private String userEmail;
    @SerializedName("tnc_accepted")
    private boolean tnc_accepted;

    @SerializedName("bank_account")
    private ADBankAccount bankAccount;
    @SerializedName("account_details")
    private AccountDetails accountDetails;

    @Override
    public String toString() {
        return "ADCreateAccountRequest{" +
                "userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", tnc_accepted=" + tnc_accepted +
                ", bankAccount=" + bankAccount +
                ", accountDetails=" + accountDetails +
                '}';
    }

    public ADCreateAccountRequest(String userName, String userEmail, boolean tnc_accepted, ADBankAccount bankAccount, AccountDetails accountDetails) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.tnc_accepted = tnc_accepted;
        this.bankAccount = bankAccount;
        this.accountDetails = accountDetails;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isTnc_accepted() {
        return tnc_accepted;
    }

    public void setTnc_accepted(boolean tnc_accepted) {
        this.tnc_accepted = tnc_accepted;
    }

    public ADBankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(ADBankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public AccountDetails getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(AccountDetails accountDetails) {
        this.accountDetails = accountDetails;
    }
}