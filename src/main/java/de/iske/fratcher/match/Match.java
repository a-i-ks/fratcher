package de.iske.fratcher.match;

import de.iske.fratcher.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Map;

@Entity
public class Match {

    @Id
    @GeneratedValue
    private Long id;

    private final Map.Entry<User,User> matchedUsers;

    private final Instant matchingTimestamp;

    public Match(User u1, User u2) {
        matchedUsers = new AbstractMap.SimpleEntry<User, User>(u1,u2);
        matchingTimestamp = Instant.now();
    }

    public Map.Entry<User,User> getMatchedUsers() {
        return matchedUsers;
    }

    public Instant getMatchingTimestamp() {
        return matchingTimestamp;
    }
}
