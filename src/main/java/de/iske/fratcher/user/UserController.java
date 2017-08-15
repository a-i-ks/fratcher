package de.iske.fratcher.user;


import de.iske.fratcher.util.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * HTTP endpoint for a user-related HTTP requests.
 * @author André Iske
 * @since 2017-07-24
 */
@RestController
public class UserController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "api/user", method = RequestMethod.POST)
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        userService.addUser(user);
        UserCreated userCreated = new UserCreated(user,addressService.getServerURL());
        return ResponseEntity.ok(userCreated);
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.GET)
    public Iterable<User> getUserList() {
        return userService.getUserList();
    }

    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    private static class UserCreated {

        public String url;
        public UserCreated(User user,String serverUrl) {
            this.url = serverUrl + "/api/user/" + user.getId();
        }
    }
}
