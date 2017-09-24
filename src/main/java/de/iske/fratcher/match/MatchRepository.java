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
    Iterable<Match> findMatchesForUser(@Param("initialUser") User initialUser);
}
