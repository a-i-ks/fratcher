package de.iske.fratcher.chat;


import de.iske.fratcher.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for all chat-related functional operations.
 *
 * @author André Iske
 * @since 2017-09-22
 */
@Service
public class ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private UserService userService;


}
