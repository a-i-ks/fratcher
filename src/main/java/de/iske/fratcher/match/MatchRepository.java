package de.iske.fratcher.match;

import de.iske.fratcher.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends CrudRepository<Match, Long> {

    @Query("SELECT m from Match_ m WHERE m.user1 = :initialUser AND m.user2 = :likedUser")
    Match findMatchForUsers(@Param("initialUser") User initialUser, @Param("likedUser") User likedUser);

    @Query("SELECT m from LikeMatch m WHERE (m.user1 = :user OR m.user2 = :user) AND m.isConfirmed = TRUE")
    Iterable<LikeMatch> findConfirmedLikeMatchesForUser(@Param("user") User user);

    @Query("SELECT m from LikeMatch m WHERE (m.user1 = :user OR m.user2 = :user)")
    Iterable<LikeMatch> findLikeMatchesForUser(@Param("user") User user);

    @Query("SELECT m from Match_ m WHERE m.user1 = :initialUser")
    Iterable<Match> findMatchesForUserAsInitiator(@Param("initialUser") User initialUser);

    /**
     * Get all matches in which the passed user has already pressed like/dislike
     * Possibilities:
     * 1. Passed user is user1 => he is initiator => already rated
     * 2. Passed user is user2 and reactionTimestamp != null
     * => he has reacted to the match => already rated
     *
     * @param user the user to search for already rated matches
     * @return matches in which the passed user has already pressed like/dislike
     */
    @Query("SELECT m from Match_ m WHERE (m.user1 = :user) OR (m.user2 = :user AND m.reactionTimestamp IS NOT NULL)")
    Iterable<Match> findMatchesAlreadyRatedByUser(@Param("user") User user);
}
