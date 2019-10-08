package android.adwait.com.donation.model;

import android.adwait.com.utils.ADBaseModel;

import com.google.gson.annotations.SerializedName;

public class ADCreateOrderData extends ADBaseModel {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ADCreateOrderData(String id, String entity, String amount, String status) {
        this.id = id;
        this.entity = entity;
        this.amount = amount;
        this.status = status;
    }

    @SerializedName("id")
    private String id;
    @SerializedName("entity")
    private String entity;
    @SerializedName("amount")
    private String amount;
    @SerializedName("status")
    private String status;
}
