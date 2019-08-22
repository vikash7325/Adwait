package android.adwait.com.admin.model;

public class Sample {
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Sample(int count, String entity) {
        this.count = count;
        this.entity = entity;
    }

    private String entity;

}
