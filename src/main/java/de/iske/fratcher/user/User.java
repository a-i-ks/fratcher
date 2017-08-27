package de.iske.fratcher.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.iske.fratcher.user.profile.Profile;
import de.iske.fratcher.util.Status;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// Renaming because "User" is keyword for PostgreSQL
@Entity(name =  "User_")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull
    private Status status;

    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull
    private UserType userType;

    @Column(unique = true)
    @Size(min = 3, max = 32)
    @NotNull
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 8)
    private String password;

    @Pattern(regexp = ".+@.+\\.[a-z]+")
    @Column(unique = true)
    private String email;

    @Embedded
    private Profile profile;

    public User() {
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

    public boolean isAdmin() {
        return (getUserType() == UserType.ADMIN);
    }

    public enum UserType {
        USER,
        MODERATOR,
        ADMIN,
        SUPER_ADMIN
    }


}
