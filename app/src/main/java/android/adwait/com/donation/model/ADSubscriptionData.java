package android.adwait.com.donation.model;

import android.adwait.com.utils.ADBaseModel;

import com.google.gson.annotations.SerializedName;

public class ADSubscriptionData extends ADBaseModel {

    @SerializedName("id")
    private String id;
    @SerializedName("entity")
    private String entity;
    @SerializedName("plan_id")
    private String plan_id;
    @SerializedName("status")
    private String status;
    private int total_count;
    private int remaining_count;

    public ADSubscriptionData(String id, String entity, String plan_id, String status, int total_count, int remaining_count) {
        this.id = id;
        this.entity = entity;
        this.plan_id = plan_id;
        this.status = status;
        this.total_count = total_count;
        this.remaining_count = remaining_count;
    }

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

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getRemaining_count() {
        return remaining_count;
    }

    public void setRemaining_count(int remaining_count) {
        this.remaining_count = remaining_count;
    }
}
