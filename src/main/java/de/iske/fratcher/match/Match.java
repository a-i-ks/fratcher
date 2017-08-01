package de.iske.fratcher.match;

import de.iske.fratcher.user.User;

import javax.persistence.*;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Map;

@Entity
public class Match {

    private final Instant matchingTimestamp;
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(targetEntity = User.class)
    private User user1;
    @ManyToOne(targetEntity = User.class)
    private User user2;

    public Match() {
        matchingTimestamp = Instant.now();
    }

    public Match(User firstMatcher) {
        matchingTimestamp = Instant.now();
        user1 = firstMatcher;
        user1.addMatch(this);
    }

    public Match(User u1, User u2) {
        user1 = u1;
        user2 = u2;
        matchingTimestamp = Instant.now();
        u1.addMatch(this);
        u2.addMatch(this);
    }

    public Long getId() {
        return id;
    }

//    @ManyToOne(targetEntity = User.class)
//    private final Map.Entry<User,User> matchedUsers;

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Instant getMatchingTimestamp() {
        return matchingTimestamp;
    }
}
