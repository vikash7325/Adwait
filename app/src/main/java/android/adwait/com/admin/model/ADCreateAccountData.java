package android.adwait.com.admin.model;

import android.adwait.com.utils.ADBaseModel;

import com.google.gson.annotations.SerializedName;

public class ADCreateAccountData extends ADBaseModel {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ADCreateAccountData(String id, String entity, String name, String email) {
        this.id = id;
        this.entity = entity;
        this.name = name;
        this.email = email;
    }

    @SerializedName("id")
    private String id;
    @SerializedName("entity")
    private String entity;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
}
