package de.iske.fratcher.chat;

import de.iske.fratcher.user.User;
import de.iske.fratcher.util.Status;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;

@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(targetEntity = ChatConversation.class)
    private ChatConversation conversation;

    @OneToOne
    private User sender;

    @OneToOne
    private User receiver;

    @Size(min = 1, max = 255)
    private String message;

    private Status status;

    private Instant transmissionTimestamp;

    private Instant seenTimestamp;

    public ChatMessage() {
        this.transmissionTimestamp = Instant.now();
        this.status = Status.DEFAULT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChatConversation getConversation() {
        return conversation;
    }

    public void setConversation(ChatConversation conversation) {
        this.conversation = conversation;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getTransmissionTimestamp() {
        return transmissionTimestamp;
    }

    public void setTransmissionTimestamp(Instant transmissionTimestamp) {
        this.transmissionTimestamp = transmissionTimestamp;
    }

    public Instant getSeenTimestamp() {
        return seenTimestamp;
    }

    public void setSeenTimestamp(Instant seenTimestamp) {
        this.seenTimestamp = seenTimestamp;
    }

    @PrePersist
    public void prePersist() {
        transmissionTimestamp = Instant.now();
    }


}
