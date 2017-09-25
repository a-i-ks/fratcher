package de.iske.fratcher.chat;

import java.time.Instant;
import java.util.List;

public class ChatConversationDto {

    private Long id;

    private List<ChatMessageDto> messages;

    private Instant startedTimestamp;

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

    public Instant getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Instant startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }


}
