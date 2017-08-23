package de.iske.fratcher.user;

import de.iske.fratcher.util.AddressService;
import de.iske.fratcher.util.AddressUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private static final Logger LOG = LoggerFactory.getLogger(UserControllerTest.class);

    @LocalServerPort
    int port;

    @Autowired
    private AddressService addressService;

    /**
     * Test that listing of user works.
     */
    @Test
    @Transactional
    public void testUserList() throws MalformedURLException {
        String url = AddressUtils.getURL(addressService.getServerURL(), "api/user", port);
        LOG.info("Sending GET Request to " + url);
        RestTemplate rest = new RestTemplate();
        ResponseEntity<List> response = rest.getForEntity(url, List.class);
        List<?> users = response.getBody();
        assertEquals("HTTP status code should be 200", 200, response.getStatusCodeValue());
        assertTrue("There shouldn't be any users in the database", users.size() == 0);
    }


}
