package de.iske.fratcher.user;

import de.iske.fratcher.match.Match;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class UserServiceTest {

    private EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom();

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceTest.class);

    @Autowired
    private UserService userService;

    @Test
    public void testNotNull() {
        assertNotNull("No instance for userService. Dependency injection failed", userService);
    }

    @Test
    @Rollback
    public void testUserCreation() {
        LOG.info("Number of user in database: {}",userService.getUserList().count());
        User user = random.nextObject(User.class,"id");
        //Check that id is null in the beginning and is set only after persisting the entity
        assertNull("User id should be null before persisting entity",user.getId());

        userService.addUser(user);
        assertNotNull("User id should not be null after persisting entity",user.getId());
    }

    @Test
    @Rollback
    public void testUserPersist() {
        User user = random.nextObject(User.class,"id");
        assertNull("User id should be null before persisting entity",user.getId());
        userService.addUser(user);
        Long userId = user.getId();
        assertNotNull("User id should not be null after persisting entity",userId);
        User storedUser = userService.getUser(userId);
        assertEquals("User was not read correctly from database",user,storedUser);
    }

    @Test
    @Rollback
    public void testMatchCreation() {
        User user1 = random.nextObject(User.class,"id");
        User user2 = random.nextObject(User.class,"id");
        userService.addUser(user1);
        userService.addUser(user2);

        Match match1 = new Match(user1);
        assertNotNull("Match object should not null after creation",match1);
        assertNull("Match id should be null before persisting entity",match1.getId());
        //TODO: Check for match id != null after persisting entity
        assertEquals("User 1 should now have one Match in his list",1,user1.getMatches().size());
        assertEquals("User 1 should now have match2 in his list",match1, user1.getMatches().get(0));

        Match match2 = new Match(user1,user2);
        assertNotNull("Match object should not null after creation",match2);
        assertNull("Match id should be null before persisting entity",match2.getId());
        //TODO: Check for match id != null after persisting entity

        assertEquals("User 1 should now have two Matches in his list",2,user1.getMatches().size());
        assertEquals("User 2 should now have one Match in his list",1,user2.getMatches().size());
        assertEquals("User 1 should now have match1 in his list",match1, user1.getMatches().get(0));
        assertEquals("User 1 should now have match2 in his list",match2, user1.getMatches().get(1));
        assertEquals("User 2 should now have match1 in his list",match2, user2.getMatches().get(0));

    }




}
