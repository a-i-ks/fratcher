package de.iske.fratcher.chat;

import de.iske.fratcher.match.Match;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ChatConversationRepository extends CrudRepository<ChatConversation, Long> {

    @Query("SELECT c FROM ChatConversation c WHERE c.match = :m")
    ChatConversation findConversationForMatch(@Param("m") Match match);
}
