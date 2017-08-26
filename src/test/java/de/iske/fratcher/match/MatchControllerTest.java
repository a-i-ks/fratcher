package de.iske.fratcher.match;

import de.iske.fratcher.user.User;
import de.iske.fratcher.util.AddressService;
import de.iske.fratcher.util.AddressUtils;
import de.iske.fratcher.util.RestAuthUtils;
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
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MatchControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(MatchControllerTest.class);

    @LocalServerPort
    int port;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RestAuthUtils restAuthUtils;

    @Test
    public void testMatchCreation() throws MalformedURLException {
        RestTemplate restTemplate = new RestTemplate();

        // Get user120 via REST
        String user120Url = AddressUtils.getURL(addressService.getServerURL(), "api/user/120", port);
        HttpEntity<Object> authHeader = restAuthUtils.getEntityWithAdminAuthHeader(null);//send empty body with admin auth
        ResponseEntity<User> userResponse = restTemplate.exchange(user120Url, HttpMethod.GET, authHeader, User.class);
        User user120 = userResponse.getBody();
        assertNotNull("User object should not be null", user120);

        // send like for user120
        String createMatchUrl = AddressUtils.getURL(addressService.getServerURL(), "api/match", port);
        HttpEntity<User> request = restAuthUtils.getEntityWithAdminAuthHeader(user120);
        ResponseEntity<MatchCreated> matchResponse = restTemplate.exchange(createMatchUrl, HttpMethod.POST, request, MatchCreated.class);
        MatchCreated matchCreated = matchResponse.getBody();
        assertNotNull("Match object should not be null", matchCreated);

        // Get user120 via REST
        ResponseEntity<User> userAfterMatchResponse = restTemplate.exchange(user120Url, HttpMethod.GET, authHeader, User.class);
        User user120afterMatch = userResponse.getBody();
        assertNotNull("User object should not be null", user120);


    }
}
