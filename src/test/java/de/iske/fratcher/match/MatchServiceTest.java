package de.iske.fratcher.match;

import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.Status;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.FieldDefinitionBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.EmailRandomizer;
import io.github.benas.randombeans.randomizers.text.StringRandomizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

// Use Spring's testing support in JUnit
@RunWith(SpringRunner.class)
// Enable Spring features, e.g. loading of application-properties, etc.
@SpringBootTest
public class MatchServiceTest {

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserService userService;


    private EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
            .randomize(FieldDefinitionBuilder.field().named("email").ofType(String.class).inClass(User.class).get(),
                    new EmailRandomizer())
            .randomize(FieldDefinitionBuilder.field().named("password").ofType(String.class).inClass(User.class).get(),
                    new StringRandomizer(8, 25, System.currentTimeMillis()))
            .build();

    @Test
    public void testServiceInjection() {
        assertNotNull("No instance for userService. Dependency injection failed", userService);
    }

    @Test
    @Transactional
    public void testLikeMatchIdShouldBeNullAfterCreation() {
        Match match1 = new LikeMatch();
        assertNotNull("Match object should not null after creation",match1);
        assertNull("Match id should be null before persisting entity",match1.getId());
    }

    @Test
    @Transactional
    public void testDislikeMatchIdShouldBeNullAfterCreation() {
        Match match1 = new DislikeMatch();
        assertNotNull("Match object should not null after creation", match1);
        assertNull("Match id should be null before persisting entity", match1.getId());
    }

    @Test
    @Transactional
    public void testLikeMatchIdShouldNotBeNullAfterPersisting() {
        Match match1 = new LikeMatch();
        matchService.addMatch(match1);
        assertNotNull("Match object should not null after creation", match1);
        assertNotNull("Match id should not null after persisting entity", match1.getId());
    }

    @Test
    @Transactional
    public void testDislikeMatchIdShouldNotBeNullAfterPersisting() {
        Match match1 = new DislikeMatch();
        matchService.addMatch(match1);
        assertNotNull("Match object should not null after creation",match1);
        assertNotNull("Match id should not null after persisting entity",match1.getId());
    }

    @Test
    @Transactional
    public void testMatchCreation() {
        User user1 = random.nextObject(User.class,"id");
        User user2 = random.nextObject(User.class,"id");
        userService.addUser(user1);
        userService.addUser(user2);


        Match match1 = new LikeMatch();
        match1.setUser1(user1);
        matchService.addMatch(match1);

        Match match2 = new DislikeMatch();
        match2.setUser1(user1);
        match2.setUser2(user2);
        matchService.addMatch(match2);


        assertNotNull("Match id should not null after persisting entity",match1.getId());
        assertNotNull("Match object should not null after creation",match2);
        assertNotNull("Match id should not null after persisting entity",match2.getId());
    }

    @Test
    @Transactional
    public void testUserInitialLike() {
        User user1 = random.nextObject(User.class, "id");
        User user2 = random.nextObject(User.class, "id");
        userService.addUser(user1);
        userService.addUser(user2);

        userService.setCurrentUser(user1.getId(), user1.getEmail());
        Match match = matchService.likeUser(user2);

        assertNotNull("Returned match object should not be null", match);
        assertNotNull("Match object id should not be null", match.getId());
        assertNotNull("Match matchingTimestamp should not be null", match.getMatchingTimestamp());
        assertEquals("Match status should be default", Status.DEFAULT, match.getStatus());
        assertEquals("Match should have User 1 in property user1", user1, match.getUser1());
        assertEquals("Match should have User 2 in property user2", user2, match.getUser2());
        assertTrue("Match should be a LikeMatch", match instanceof LikeMatch);
        assertEquals("Match should not be confirmed", false, match.isConfirmed());
        assertNull("Match should have a empty reactionTimestamp", match.getReactionTimestamp());
    }

    @Test
    @Transactional
    public void testUserConfirmLike() {
        User user1 = random.nextObject(User.class, "id");
        User user2 = random.nextObject(User.class, "id");
        userService.addUser(user1);
        userService.addUser(user2);

        userService.setCurrentUser(user1.getId(), user1.getEmail());
        matchService.likeUser(user2);

        userService.setCurrentUser(user2.getId(), user2.getEmail());
        Match match = matchService.likeUser(user1);

        assertNotNull("Returned match object should not be null", match);
        assertNotNull("Match object id should not be null", match.getId());
        assertNotNull("Match matchingTimestamp should not be null", match.getMatchingTimestamp());
        assertEquals("Match status should be default", Status.DEFAULT, match.getStatus());
        assertEquals("Match should have User 1 in property user1", user1, match.getUser1());
        assertEquals("Match should have User 2 in property user2", user2, match.getUser2());
        assertTrue("Match should be a LikeMatch", match instanceof LikeMatch);
        assertEquals("Match should be confirmed", true, match.isConfirmed());
        assertNotNull("Match should have a reactionTimestamp", match.getReactionTimestamp());
    }


}
