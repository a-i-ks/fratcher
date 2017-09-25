package de.iske.fratcher.chat;


import de.iske.fratcher.match.Match;
import de.iske.fratcher.user.UserService;
import de.iske.fratcher.util.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Autowired
    private ChatConversationRepository chatConversationRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatConversation startNewChatBetweenUsers(Match match) {
        //check if a existing conversation between user1 and user2
        ChatConversation conversation = chatConversationRepository.findConversationForMatch(match);
        if (conversation == null || conversation.getStatus() == Status.DELETED) {
            conversation = new ChatConversation();
            conversation.setMatch(match);
            chatConversationRepository.save(conversation);
            LOG.info("[ADDED] ChatConversation for {}", match);
        }
        return conversation;
    }

    public boolean deleteChatForMatch(Match match) {
        ChatConversation conversation = chatConversationRepository.findConversationForMatch(match);
        if (conversation == null || conversation.getStatus() == Status.DELETED) {
            return false;
        } else {
            conversation.setStatus(Status.DELETED);
            chatConversationRepository.save(conversation);
            LOG.info("[DELETED] ChatConversation for {}", match);
            return true;
        }
    }

    public void sendMessageInChat(Match match, ChatMessage message) {
        ChatConversation conversation = chatConversationRepository.findConversationForMatch(match);
        if (conversation == null || conversation.getStatus() == Status.DELETED) {
            conversation = new ChatConversation();
            conversation.setMatch(match);
        }
        LOG.info("[CREATED] {} in chat for match {}", message, match);
        //save message in database
        message.setConversation(conversation);
        chatConversationRepository.save(conversation);
        chatMessageRepository.save(message);
    }

    public ChatConversation getChatForMatch(Match match) {
        return chatConversationRepository.findConversationForMatch(match);
    }

    public void deleteConversation(ChatConversation conversation) {
        ChatConversation conversationToDeleted = chatConversationRepository.findOne(conversation.getId());
        conversationToDeleted.setStatus(Status.DELETED);
        chatConversationRepository.save(conversationToDeleted);
        // update messages of conversation
        List<ChatMessage> messages = conversationToDeleted.getMessages();
        if (messages != null) {
            for (int i = 0; i < messages.size(); i++) {
                ChatMessage msg = messages.get(i);
                ChatMessage msgToDelete = chatMessageRepository.findOne(msg.getId());
                msgToDelete.setStatus(Status.DELETED);
                chatMessageRepository.save(msgToDelete);
                LOG.info("[DELETED] {}", msgToDelete);
            }
        }
        LOG.info("[DELETED] {}", conversationToDeleted);
    }
}
