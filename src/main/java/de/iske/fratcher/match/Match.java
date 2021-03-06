package de.iske.fratcher.match;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.iske.fratcher.chat.ChatConversation;
import de.iske.fratcher.user.User;
import de.iske.fratcher.util.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "Match_")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    private LocalDateTime matchingTimestamp;

    /**
     * Chat between matched users
     */
    @OneToOne(targetEntity = ChatConversation.class, mappedBy = "match")
    private ChatConversation conversation;

    public ChatConversation getConversation() {
        return conversation;
    }

    public void setConversation(ChatConversation conversation) {
        this.conversation = conversation;
    }

    /**
     * Timestamp when user2 presses like/dislike on user1
     * This timestamp saved the time when user2 reacts on the like/dislike
     * of user1.
     * This timestamp is null, if user2 has not react on the like/dislike of user1
     */
    private LocalDateTime reactionTimestamp;

    public Match() {
        matchingTimestamp = LocalDateTime.now();
        status = Status.DEFAULT;
    }

    public LocalDateTime getReactionTimestamp() {
        return reactionTimestamp;
    }

    public void setReactionTimestamp(LocalDateTime reactionTimestamp) {
        this.reactionTimestamp = reactionTimestamp;
    }

    @PrePersist
    public void prePersist() {
        matchingTimestamp = LocalDateTime.now();
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

    public LocalDateTime getMatchingTimestamp() {
        return matchingTimestamp;
    }

    public void setMatchingTimestamp(LocalDateTime matchingTimestamp) {
        this.matchingTimestamp = matchingTimestamp;
    }

    public boolean hasReaction() {
        return (getReactionTimestamp() != null);
    }

    public abstract boolean isConfirmed();

    @Override
    public String toString() {
        return "Match{" + "id=" +
                id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Match match = (Match) o;

        return id != null ? id.equals(match.id) : match.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
