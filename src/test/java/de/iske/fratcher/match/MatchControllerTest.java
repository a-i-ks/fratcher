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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;

import static org.junit.Assert.*;

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

    /**
     * In this test case the admin user presses "like" on user120
     *
     * @throws MalformedURLException if server url is malformed.
     */
    @Test
    public void testInitialLike() throws MalformedURLException {
        RestTemplate restTemplate = new RestTemplate();

        String userUrl = AddressUtils.getURL(addressService.getServerURL(), "api/user/", port);

        // Get user120 via REST
        String user120Url = userUrl + "120";
        HttpEntity<Object> authHeader = restAuthUtils.getEntityWithAdminAuthHeader(null);//send empty body with admin auth
        final ResponseEntity<User> userResponse = restTemplate.exchange(user120Url, HttpMethod.GET, authHeader, User.class);
        User user120 = userResponse.getBody();
        assertNotNull("User object should not be null", user120);

        // Send like for user120
        String createMatchUrl = AddressUtils.getURL(addressService.getServerURL(), "api/match", port);
        HttpEntity<User> adminLike120request = restAuthUtils.getEntityWithAdminAuthHeader(user120);
        final ResponseEntity<MatchCreated> matchResponse = restTemplate.exchange(createMatchUrl + "/like", HttpMethod.POST, adminLike120request, MatchCreated.class);
        MatchCreated matchCreated = matchResponse.getBody();

        assertEquals("Response Status should be CREATED", HttpStatus.CREATED, matchResponse.getStatusCode());
        assertNotNull("Location header should not be null", matchResponse.getHeaders().getLocation());
        assertTrue("Location header should contain the actual server address", matchResponse.getHeaders().getLocation().toString().startsWith(createMatchUrl));
        assertTrue("Location header should contain a valid resource location", matchResponse.getHeaders().getLocation().toString().matches(".*/\\d+/?"));
        assertNotNull("Match object should not be null", matchCreated);
        assertFalse("Match object should not be confirmed", matchCreated.confirmed);

        // Get created match
        final HttpEntity<Object> getMatchRequest = restAuthUtils.getEntityWithAdminAuthHeader(null);
        final ResponseEntity<Match> matchResponse2 = restTemplate.exchange(matchResponse.getHeaders().getLocation(), HttpMethod.GET, getMatchRequest, Match.class);

        assertEquals("Get should return HTTP 200", HttpStatus.OK, matchResponse2.getStatusCode());
        assertNotNull("Response body should not be empty", matchResponse2.getBody());
        assertEquals("User 1 should be admin user", new Long(1), matchResponse2.getBody().getUser1().getId());
        assertEquals("User 2 should be user 120", new Long(120), matchResponse2.getBody().getUser2().getId());
        assertFalse("Match should be unconfirmed", matchResponse2.getBody().isConfirmed());
    }

}
