package de.iske.fratcher.match;
import de.iske.fratcher.user.User;
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

    private EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom();

    @Test
    @Rollback
    public void testMatchCreation() {
        User user1 = random.nextObject(User.class,"id");
        User user2 = random.nextObject(User.class,"id");

        Match match1 = new Match(user1);
        assertNotNull("Match object should not null after creation",match1);
        assertNull("Match id should be null before persisting entity",match1.getId());


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
