package de.iske.fratcher.user;

import de.iske.fratcher.util.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        userService.addUser(user);
        UserCreated userCreated = new UserCreated(user, addressService.getServerURL());
        return ResponseEntity.ok(userCreated);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Object> getUserList() {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userService.getUserList(), HttpStatus.OK);
    }

}
