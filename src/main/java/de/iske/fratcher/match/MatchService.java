package de.iske.fratcher.match;


import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service for all match-related functional operations.
 * @author André Iske
 * @since 2017-08-01
 */
@Service
public class MatchService {

    private static final Logger LOG = LoggerFactory.getLogger(MatchService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MatchRepository matchRepository;


    public void addMatch(Match match) {
        matchRepository.save(match);
    }

    /**
     * Method that is called if one user likes another.
     *
     * @param likedUser - the user who has been liked by initialUser
     * @return - the match object that has either been created or was already existing
     */
    public Match likeUser(User likedUser) {
        User initialUser = userService.getCurrentUser();
        LOG.info("{} liked {}", initialUser, likedUser);
        // Check if likedUser has already like initialUser
        Match inverseMatch = matchRepository.findMatchForUsers(likedUser, initialUser);
        if (inverseMatch != null) {
            // Check if inverse match was also a like
            if (inverseMatch instanceof LikeMatch) {
                // => confirm like
                ((LikeMatch) inverseMatch).confirm();
            } else { // => just set reaction timestamp for match
                inverseMatch.setReactionTimestamp(Instant.now());
            }
            matchRepository.save(inverseMatch);
            return inverseMatch;
        }
        // Check if there is already an existing match for initialUser -> likedUser
        Match existingMatch = matchRepository.findMatchForUsers(initialUser, likedUser);
        if (existingMatch != null) {
            return existingMatch;
        } else {
            Match unconfirmedMatch = new LikeMatch();
            unconfirmedMatch.setUsers(initialUser, likedUser);
            addMatch(unconfirmedMatch);
            return unconfirmedMatch;
        }
    }

    /**
     * Method that is called if one user dislikes another.
     *
     * @param dislikeUser - the user who has been disliked by initialUser
     * @return - the match object that has either been created or was already existing
     */
    public Match dislikeUser(User dislikeUser) {
        User initialUser = userService.getCurrentUser();
        LOG.info("{} disliked {}", initialUser, dislikeUser);
        // Check if likedUser has already disliked initialUser
        Match inverseMatch = matchRepository.findMatchForUsers(dislikeUser, initialUser);
        if (inverseMatch != null) {
            // Check if inverse match was also a dislike
            if (inverseMatch instanceof DislikeMatch) {
                // => confirm dislike
                ((DislikeMatch) inverseMatch).confirmDislike();
            } else { // => just set reaction timestamp for match
                inverseMatch.setReactionTimestamp(Instant.now());
            }
            return inverseMatch;
        }
        // Check if there is already an existing dislike match for initialUser -> dislikeUser
        Match existingMatch = matchRepository.findMatchForUsers(initialUser, dislikeUser);
        if (existingMatch != null) {
            return existingMatch;
        } else {
            Match unconfirmedMatch = new DislikeMatch();
            unconfirmedMatch.setUsers(initialUser, dislikeUser);
            addMatch(unconfirmedMatch);
            return unconfirmedMatch;
        }
    }

    public Match getMatchById(Long id) {
        return matchRepository.findOne(id);
    }

    public Iterable<LikeMatch> getConfirmedLikeMatchesForUser(User user) {
        return matchRepository.findConfirmedLikeMatchesForUser(user);
    }
}
