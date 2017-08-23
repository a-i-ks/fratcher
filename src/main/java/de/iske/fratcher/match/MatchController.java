package de.iske.fratcher.match;

import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP endpoint for a match-related HTTP requests.
 * @author André Iske
 * @since 2017-08-01
 */
@RestController
@RequestMapping("/api/match")
public class MatchController {

    private static final Logger LOG = LoggerFactory.getLogger(MatchController.class);


    @Autowired
    UserService userService;

    @Autowired
    MatchService matchService;

    @Autowired
    AddressService addressService;

    /**
     * This REST endpoint is used to "like" another user
     *
     * @param likedUser - The user that the current "likes"
     * @return MatchCreated object with url of new match and
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> addMatch(@RequestBody User likedUser) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Match match = matchService.likeUser(likedUser);
        MatchCreated matchCreated = new MatchCreated(match, addressService.getServerURL());
        return ResponseEntity.ok(matchCreated);
    }

}
