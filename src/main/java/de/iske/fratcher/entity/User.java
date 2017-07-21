package de.iske.fratcher.entity;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String username;
    private String firstName;
    private String lastName;

    private List<Match> matches;

    public User() {
        matches = new ArrayList<>();
    }
}
