package de.iske.fratcher.user;

import de.iske.fratcher.util.AddressService;
import de.iske.fratcher.util.AddressUtils;
import de.iske.fratcher.util.RestAuthUtils;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
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
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(UserControllerTest.class);

    private EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom();

    @LocalServerPort
    int port;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RestAuthUtils restAuthUtils;

    /**
     * Test that listing of all user works (if you logged in as admin)
     */
    @Test
    public void testUserListAsAdmin() throws MalformedURLException {
        String url = AddressUtils.getURL(addressService.getServerURL(), "api/user", port);
        LOG.info("Sending GET Request to " + url);
        RestTemplate rest = new RestTemplate();
        HttpEntity<Object> authHeaderRequest = restAuthUtils.getEntityWithAdminAuthHeader(null);
        ResponseEntity<List> response = rest.exchange(url, HttpMethod.GET, authHeaderRequest, List.class);
        List<?> users = response.getBody();
        assertEquals("HTTP status code should be 200", 200, response.getStatusCodeValue());
        assertTrue("The userList should contain at least 2 users.", users.size() > 1);
    }


    /**
     * Test if a new user can be generated via REST Endpoint
     *
     * @throws Exception if something goes wrong
     */
    @Test
    public void testCreationOfNewUser() throws Exception {
        String url = AddressUtils.getURL(addressService.getServerURL(), "api/user", port);
        RestTemplate rest = new RestTemplate();
        User newUser = random.nextObject(User.class, "id");
        HttpEntity<User> requestObj = new HttpEntity<>(newUser);
        final ResponseEntity<Void> response = rest.exchange(url, HttpMethod.POST, requestObj, Void.class);

        assertEquals("Response status code should be 201", HttpStatus.CREATED, response.getStatusCode());
        assertNull("Response body should be empty.", response.getBody());
        assertNotNull("Location header should not be null", response.getHeaders().getLocation());
        assertTrue("Location header should contain the actual server address", response.getHeaders().getLocation().toString().startsWith(url));
        assertTrue("Location header should contain a valid resource location", response.getHeaders().getLocation().toString().matches(".*/\\d+/?"));
    }
}
