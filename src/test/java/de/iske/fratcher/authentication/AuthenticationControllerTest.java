package de.iske.fratcher.authentication;

import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.AddressService;
import de.iske.fratcher.util.AddressUtils;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.FieldDefinitionBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.EmailRandomizer;
import io.github.benas.randombeans.randomizers.text.StringRandomizer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
            .randomize(FieldDefinitionBuilder.field().named("email").ofType(String.class).inClass(User.class).get(),
                    new EmailRandomizer())
            .randomize(FieldDefinitionBuilder.field().named("password").ofType(String.class).inClass(User.class).get(),
                    new StringRandomizer(8, 25, System.currentTimeMillis()))
            .build();
    @Test
    public void testLoginWithMailAndCorrectPwd() throws MalformedURLException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<UserLogin> loginRequest = new HttpEntity<>(new UserLogin("andre.iske@mailbox.org", "powerlan"));
        final String loginUrl = AddressUtils.getURL(addressService.getServerURL(), "api/user/login", port);
        final ResponseEntity<AuthenticationService.UserToken> loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginRequest, AuthenticationService.UserToken.class);

        final String token = loginResponse.getBody().token;

        assertEquals("HTTP response code should be 202 (ACCEPTED).", HttpStatus.ACCEPTED, loginResponse.getStatusCode());
        assertNotNull("Response body shouldn't be empty", loginResponse.getBody());
        assertEquals("Response user should be the same which had send the request", "andre.iske@mailbox.org", loginResponse.getBody().user.getEmail());
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

    @Test
    public void testLoginWithUsernameAndCorrectPwd() throws MalformedURLException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<UserLogin> loginRequest = new HttpEntity<>(new UserLogin("admin", "powerlan"));
        final String loginUrl = AddressUtils.getURL(addressService.getServerURL(), "api/user/login", port);
        final ResponseEntity<AuthenticationService.UserToken> loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginRequest, AuthenticationService.UserToken.class);

        final String token = loginResponse.getBody().token;

        assertEquals("HTTP response code should be 202 (ACCEPTED).", HttpStatus.ACCEPTED, loginResponse.getStatusCode());
        assertNotNull("Response body shouldn't be empty", loginResponse.getBody());
        assertEquals("Response user should be the same which had send the request", "andre.iske@mailbox.org", loginResponse.getBody().user.getEmail());
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

    @Test
    public void testLoginUnknownUsernameAndCorrectPwd() throws MalformedURLException {
        RestTemplate restTemplate = new RestTemplate();
        final String randUsername = random.nextObject(String.class);
        HttpEntity<UserLogin> loginRequest = new HttpEntity<>(new UserLogin(randUsername, "kla4st#en"));
        final String loginUrl = AddressUtils.getURL(addressService.getServerURL(), "api/user/login", port);

        thrown.expect(HttpClientErrorException.class);
        thrown.expect(hasProperty("statusCode", is(HttpStatus.UNAUTHORIZED)));
        thrown.expect(hasProperty("responseBodyAsString", is("")));

        ResponseEntity<AuthenticationService.UserToken> loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginRequest, AuthenticationService.UserToken.class);
    }

    @Test
    public void testLoginWithUsernameAndWrongPwd() throws MalformedURLException {
        RestTemplate restTemplate = new RestTemplate();
        final String randPassword = random.nextObject(String.class);
        HttpEntity<UserLogin> loginRequest = new HttpEntity<>(new UserLogin("admin", randPassword));
        final String loginUrl = AddressUtils.getURL(addressService.getServerURL(), "api/user/login", port);

        thrown.expect(HttpClientErrorException.class);
        thrown.expect(hasProperty("statusCode", is(HttpStatus.UNAUTHORIZED)));
        thrown.expect(hasProperty("responseBodyAsString", is("")));

        ResponseEntity<AuthenticationService.UserToken> loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginRequest, AuthenticationService.UserToken.class);
    }


}
