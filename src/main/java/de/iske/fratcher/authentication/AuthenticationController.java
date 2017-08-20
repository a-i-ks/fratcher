package de.iske.fratcher.authentication;


import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * HTTP endpoint for a user-related HTTP requests.
 * @author André Iske
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/api/user")
public class AuthenticationController {
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        userService.addUser(user);
        UserCreated userCreated = new UserCreated(user, addressService.getServerURL());
        return ResponseEntity.ok(userCreated);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<User> getUserList() {
        return userService.getUserList();
    }

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<AuthenticationService.UserToken> login(@RequestBody UserLogin userLogin) {
        AuthenticationService.UserToken token = authenticationService.login(userLogin.email, userLogin.password);
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    public static class UserLogin {
        public String email;
        public String password;
    }

    private static class UserCreated {
        public String url;

        public UserCreated(User user, String serverUrl) {
            this.url = serverUrl + "/api/user/" + user.getId();
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }


}
