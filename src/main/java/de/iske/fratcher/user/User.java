package de.iske.fratcher.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.iske.fratcher.match.Match;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

// Renaming because "User" is keyword for PostgreSQL
@Entity(name =  "User_")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    @JsonIgnore
    private String password;

    private String email;
    private String firstName;
    private String lastName;

    private List<Match> matches;

    public Long getId() {
        return id;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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



    public User() {
        matches = new ArrayList<>();
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
}
