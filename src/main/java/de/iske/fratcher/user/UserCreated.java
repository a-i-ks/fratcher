package de.iske.fratcher.user;

class UserCreated {
    public String url;

    public UserCreated(User user, String serverUrl) {
        this.url = serverUrl + "/api/user/" + user.getId();
    }
}
