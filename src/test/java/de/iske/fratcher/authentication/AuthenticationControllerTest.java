package de.iske.fratcher.authentication;

import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.AddressService;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


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

    @Before
    public void setup() {
        User adminUser = new User();
        adminUser.setEmail("admin@fratcher.de");
        adminUser.setUsername("admin");
        adminUser.setPassword(DigestUtils.sha512Hex(salt + "kla4st#en"));
        adminUser.setFirstName("Admin");
        adminUser.setLastName("Admin");
        userService.addUser(adminUser);
        assertNotNull(adminUser.getId());
    }

    @Test
    @Transactional
    public void testLoginWithMailAndCorrectPwd() throws MalformedURLException {
        RestTemplate request = new RestTemplate();
        //ResponseEntity<List> response = request.postForEntity(getURL(),)


    }

    @Test
    @Transactional
    public void testTest2() {
        userService.getUserList().forEach(user -> System.out.println(user));
        assertTrue(true);
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
