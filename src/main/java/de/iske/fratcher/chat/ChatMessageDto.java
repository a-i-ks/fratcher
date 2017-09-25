package de.iske.fratcher.chat;

import java.time.Instant;

public class ChatMessageDto {

    private Long id;

    private Long senderId;

    private Long receiverId;

    private String message;

    private Instant transmissionTimestamp;

    private Instant seenTimestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
