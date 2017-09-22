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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            final User user = userService.getUser(id);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            // if current user requests its own user object
            // => send full user object
            else if (user.equals(userService.getCurrentUser())) {
                return new ResponseEntity<>(user, HttpStatus.OK);
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
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
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

    /**
     * Endpoint for updating current user object. Every value that is not null will be
     * updated to the values passed to this request method.
     *
     * @param userToMerge user object that contains new values for user to update
     * @return 200 if merge was successful
     */
    @RequestMapping(value = "", method = RequestMethod.PATCH)
    public ResponseEntity<Object> updateUser(@RequestBody User userToMerge) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        userToMerge.setId(userService.getCurrentUser().getId());
        if (userToMerge.getPassword() != null && userToMerge.getPassword().length() > 0) {
            userToMerge.setPassword(authenticationService.hashPassword(userToMerge.getPassword()));
        }
        userService.mergeUser(userToMerge);
        // Add url of merged user to Location head field
        final URI location = ServletUriComponentsBuilder
                .fromCurrentServletMapping().path("api/user/{id}").build()
                .expand(userToMerge.getId()).toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    /**
     * Endpoint for updating a existing user object. Every value that is not null will be
     * updated to the values passed to this request method.
     * <p>
     * Only admins are allowed to change user objects deviating from its own user object.
     *
     * @param userToMerge user object that contains new values for user to update
     * @param userID      id of user that should be updated
     * @return 200 if merge was successful
     */
    @RequestMapping(value = "{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Object> updateUser(@RequestBody User userToMerge, @PathVariable Long userID) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userID.equals(userService.getCurrentUser().getId()) &&
                !userService.getCurrentUser().isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        userToMerge.setId(userService.getCurrentUser().getId());
        if (userToMerge.getPassword() != null && userToMerge.getPassword().length() > 0) {
            userToMerge.setPassword(authenticationService.hashPassword(userToMerge.getPassword()));
        }
        userToMerge.setId(userID);
        userService.mergeUser(userToMerge);
        // Add url of merged user to Location head field
        final URI location = ServletUriComponentsBuilder
                .fromCurrentServletMapping().path("api/user/{id}").build()
                .expand(userToMerge.getId()).toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }


    /**
     * Endpoint to delete a specific user. The user status will be set to DELETED.
     * <p>
     * Only admins are allowed to delete user objects deviating from its own user object.
     *
     * @param userID the id of the user that should be deleted.
     * @return 200 if the deleting was successful.
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUser(@PathVariable Long userID) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userID.equals(userService.getCurrentUser().getId()) &&
                !userService.getCurrentUser().isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // find user that should be deleted
        User userToDelete = userService.getUser(userID);

        userService.deleteUser(userToDelete);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Endpoint the current user. The user status will be set to DELETED.
     * *
     *
     * @return 200 if the deleting was successful.
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUser() {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        userService.deleteUser(userService.getCurrentUser());

        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Endpoint to receive a list of all existing user.
     * <p>
     * Only admins and moderators are allowed to receive a complete user list
     *
     * @return a list of all existing users
     */
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

    @RequestMapping(value = "candidates", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public ResponseEntity<Object> getMatchingCandidates(@RequestParam(value = "n", required = false) Integer numberOfCandidates) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (numberOfCandidates == null) {
            numberOfCandidates = 10;
        }
        final List<User> matchingCandidates = matchService.getMatchingCandidatesForUser(userService.getCurrentUser(), numberOfCandidates, true);
        List<UserDto> matchingCandidatesDto = new ArrayList();
        matchingCandidates.forEach(user -> matchingCandidatesDto.add(convertUserToDto(user)));
        return new ResponseEntity<>(
                matchingCandidatesDto, HttpStatus.OK);
    }


    @PostMapping("img") //new annotation since spring 4.3
    public ResponseEntity<Object> uploadUserImg(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        }
        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            String UPLOADED_FOLDER = "imgs/";
            new File(UPLOADED_FOLDER).mkdirs();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);


            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private UserDto convertUserToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private MatchDto convertToMatchDto(Match match) {
        return modelMapper.map(match, MatchDto.class);
    }

}
