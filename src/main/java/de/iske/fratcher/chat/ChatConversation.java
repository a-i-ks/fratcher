package de.iske.fratcher.chat;

import de.iske.fratcher.match.Match;
import de.iske.fratcher.user.User;
import de.iske.fratcher.util.Status;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(targetEntity = Match.class)
    private Match match;

    @OneToMany(targetEntity = ChatMessage.class, mappedBy = "conversation")
    private List<ChatMessage> messages;

    /**
     * User1 is always the user who started the conversation
     */
    @OneToOne
    private User user1;

    /**
     * User2 is always the user who received the first message from user1.
     */
    @OneToOne
    private User user2;

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


    private Status status;

    private Instant startedTimestamp;

    public ChatConversation() {
        this.startedTimestamp = Instant.now();
        this.status = Status.DEFAULT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Instant startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    @PrePersist
    public void prePersist() {
        startedTimestamp = Instant.now();
    }
}
