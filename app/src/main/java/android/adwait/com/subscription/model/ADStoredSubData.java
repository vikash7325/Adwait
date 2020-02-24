package android.adwait.com.subscription.model;

import android.adwait.com.utils.ADBaseModel;

public class ADStoredSubData extends ADBaseModel {

    private String id;
    private String plan_id;
    private String customer_id;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPaid_count() {
        return paid_count;
    }

    public void setPaid_count(int paid_count) {
        this.paid_count = paid_count;
    }

    public int getRemaining_count() {
        return remaining_count;
    }

    public void setRemaining_count(int remaining_count) {
        this.remaining_count = remaining_count;
    }

    private int paid_count;
    private int remaining_count;

}
