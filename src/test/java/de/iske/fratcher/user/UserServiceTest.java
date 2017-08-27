package de.iske.fratcher.user;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.FieldDefinitionBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.EmailRandomizer;
import io.github.benas.randombeans.randomizers.text.StringRandomizer;
import org.assertj.core.util.IterableUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.junit.Assert.*;

// Use Spring's testing support in JUnit
@RunWith(SpringRunner.class)
// Enable Spring features, e.g. loading of application-properties, etc.
@SpringBootTest
public class UserServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceTest.class);

    private EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
            .randomize(FieldDefinitionBuilder.field().named("email").ofType(String.class).inClass(User.class).get(),
                    new EmailRandomizer())
            .randomize(FieldDefinitionBuilder.field().named("password").ofType(String.class).inClass(User.class).get(),
                    new StringRandomizer(8, 25, System.currentTimeMillis()))
            .build();

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

    @Test
    @Transactional
    public void testErrorOnNonUniqueUsername() {
        User user1 = random.nextObject(User.class, "id");
        User user2 = random.nextObject(User.class, "id");

        user1.setUsername("test");
        userService.addUser(user1);
        user2.setUsername("test");

        thrown.expect(DataIntegrityViolationException.class);
        thrown.expectMessage("ConstraintViolationException");
        userService.addUser(user2);
    }

    @Test
    @Transactional
    public void testErrorOnNonUniqueEmail() {
        User user1 = random.nextObject(User.class, "id");
        User user2 = random.nextObject(User.class, "id");

        user1.setEmail("test@test.de");
        userService.addUser(user1);
        user2.setEmail("test@test.de");

        thrown.expect(DataIntegrityViolationException.class);
        thrown.expectMessage("ConstraintViolationException");
        userService.addUser(user2);
    }
}
