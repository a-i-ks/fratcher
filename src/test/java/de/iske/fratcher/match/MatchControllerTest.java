package de.iske.fratcher.match;

import de.iske.fratcher.user.User;
import de.iske.fratcher.util.AddressService;
import de.iske.fratcher.util.AddressUtils;
import de.iske.fratcher.util.RestAuthUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

        thrown.expect(HttpClientErrorException.class);
        thrown.expect(hasProperty("statusCode", is(HttpStatus.UNAUTHORIZED)));
        thrown.expect(hasProperty("responseBodyAsString", is("MATCH_NOT_CONFIRMED")));

        // Get created match
        final HttpEntity<Object> getMatchRequest = restAuthUtils.getEntityWithAdminAuthHeader(null);
        final ResponseEntity<MatchDto> matchResponse2 = restTemplate.exchange(matchResponse.getHeaders().getLocation(), HttpMethod.GET, getMatchRequest, MatchDto.class);
    }

}
