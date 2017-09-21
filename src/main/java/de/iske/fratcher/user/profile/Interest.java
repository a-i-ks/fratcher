package de.iske.fratcher.user.profile;

import javax.persistence.Entity;

@Entity
public class Interest {

    private String value;

    private Integer count;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
