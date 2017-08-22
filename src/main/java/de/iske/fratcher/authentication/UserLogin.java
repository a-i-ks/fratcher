package de.iske.fratcher.authentication;

public class UserLogin {
    public String email;
    public String password;

    public UserLogin() {
    }

    public UserLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
