package de.iske.fratcher.chat;

import org.springframework.data.repository.CrudRepository;

public interface ChatConversationRepository extends CrudRepository<ChatConversation, Long> {
}
