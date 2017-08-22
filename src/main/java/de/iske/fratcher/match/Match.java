package de.iske.fratcher.match;

import de.iske.fratcher.user.User;
import de.iske.fratcher.util.Status;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "Match_")
public class Match {

    private final Instant matchingTimestamp;

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @ManyToOne(targetEntity = User.class)
    private User user1;

    @ManyToOne(targetEntity = User.class)
    private User user2;

    public Match() {
        matchingTimestamp = Instant.now();
        status = Status.DEFAULT;

    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
        user1.addMatch(this);
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
        user2.addMatch(this);
    }

    public void setUsers(User user1, User user2) {
        this.setUser1(user1);
        this.setUser2(user2);
    }

    public Instant getMatchingTimestamp() {
        return matchingTimestamp;
    }
}
