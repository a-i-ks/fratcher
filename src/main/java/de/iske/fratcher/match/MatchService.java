package de.iske.fratcher.match;


import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

    @PersistenceContext
    private EntityManager entityManager;

    public void addMatch(Match match) {
        matchRepository.save(match);
        LOG.info(match.getUser1().getUsername());
    }

    /**
     * Method that is called if one user likes another.
     *
     * @param likedUser - the user who has been liked by initialUser
     * @return - the match object that has either been created or was already existing
     */
    public Match likeUser(User likedUser) {
        User initialUser = userService.getCurrentUser();
        //check if likedUser has already like initialUser
        for (Match match : likedUser.getMatches()) {
            if (match.getUser2().equals(initialUser)) {
                match.confirm();
                return match; // it's a match
            }
        }
        Match unconfirmedMatch = new Match();
        unconfirmedMatch.setUsers(initialUser, likedUser);
        addMatch(unconfirmedMatch);
        return unconfirmedMatch;
    }
}
