package de.iske.fratcher.chat;

import de.iske.fratcher.match.MatchCreated;
import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.AddressService;
import de.iske.fratcher.util.AddressUtils;
import de.iske.fratcher.util.RestAuthUtils;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.FieldDefinitionBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.EmailRandomizer;
import io.github.benas.randombeans.randomizers.text.StringRandomizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ChatControllerTest.class);

    private final EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
            .randomize(FieldDefinitionBuilder.field().named("email").ofType(String.class).inClass(User.class).get(),
                    new EmailRandomizer())
            .randomize(FieldDefinitionBuilder.field().named("password").ofType(String.class).inClass(User.class).get(),
                    new StringRandomizer(8, 25, System.currentTimeMillis()))
            .build();

    @LocalServerPort
    int port;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RestAuthUtils restAuthUtils;

    private URI matchLocationFromSetup;

    private long userId1;

    private long userId2;

    private String user1mail;

    private String user2mail;

    @Before
    public void setup() throws MalformedURLException {
        // Create a match between to random test users
        RestTemplate restTemplate = new RestTemplate();

        // choose users
        userId1 = random.longs(1, 3, 203).findFirst().getAsLong();
        userId2 = random.longs(1, 3, 203).findFirst().getAsLong();
        user1mail = userService.getUser(userId1).getEmail();
        user2mail = userService.getUser(userId2).getEmail();

        //user1 like user2
        User user1requestObj = new User();
        user1requestObj.setId(userId2);
        String createMatchUrl = AddressUtils.getURL(addressService.getServerURL(), "api/match", port);
        HttpEntity<User> user1LikeUser2request = restAuthUtils.getEntityWithUserAuthHeader(user1requestObj, user1mail, "password" + userId1);
        final ResponseEntity<MatchCreated> matchResponse1 = restTemplate.exchange(createMatchUrl + "/like", HttpMethod.POST, user1LikeUser2request, MatchCreated.class);
        matchLocationFromSetup = matchResponse1.getHeaders().getLocation();

        assertNotNull(matchLocationFromSetup);

        //user2 re-like user1
        User user2requestObj = new User();
        user2requestObj.setId(userId1);
        HttpEntity<User> user2LikeUser1request = restAuthUtils.getEntityWithUserAuthHeader(user2requestObj, user2mail, "password" + userId2);
        final ResponseEntity<MatchCreated> matchResponse = restTemplate.exchange(createMatchUrl + "/like", HttpMethod.POST, user2LikeUser1request, MatchCreated.class);
        MatchCreated matchCreated = matchResponse.getBody();
        matchLocationFromSetup = matchResponse.getHeaders().getLocation();

        assertEquals("Match should be confirmed", true, matchCreated.confirmed);
    }

    @Transactional
    @Test
    public void testUser1StartChat() throws MalformedURLException {
        RestTemplate restTemplate = new RestTemplate();
        //user 1 start chat with user2
        URI createChatUrl = AddressUtils.getURLasURI(addressService.getServerURL(), matchLocationFromSetup.getPath() + "/chat", port);
        HttpEntity<User> user1startChat = restAuthUtils.getEntityWithUserAuthHeader(null, user1mail, "password" + userId1);

        URI uriChat = restTemplate.postForLocation(createChatUrl, user1startChat);

        //user 1 send message to user 2
        ChatMessage message = new ChatMessage();
        message.setMessage("Hallo Welt");
        HttpEntity<ChatMessage> user1sendMsg = restAuthUtils.getEntityWithUserAuthHeader(message, user1mail, "password" + userId1);

        URI uriChatMsg = URI.create(uriChat.toString() + "message");

        uriChat = restTemplate.postForLocation(uriChatMsg, user1sendMsg);

        HttpEntity<Object> authHeader = restAuthUtils.getEntityWithUserAuthHeader(null, user1mail, "password" + userId1);

        //get chat and check message
        ResponseEntity<ChatConversation> chatConversation = restTemplate.exchange(uriChat, HttpMethod.GET, authHeader, ChatConversation.class);

        assertNotNull(chatConversation);
        assertNotNull(chatConversation.getBody());
        assertNotNull(chatConversation.getBody().getMessages());
        assertTrue(chatConversation.getBody().getMessages().size() > 0);
        assertTrue(chatConversation.getBody().getMessages().get(0).getMessage().equals("Hallo Welt"));


    }


}
