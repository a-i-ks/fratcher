package de.iske.fratcher.user;

import de.iske.fratcher.authentication.AuthenticationService;
import de.iske.fratcher.match.LikeMatch;
import de.iske.fratcher.match.Match;
import de.iske.fratcher.match.MatchDto;
import de.iske.fratcher.match.MatchService;
import de.iske.fratcher.util.AddressService;
import org.modelmapper.ModelMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private Validator validator;

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            final User user = userService.getUser(id);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            final UserDto userDto = convertUserToDto(user);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }
    }

    /**
     * Endpoint to query the list of confirmed LikeMatches of a user.
     * Only Mods or Admin are allowed to query the list of different users
     *
     * @param id the id of the user whose match list is to be queried.
     * @return a list of confirmed LikeMatches of the user with the passed id
     */
    @RequestMapping(value = "{id}/matches", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public ResponseEntity<Iterable> getMatchesForUser(@PathVariable Long id) {
        // check rights
        final User currentUser = userService.getCurrentUser();
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!id.equals(currentUser.getId()) &&
                currentUser.isAdmin() &&
                currentUser.isMod()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        final User user = userService.getUser(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Iterable<LikeMatch> matches = matchService.getConfirmedLikeMatchesForUser(user);
        List<MatchDto> matchesDto = new ArrayList();
        matches.forEach(m -> matchesDto.add(convertToMatchDto(m)));
        return new ResponseEntity<>(matchesDto, HttpStatus.OK);

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
    public ResponseEntity<Object> addUser(@RequestBody User newUser) {

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        if (violations.isEmpty()) {
            newUser.setPassword(authenticationService.hashPassword(newUser.getPassword()));
            try {
                userService.addUser(newUser);
            } catch (DataIntegrityViolationException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.IM_USED);
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
    @SuppressWarnings("unchecked")
    public ResponseEntity<Object> getUserList() {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (userService.getCurrentUser().isAdmin()) {
            final Iterable<User> userList = userService.getUserList();
            List<UserDto> userListDto = new ArrayList();
            userList.forEach(user -> userListDto.add(convertUserToDto(user)));
            return new ResponseEntity<>(userListDto, HttpStatus.OK);
        } else {
            final UserDto curUserDto = convertUserToDto(userService.getCurrentUser());
            return new ResponseEntity<>(curUserDto, HttpStatus.OK);
        }
    }

    private UserDto convertUserToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private MatchDto convertToMatchDto(Match match) {
        return modelMapper.map(match, MatchDto.class);
    }

}
