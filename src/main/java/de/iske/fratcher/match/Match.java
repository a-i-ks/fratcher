package de.iske.fratcher.match;

import de.iske.fratcher.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;

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

    public Long getId() {
        return id;
    }

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
