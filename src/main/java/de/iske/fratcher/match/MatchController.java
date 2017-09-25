package de.iske.fratcher.match;

import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.AddressService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
    private ModelMapper modelMapper;

    @Autowired
    AddressService addressService;

    /**
     * This REST endpoint is used to "like" another user
     *
     * @param likedUser - The user that the current "likes"
     * @return MatchCreated object with url of new match and the info if the match is confirmed
     */
    @RequestMapping(value = "/like", method = RequestMethod.POST)
    public ResponseEntity<MatchCreated> like(@RequestBody User likedUser) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Match match = matchService.likeUser(likedUser);
        MatchCreated matchCreated = new MatchCreated(match);
        final URI location = ServletUriComponentsBuilder
                .fromCurrentServletMapping().path("api/match/{id}").build()
                .expand(match.getId()).toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(matchCreated, headers, HttpStatus.CREATED);
    }

    /**
     * This REST endpoint is used to "dislike" another user
     *
     * @param dislikedUser - The user that the current "likes"
     * @return MatchCreated object with url of new match and the info if the match is confirmed
     */
    @RequestMapping(value = "/dislike", method = RequestMethod.POST)
    public ResponseEntity<MatchCreated> dislike(@RequestBody User dislikedUser) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Match match = matchService.dislikeUser(dislikedUser);
        MatchCreated matchCreated = new MatchCreated(match);
        final URI location = ServletUriComponentsBuilder
                .fromCurrentServletMapping().path("api/match/{id}").build()
                .expand(match.getId()).toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(matchCreated, headers, HttpStatus.CREATED);
    }

    /**
     * This REST endpoint is used to get an specific Match
     *
     * @return Match object, if user is allowed to receive this info
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getMatch(@PathVariable Long id) {
        Match match = matchService.getMatchById(id);
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (match == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (userService.getCurrentUser().getId().equals(match.getUser1().getId()) &&
                userService.getCurrentUser().getId().equals(match.getUser2().getId())) {
            LOG.info("[UNAUTHORIZED] {} requested {}", userService.getCurrentUser(), match);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!(match instanceof LikeMatch) || !match.isConfirmed()) {
            LOG.info("[UNAUTHORIZED] {} requested {}", userService.getCurrentUser(), match);
            return new ResponseEntity<>("MATCH_NOT_CONFIRMED", HttpStatus.UNAUTHORIZED);
        }
        LOG.info("{} requested {}", userService.getCurrentUser(), match);
        final MatchDto matchDto = convertToMatchDto(match);
        return new ResponseEntity<>(matchDto, HttpStatus.OK);
    }

    /**
     * API endpoint to delete a specific match.
     * <p>
     * Only confirmed LikeMatches can be deleted by one of the containing
     * users. Otherwise a user could reject a like or dislike rating
     *
     * @param id ID of the match that should be deleted
     * @return OK (200) if successful
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteMatch(@PathVariable Long id) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Match match = matchService.getMatchById(id);
        if (match == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (!match.getUser1().equals(userService.getCurrentUser()) &&
                !match.getUser2().equals(userService.getCurrentUser())) {
            return new ResponseEntity<>("NOT_PART_OF_MATCH", HttpStatus.UNAUTHORIZED);
        } else if (!(match instanceof LikeMatch)) {
            return new ResponseEntity<>("NO_LIKE_MATCH", HttpStatus.UNAUTHORIZED);
        } else if (!match.isConfirmed()) {
            return new ResponseEntity<>("NOT_CONFIRMED", HttpStatus.UNAUTHORIZED);
        }
        matchService.deleteMatch(match);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private MatchDto convertToMatchDto(Match match) {
        MatchDto matchDto = modelMapper.map(match, MatchDto.class);
        if (match.getConversation() != null) {
            matchDto.setHasChat(true);
        }
        return matchDto;
    }
}
