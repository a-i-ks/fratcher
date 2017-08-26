package de.iske.fratcher.authentication;

import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.AddressService;
import de.iske.fratcher.util.AddressUtils;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;

import static org.junit.Assert.*;


/**
 *  Integration test for AuthenticationController
 *  @author André Iske
 *  @since 2017-08-14
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationControllerTest.class);

    @LocalServerPort
    int port;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Value("${authenticationService.salt}")
    private String salt;

    private EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom();

    @Test
    public void testLoginWithMailAndCorrectPwd() throws MalformedURLException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<UserLogin> loginRequest = new HttpEntity<>(new UserLogin("admin@fratcher.de", "kla4st#en"));
        final String loginUrl = AddressUtils.getURL(addressService.getServerURL(), "api/login", port);
        final ResponseEntity<AuthenticationService.UserToken> loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginRequest, AuthenticationService.UserToken.class);

        final String token = loginResponse.getBody().token;

        assertEquals("HTTP response code should be 202 (ACCEPTED).", HttpStatus.ACCEPTED, loginResponse.getStatusCode());
        assertNotNull("Response body shouldn't be empty", loginResponse.getBody());
        assertEquals("Response user should be the same which had send the request", "admin@fratcher.de", loginResponse.getBody().user.getEmail());
        assertNotNull("Response token should not be null", token);
        assertTrue("Response token should have a correct format", token.matches("^\\S+\\.\\S+\\.\\S+$"));

        //Try to access own user information via REST
        final String getUserUrl = AddressUtils.getURL(addressService.getServerURL(), "api/user/1", port);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        final HttpEntity<Object> getUserRequest = new HttpEntity<>(null, headers);
        final ResponseEntity<User> getUserResponse = restTemplate.exchange(getUserUrl, HttpMethod.GET, getUserRequest, User.class);

        assertEquals("HTTP response code should be 200 (OK).", HttpStatus.OK, getUserResponse.getStatusCode());
    }


}
