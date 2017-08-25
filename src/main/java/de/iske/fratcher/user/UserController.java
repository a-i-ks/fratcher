package de.iske.fratcher.user;

import de.iske.fratcher.util.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
        } else {
            return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        userService.addUser(user);
        UserCreated userCreated = new UserCreated(user, addressService.getServerURL());
        // Add url of new created user to Location head field
        final URI location = ServletUriComponentsBuilder
                .fromCurrentServletMapping().path("api/user/{id}").build()
                .expand(user.getId()).toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Object> getUserList() {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (userService.getCurrentUser().isAdmin()) {
            return new ResponseEntity<>(userService.getUserList(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(userService.getCurrentUser(), HttpStatus.OK);
        }
    }

}
