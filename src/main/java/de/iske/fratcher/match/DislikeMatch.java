package de.iske.fratcher.match;

import javax.persistence.Entity;
import java.time.Instant;

/**
 * Entity that describes that user1 pressed dislike on user2.
 */
@Entity
public class DislikeMatch extends Match {

    /**
     * If this flag is set to true it signals that user2 re-disliked user1. So they both don't like each other
     * Maybe I won't implement or display this information in the frontend, but is good to have this information
     * for subsequent purposes.
     */
    private boolean confirmedDislike;

    public DislikeMatch() {
        super();
    }

    public boolean isConfirmed() {
        return confirmedDislike;
    }

    public void setConfirmedDislike(boolean confirmedDislike) {
        this.confirmedDislike = confirmedDislike;
    }

    public void confirmDislike() {
        setConfirmedDislike(true);
        setReactionTimestamp(Instant.now());
    }


}
