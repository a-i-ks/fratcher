package de.iske.fratcher.user.profile;

import javax.persistence.Embeddable;

@Embeddable
public class Profile {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
