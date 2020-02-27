package ad.adwait.mcom.admin.model;

import ad.adwait.mcom.utils.ADBaseModel;

import ad.adwait.mcom.utils.ADBaseModel;

public class RestError extends ADBaseModel {
    public RestError(ADCreateError error) {
        this.error = error;
    }

    public ADCreateError getError() {
        return error;
    }

    @Override
    public String toString() {
        return "RestError{" +
                "error=" + error +
                '}';
    }

    public void setError(ADCreateError error) {
        this.error = error;
    }

    private ADCreateError error;

}
