package de.iske.fratcher.match;

class MatchCreated {
    public String url;

    public boolean confirmed;

    public MatchCreated(Match match, String url) {
        this.url = url + "/api/match/" + match.getId();
        this.confirmed = match.isConfirmed();
    }

    public MatchCreated() {
    }
}
