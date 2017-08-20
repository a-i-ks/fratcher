package de.iske.fratcher.user;

import de.iske.fratcher.util.AddressService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *  Integration Test for UserController
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
    private AddressService addressService;

    /**
     * Test that listing posts works.
     */
    @Test
    @Rollback
    public void testUserList() throws MalformedURLException {
        String url = getURL();
        LOG.info("Sending GET Request to "+url);
        RestTemplate rest = new RestTemplate();
        ResponseEntity<List> response = rest.getForEntity(getURL(), List.class);
        List<User> users = response.getBody();
        assertEquals("HTTP status code should be 200",200, response.getStatusCodeValue());
        assertTrue("There shouldn't be any users in the database", users.size() == 0);
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
        url.append("api/user");
        return url.toString();
    }

}
