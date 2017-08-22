package de.iske.fratcher.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.iske.fratcher.match.Match;
import de.iske.fratcher.user.profile.Profile;
import de.iske.fratcher.util.Status;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// Renaming because "User" is keyword for PostgreSQL
@Entity(name =  "User_")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String username;

    @JsonIgnore
    private String password;

    private String email;

    @OneToOne(mappedBy = "user")
    private Profile profile;

    @OneToMany(targetEntity = Match.class, cascade = CascadeType.ALL)
    private List<Match> matches;

    public User() {
        matches = new ArrayList<>();
        status = Status.DEFAULT;
        userType = UserType.USER;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // See https://stackoverflow.com/questions/17027777/relationship-between-hashcode-and-equals-method-in-java for
    // an explanation why we override both equals() and hashCode().
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return id != null ? id.equals(user.id) : user.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public enum UserType {
        USER,
        MODERATOR,
        ADMIN,
        SUPER_ADMIN
    }


}
