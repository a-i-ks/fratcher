package de.iske.fratcher.authentication;

import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.AddressService;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


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
        HttpEntity<UserLogin> request = new HttpEntity<>(new UserLogin("admin@fratcher.de", "kla4st#en"));
        ResponseEntity<AuthenticationService.UserToken> response = restTemplate.exchange(getURL(), HttpMethod.POST, request, AuthenticationService.UserToken.class);
        assertEquals("HTTP response code should be 202 (ACCEPTED).", HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull("Response body shouldn't be empty", response.getBody());
        assertEquals("Response user should be the same which had send the request", "admin@fratcher.de", response.getBody().user.getEmail());
        assertNotNull("Response token should not be null", response.getBody().token);
        assertEquals("Response token should be correct", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBmcmF0Y2hlci5kZSIsImp0aSI6IjEifQ.xBedRIOA_j5QeUH3uUu5f6y6RufIoJdUjXjevNYLUK2SxXSRbbZmcnYaymd5uyN3j2Y445kPIAtcP1W5KSCZzw", response.getBody().token);
    }

    /**
     * Use the AdressService to get the current server address and fix the port to the correct value, that is used
     * in this test environment
     * @return the correct server url
     */
    private String getURL() throws MalformedURLException {
        URL originalUrl = new URL(addressService.getServerURL());
        URL correctUrl = new URL(originalUrl.getProtocol(),originalUrl.getHost(),port, originalUrl.getFile());
        StringBuilder url = new StringBuilder(correctUrl.toString());
        if (!url.toString().endsWith("/"))  { url.append("/"); }
        url.append("api/login");
        return url.toString();
    }
}
