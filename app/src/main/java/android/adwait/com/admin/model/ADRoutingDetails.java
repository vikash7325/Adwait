package android.adwait.com.admin.model;

import android.adwait.com.utils.ADBaseModel;

public class ADRoutingDetails extends ADBaseModel {

    private String transferDate;

    public ADRoutingDetails(String transferDate, boolean transfered) {
        this.transferDate = transferDate;
        this.transfered = transfered;
    }

    private boolean transfered;

}
