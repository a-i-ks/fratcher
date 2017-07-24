package de.iske.fratcher.match;

import de.iske.fratcher.user.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Match {

    private final List<User> matchedUsers;

    private final Instant matchingTimestamp;

    public Match(User u1, User u2) {
        matchedUsers = new ArrayList<>();
        matchedUsers.add(u1);
        matchedUsers.add(u2);
        matchingTimestamp = Instant.now();
    }

    public List<User> getMatchedUsers() {
        return matchedUsers;
    }

    public Instant getMatchingTimestamp() {
        return matchingTimestamp;
    }
}
