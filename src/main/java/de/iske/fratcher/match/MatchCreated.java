package de.iske.fratcher.match;

class MatchCreated {
    public boolean confirmed;

    public MatchCreated(Match match) {
        if (match instanceof LikeMatch) {
            this.confirmed = ((LikeMatch) match).isConfirmed();
        } else {
            this.confirmed = false;
        }
    }

    public MatchCreated() { //JPA only
    }
}
