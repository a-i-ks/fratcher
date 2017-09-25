package de.iske.fratcher.chat;

import de.iske.fratcher.match.LikeMatch;
import de.iske.fratcher.match.Match;
import de.iske.fratcher.match.MatchService;
import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
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
 * HTTP endpoint for a chat-related HTTP requests.
 *
 * @author André Iske
 * @since 2017-09-22
 */
@RestController
@RequestMapping("/api/match/{matchId}/chat")
public class ChatController {

    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * API endpoint to receive a ChatConversation for a match.
     * <p>
     * A ChatConversation can only be received by a user which is part of the match.
     *
     * @param matchId matchID for which the ChatConversation should be returned
     * @return the {@link ChatConversation} for the matchId
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Object> getChat(@PathVariable Long matchId) {
        // find match obj in database
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ResponseEntity<Object> authErrorResponse = checkAuthorisation(match);
        if (authErrorResponse != null) {
            return authErrorResponse;
        }
        ChatConversation chatForMatch = chatService.getChatForMatch(match);
        if (chatForMatch == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ChatConversationDto conversationDto = convertConversationToDto(chatForMatch);
        return new ResponseEntity<>(conversationDto, HttpStatus.OK);
    }


    /**
     * API endpoint to start a new chat for a match.
     * <p>
     * A new chat can only be initiated for a confirmed LikeMatch and can only be started
     * by a user which is part of the match.
     *
     * @param matchId matchID for which the chat should be started
     * @return OK(200) and location of chat in header
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> startChat(@PathVariable Long matchId) {
        // find match obj in database
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ResponseEntity<Object> authErrorResponse = checkAuthorisation(match);
        if (authErrorResponse != null) {
            return authErrorResponse;
        }

        ChatConversation conversation = chatService.startNewChatBetweenUsers(match);

        // Add url of new created chat to location head field
        final URI location = ServletUriComponentsBuilder
                .fromCurrentServletMapping().path("api/match/{matchId}/chat/").build()
                .expand(match.getId()).toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    /**
     * API endpoint to delete a existing chat for a match.
     * <p>
     * Status of this chat will be set to DELETED
     *
     * @param matchId matchID for which the chat should be deleted
     * @return OK(200) if successful | BAD_REQUEST(400) if already deleted or not existing
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteChat(@PathVariable Long matchId) {
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ResponseEntity<Object> authErrorResponse = checkAuthorisation(match);
        if (authErrorResponse != null) {
            return authErrorResponse;
        }
        if (chatService.deleteChatForMatch(match)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * API endpoint to send a new message in a existing chat for a match.
     * <p>
     * Sender of the message is always the currentUser. Receiver the other user of the match.
     * If the ChatConversation is not existing or was deleted, a new will be created.
     *
     * @param matchId matchID for for which the message should send
     * @return CREATED(201) if successful
     */
    @RequestMapping(value = "message", method = RequestMethod.POST)
    public ResponseEntity<Object> sendMessage(@PathVariable Long matchId, @RequestBody ChatMessage message) {
        Match match = matchService.getMatchById(matchId);
        if (match == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ResponseEntity<Object> authErrorResponse = checkAuthorisation(match);
        if (authErrorResponse != null) {
            return authErrorResponse;
        }
        //Complete passed message object
        User currentUser = userService.getCurrentUser();
        User receiver = match.getUser1().equals(currentUser) ? match.getUser2() : match.getUser1();
        message.setSender(currentUser);
        message.setReceiver(receiver);

        chatService.sendMessageInChat(match, message);

        // Add url of chat to location head field
        final URI location = ServletUriComponentsBuilder
                .fromCurrentServletMapping().path("api/match/{matchId}/chat/").build()
                .expand(match.getId()).toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }


    private ResponseEntity<Object> checkAuthorisation(Match match) {
        if (userService.isAnonymous()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!match.getUser1().equals(userService.getCurrentUser()) &&
                !match.getUser2().equals(userService.getCurrentUser())) {
            return new ResponseEntity<>("NOT_PART_OF_MATCH", HttpStatus.UNAUTHORIZED);
        } else if (!(match instanceof LikeMatch) || !match.isConfirmed()) {
            return new ResponseEntity<>("MATCH_NOT_CONFIRMED", HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    private ChatConversationDto convertConversationToDto(ChatConversation conversation) {
        return modelMapper.map(conversation, ChatConversationDto.class);
    }

    private ChatMessageDto convertToMsgToMsgDto(ChatMessage msg) {
        return modelMapper.map(msg, ChatMessageDto.class);
    }


}
