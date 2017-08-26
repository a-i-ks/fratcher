package de.iske.fratcher.authentication;

public class UserLogin {
    public String identification;
    public String password;

    public UserLogin() {
    }

    public UserLogin(String identification, String password) {
        this.identification = identification;
        this.password = password;
    }
}
