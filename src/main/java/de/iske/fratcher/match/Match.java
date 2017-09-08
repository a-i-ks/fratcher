package de.iske.fratcher.match;

import de.iske.fratcher.user.User;
import de.iske.fratcher.util.Status;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "Match_")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Match {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    /**
     * User 1 is always the user who likes/dislikes the other person first
     */
    @ManyToOne(targetEntity = User.class)
    private User user1;

    /**
     * User 2 is the user who has to confirm the match
     */
    @ManyToOne(targetEntity = User.class)
    private User user2;

    private Instant matchingTimestamp;

    /**
     * Timestamp when user2 presses like/dislike on user1
     * This timestamp saved the time when user2 reacts on the like/dislike
     * of user1.
     * This timestamp is null, if user2 has not react on the like/dislike of user1
     */
    private Instant reactionTimestamp;

    public Match() {
        matchingTimestamp = Instant.now();
        status = Status.DEFAULT;
    }

    public Instant getReactionTimestamp() {
        return reactionTimestamp;
    }

    public void setReactionTimestamp(Instant reactionTimestamp) {
        this.reactionTimestamp = reactionTimestamp;
    }

    @PrePersist
    public void prePersist() {
        matchingTimestamp = Instant.now();
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
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public void setUsers(User user1, User user2) {
        this.setUser1(user1);
        this.setUser2(user2);
    }

    public Instant getMatchingTimestamp() {
        return matchingTimestamp;
    }

    public void setMatchingTimestamp(Instant matchingTimestamp) {
        this.matchingTimestamp = matchingTimestamp;
    }

    public boolean hasReaction() {
        return (getReactionTimestamp() != null);
    }

    public abstract boolean isConfirmed();
}
