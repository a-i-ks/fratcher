package de.iske.fratcher.chat;

import java.time.LocalDateTime;
import java.util.List;

public class ChatConversationDto {

    private Long id;

    private List<ChatMessageDto> messages;

    private LocalDateTime startedTimestamp;

    private Long matchId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ChatMessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageDto> messages) {
        this.messages = messages;
    }

    public LocalDateTime getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(LocalDateTime startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }


}
