package de.iske.fratcher.match;


import de.iske.fratcher.chat.ChatConversation;
import de.iske.fratcher.chat.ChatService;
import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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
    private ChatService chatService;

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
            // save changes of Match to database
            matchRepository.save(inverseMatch);
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

    public Iterable<LikeMatch> getLikeMatchesForUser(User user) {
        return matchRepository.findLikeMatchesForUser(user);
    }

    public List<User> getMatchingCandidatesForUser(User user, int numberOfCandidates, boolean randomOrder) {
        // create empty result list
        List<User> matchingCandidates = new ArrayList<>();

        // empty list for users which have already been liked/disliked
        List<User> alreadyRatedUsers = new ArrayList<>();

        // search all matches where current user has already
        final Iterable<Match> alreadyRatedMatches = matchRepository.findMatchesAlreadyRatedByUser(user);

        // for each match add other user to list of already rated users
        for (Match match : alreadyRatedMatches) {
            if (match.getUser1().equals(user)) { //user is initiator
                alreadyRatedUsers.add(match.getUser2());
            } else { //user was matched by user1 first, but has already react to the match
                alreadyRatedUsers.add(match.getUser1());
            }
        }

        // get full user directory
        final Iterable<User> userList = userService.getUserList();

        // add all users as possible matching candidates
        userList.forEach(matchingCandidates::add);

        // shuffle userList (we don't want to see the same users every time)
        if (randomOrder) {
            Collections.shuffle(matchingCandidates);
        }

        // remove current user from matching candidates (obviously you can't match yourself)
        matchingCandidates.remove(user);

        // remove all already rated candidates
        // bring potential matching candidates (which a already liked current user)
        // to a higher position
        matchingCandidates = matchingCandidates.stream()
                .filter(u -> !alreadyRatedUsers.contains(u))
                .filter(u -> u.getStatus() != Status.INACTIVE)
                .sorted(this::userComparator)
                .limit(numberOfCandidates)
                .collect(Collectors.toList());

        // shuffle to make it a little more exciting
        // otherwise the first press on like would likely
        // be a confirmed match
        if (randomOrder) {
            Collections.shuffle(matchingCandidates);
        }

        LOG.info("[FOUND] {} matching candidates for {}", matchingCandidates.size(), user);

        return matchingCandidates;

    }

    public void deleteMatch(Match match) {
        Match matchToDelete = matchRepository.findOne(match.getId());
        matchToDelete.setStatus(Status.DELETED);
        //update child entities
        ChatConversation conversation = matchToDelete.getConversation();
        if (conversation != null) {
            chatService.deleteConversation(conversation);
            conversation.setStatus(Status.DELETED);
        }
        matchRepository.save(matchToDelete);
        LOG.info("[DELETED] {}", matchToDelete);
    }

    public Iterable<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    private int userComparator(User u1, User u2) {
        User currentUser = userService.getCurrentUser();
        boolean u1HasCommonMatch;
        boolean u2HasCommonMatch;

        //check if user1 has confirmedLikeMatch with current user;
        u1HasCommonMatch = haveUsersLikeMatchTogether(u1, currentUser);

        //check if user2 has confirmedLikeMatch with current user;
        u2HasCommonMatch = haveUsersLikeMatchTogether(u2, currentUser);

        if (u1HasCommonMatch && !u2HasCommonMatch) {
            return -1;
        } else if (!u1HasCommonMatch && u2HasCommonMatch) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean haveUsersLikeMatchTogether(User u1, User u2) {
        boolean commonMatch = false;
        for (LikeMatch likeMatch : getLikeMatchesForUser(u1)) {
            if ((likeMatch.getUser1().equals(u2) && likeMatch.getUser2().equals(u1))) {
                commonMatch = true;
                break;
            } else if ((likeMatch.getUser1().equals(u1) && likeMatch.getUser2().equals(u2))) {
                commonMatch = true;
                break;
            }
        }
        return commonMatch;
    }

}
