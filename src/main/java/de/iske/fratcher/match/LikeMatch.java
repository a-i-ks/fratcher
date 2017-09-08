package de.iske.fratcher.match;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import java.time.Instant;

/**
 * Entity that describes that user1 pressed like on user2.
 * If isConfirmed boolean is set to true, user2 had re-liked user1
 */
@Entity
public class LikeMatch extends Match {


    /**
     * A match is confirmed, if user2 also presses like on user1
     */
    private boolean isConfirmed;


    public LikeMatch() {
        super();
        isConfirmed = false;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    @PrePersist
    public void prePersist() {
        setMatchingTimestamp(Instant.now());
    }

    public void confirm() {
        setConfirmed(true);
        setReactionTimestamp(Instant.now());
    }


}
