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
    public ResponseEntity<MatchDto> getMatch(@PathVariable Long id) {
        Match match = matchService.getMatchById(id);
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (match == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (userService.getCurrentUser().getId().equals(match.getUser1().getId()) &&
                userService.getCurrentUser().getId().equals(match.getUser2().getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        final MatchDto matchDto = convertToMatchDto(match);
        return new ResponseEntity<>(matchDto, HttpStatus.OK);
    }

    private MatchDto convertToMatchDto(Match match) {
        return modelMapper.map(match, MatchDto.class);
    }
}
