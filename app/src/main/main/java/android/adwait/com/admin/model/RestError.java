package android.adwait.com.admin.model;

import android.adwait.com.utils.ADBaseModel;

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
