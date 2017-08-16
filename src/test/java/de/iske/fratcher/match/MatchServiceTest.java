package de.iske.fratcher.match;
import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

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

    private EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom();

    @Test
    public void testServiceInjection() {
        assertNotNull("No instance for userService. Dependency injection failed", userService);
    }

    @Test
    @Rollback
    public void testIdShouldBeNullAfterCreation() {
        Match match1 = new Match();
        assertNotNull("Match object should not null after creation",match1);
        assertNull("Match id should be null before persisting entity",match1.getId());
    }

    @Test
    @Rollback
    public void testIdShouldNotBeNullAfterPersisting() {
        Match match1 = new Match();
        matchService.addMatch(match1);
        assertNotNull("Match object should not null after creation",match1);
        assertNotNull("Match id should not null after persisting entity",match1.getId());
    }

    @Test
    @Rollback
    public void testMatchCreation() {
        User user1 = random.nextObject(User.class,"id");
        User user2 = random.nextObject(User.class,"id");
        userService.addUser(user1);
        userService.addUser(user2);


        Match match1 = new Match();
        match1.setUser1(user1);
        matchService.addMatch(match1);

        Match match2 = new Match();
        match2.setUser1(user1);
        match2.setUser2(user2);
        matchService.addMatch(match2);


        assertNotNull("Match id should not null after persisting entity",match1.getId());
        assertEquals("User 1 should now have one Match in his list",1,user1.getMatches().size());
        assertEquals("User 1 should now have match2 in his list",match1, user1.getMatches().get(0));
        assertNotNull("Match object should not null after creation",match2);
        assertNotNull("Match id should not null after persisting entity",match2.getId());
        assertEquals("User 1 should now have two Matches in his list",2,user1.getMatches().size());
        assertEquals("User 2 should now have one Match in his list",1,user2.getMatches().size());
        assertEquals("User 1 should now have match1 in his list",match1, user1.getMatches().get(0));
        assertEquals("User 1 should now have match2 in his list",match2, user1.getMatches().get(1));
        assertEquals("User 2 should now have match1 in his list",match2, user2.getMatches().get(0));

    }
}
