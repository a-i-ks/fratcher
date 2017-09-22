package de.iske.fratcher.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP endpoint for a chat-related HTTP requests.
 *
 * @author André Iske
 * @since 2017-08-01
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);


}
