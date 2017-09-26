package de.iske.fratcher.chat;

import java.time.LocalDateTime;

public class ChatMessageDto {

    private Long id;

    private Long senderId;

    private Long receiverId;

    private String message;

    private LocalDateTime transmissionTimestamp;

    private LocalDateTime seenTimestamp;

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

    public LocalDateTime getTransmissionTimestamp() {
        return transmissionTimestamp;
    }

    public void setTransmissionTimestamp(LocalDateTime transmissionTimestamp) {
        this.transmissionTimestamp = transmissionTimestamp;
    }

    public LocalDateTime getSeenTimestamp() {
        return seenTimestamp;
    }

    public void setSeenTimestamp(LocalDateTime seenTimestamp) {
        this.seenTimestamp = seenTimestamp;
    }
}
