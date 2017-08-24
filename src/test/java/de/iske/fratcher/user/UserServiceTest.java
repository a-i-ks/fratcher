package de.iske.fratcher.user;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.assertj.core.util.IterableUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

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
    public void testServiceInjection() {
        assertNotNull("No instance for userService. Dependency injection failed", userService);
    }

    @Test
    @Transactional
    public void testUserCreation() {
        LOG.info("Number of user in database: {}", IterableUtil.sizeOf(userService.getUserList()));
        User user = random.nextObject(User.class,"id");
        assertNotNull("user should not be null after creation",user);
        //Check that id is null in the beginning and is set only after persisting the entity
        assertNull("User id should be null before persisting entity",user.getId());
    }

    @Test
    @Transactional
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
    @Transactional
    public void testUserList() {
        Stream<User> userStream = random.objects(User.class, 10, "id");
        userStream.forEach(user -> userService.addUser(user));
        // 202 Test User + 10 from this Test = 212
        assertEquals("There should be 212 users in the user list", 212, IterableUtil.sizeOf(userService.getUserList()));
    }
}
