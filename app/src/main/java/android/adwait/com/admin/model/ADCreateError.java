package android.adwait.com.admin.model;

import android.adwait.com.utils.ADBaseModel;

import com.google.gson.annotations.SerializedName;

public class ADCreateError extends ADBaseModel {
    @SerializedName("code")
    private String code;

    public ADCreateError(String code, String description, String field) {
        this.code = code;
        this.description = description;
        this.field = field;
    }

    @Override
    public String toString() {
        return "ADCreateError{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", field='" + field + '\'' +
                '}';
    }

    @SerializedName("description")
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @SerializedName("field")
    private String field;
}
