package de.iske.fratcher.chat;

import de.iske.fratcher.match.Match;
import de.iske.fratcher.util.Status;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(targetEntity = Match.class, fetch = FetchType.LAZY)
    private Match match;

    @OneToMany(targetEntity = ChatMessage.class, mappedBy = "conversation")
    private List<ChatMessage> messages;

    private Status status;

    private LocalDateTime startedTimestamp;

    public ChatConversation() {
        this.startedTimestamp = LocalDateTime.now();
        this.status = Status.DEFAULT;
        messages = new ArrayList<>();
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

    public LocalDateTime getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(LocalDateTime startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    @PrePersist
    public void prePersist() {
        startedTimestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "ChatConversation{" + "id=" +
                id +
                ", messageCount=" +
                messages.size() +
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

        ChatConversation conversation = (ChatConversation) o;

        return id != null ? id.equals(conversation.id) : conversation.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
