package de.iske.fratcher.user;

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




}
