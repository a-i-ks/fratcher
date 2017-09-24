package de.iske.fratcher.match;

public class MatchCreated {
    public boolean confirmed;

    public MatchCreated(Match match) {
        this.confirmed = match instanceof LikeMatch && match.isConfirmed();
    }

    public MatchCreated() { //JPA only
    }
}
