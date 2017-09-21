package de.iske.fratcher.match;


import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<User> getMatchingCandidatesForUser(User user, int numberOfCandidates) {
        // create empty result list
        List<User> matchingCandidates = new ArrayList<>();

        // empty list for users which have already been liked/disliked
        List<User> alreadyMatchedUsers = new ArrayList<>();

        // search all matches that had been initiated by user
        final Iterable<Match> matchesForUser = matchRepository.findMatchesForUser(user);
        // for each match add user1 (initial user) to list of already matched users
        matchesForUser.forEach(match -> alreadyMatchedUsers.add(match.getUser1()));

        // get full user directory
        final Iterable<User> userList = userService.getUserList();

        // add all users as possible matching candidates
        userList.forEach(matchingCandidates::add);

        // remove current user from matching candidates (obviously you can't match yourself)
        matchingCandidates.remove(user);

        // remove all already rated candidates
        matchingCandidates = matchingCandidates.stream()
                .filter(u -> !alreadyMatchedUsers.contains(u))
                .filter(u -> u.getStatus() != Status.INACTIVE)
                .limit(numberOfCandidates)
                .collect(Collectors.toList());

        return matchingCandidates;

    }
}
