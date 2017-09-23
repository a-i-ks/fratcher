package de.iske.fratcher.match;

import de.iske.fratcher.chat.ChatConversation;
import de.iske.fratcher.user.UserDto;

import java.time.Instant;

public class MatchDto {

    private Long id;

    private Instant matchingTimestamp;

    private UserDto user1;

    private UserDto user2;

    private ChatConversation conversation;

    public ChatConversation getConversation() {
        return conversation;
    }

    public void setConversation(ChatConversation conversation) {
        this.conversation = conversation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMatchingTimestamp() {
        return matchingTimestamp;
    }

    public void setMatchingTimestamp(Instant matchingTimestamp) {
        this.matchingTimestamp = matchingTimestamp;
    }

    public UserDto getUser1() {
        return user1;
    }

    public void setUser1(UserDto user1) {
        this.user1 = user1;
    }

    public UserDto getUser2() {
        return user2;
    }

    public void setUser2(UserDto user2) {
        this.user2 = user2;
    }
}
