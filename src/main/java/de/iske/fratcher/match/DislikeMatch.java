package de.iske.fratcher.match;

import javax.persistence.Entity;
import java.time.LocalDateTime;

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
        setReactionTimestamp(LocalDateTime.now());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DislikeMatch{");
        sb.append("id=");
        sb.append(super.getId());
        if (this.getUser1() != null
                && this.getUser2() != null) {
            sb.append(", ");
            sb.append(this.getUser1().toString());
            sb.append(" <-> ");
            sb.append(this.getUser2().toString());
        }
        sb.append('}');
        return sb.toString();
    }
}
