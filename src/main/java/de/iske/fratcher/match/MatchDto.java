package de.iske.fratcher.match;

import de.iske.fratcher.user.UserDto;

import java.time.LocalDateTime;

public class MatchDto {

    private Long id;

    private LocalDateTime matchingTimestamp;

    private UserDto user1;

    private UserDto user2;

    private boolean hasChat;

    public boolean hasChat() {
        return hasChat;
    }

    public void setHasChat(boolean hasChat) {
        this.hasChat = hasChat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getMatchingTimestamp() {
        return matchingTimestamp;
    }

    public void setMatchingTimestamp(LocalDateTime matchingTimestamp) {
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
