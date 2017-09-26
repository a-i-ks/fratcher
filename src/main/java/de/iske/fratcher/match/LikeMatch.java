package de.iske.fratcher.match;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import java.time.LocalDateTime;

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
        setMatchingTimestamp(LocalDateTime.now());
    }

    public void confirm() {
        setConfirmed(true);
        setReactionTimestamp(LocalDateTime.now());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.isConfirmed) {
            sb.append("ConfirmedLikeMatch{");
        } else {
            sb.append("LikeMatch{");
        }
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
