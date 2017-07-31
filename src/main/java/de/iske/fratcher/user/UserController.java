package de.iske.fratcher.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

/**
 * HTTP endpoint for a user-related HTTP requests.
 * @author André Iske
 * @since 2017-07-24
 */
@RestController
public class UserController {

    private static class UserCreated {
        public UserCreated(User user) {
            this.url = "localhost:8080/api/user/" + user.getId();
        }
        public String url;
    }

    @Autowired
    private UserService userService;

    @RequestMapping(value = "api/user", method = RequestMethod.POST)
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        userService.addUser(user);
        //TODO Find a better way to return URL of recently created user
        UserCreated userCreated = new UserCreated(user);
        return ResponseEntity.ok(userCreated);
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.GET)
    public Stream<User> getUserList() {
        return userService.getUserList();
    }

    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }
}
