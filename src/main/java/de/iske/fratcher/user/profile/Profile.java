package de.iske.fratcher.user.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class Profile {

    private String name;

    private String aboutMe;

    @JsonIgnore
    @ElementCollection
    private List<String> interestsList;

    @Transient
    private String[] interests;

    public Profile() {
        interests = new String[0];
        interestsList = new ArrayList<>();
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String[] getInterests() {
        return interestsList.toArray(new String[interestsList.size()]);
    }

    public void setInterests(String[] interests) {
        this.interestsList.addAll(Arrays.asList(interests));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
