package de.iske.fratcher.user;

import de.iske.fratcher.util.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private Validator validator;

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
        }
    }


    /**
     * Endpoint for registering a new user. Expects a user object send by POST.
     * Will validate the new user values and create only a new user if it has
     * validate values. Won't create user if email or username are already existing.
     *
     * @param newUser the new user that should be created
     * @return The created user object
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<String> addUser(@RequestBody User newUser) {

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        if (violations.isEmpty()) {
            try {
                userService.addUser(newUser);
            } catch (DataIntegrityViolationException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>("ALREADY_EXISTING", HttpStatus.BAD_REQUEST);
            }
            UserCreated userCreated = new UserCreated(newUser, addressService.getServerURL());
            // Add url of new created user to Location head field
            final URI location = ServletUriComponentsBuilder
                    .fromCurrentServletMapping().path("api/user/{id}").build()
                    .expand(newUser.getId()).toUri();
            final HttpHeaders headers = new HttpHeaders();
            headers.setLocation(location);

            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } else {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<User> violation : violations) {
                errorMessage.append("Invalid ");
                errorMessage.append(violation.getPropertyPath().toString());
                errorMessage.append(" (");
                errorMessage.append(violation.getMessage());
                errorMessage.append(")\n");
            }
            return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
        }
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
