package de.iske.fratcher.match;

class MatchCreated {
    public boolean confirmed;

    public MatchCreated(Match match) {
        this.confirmed = match.isConfirmed();
    }

    public MatchCreated() { //JPA only
    }
}
