package de.iske.fratcher.match;


import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service for all match-related functional operations.
 * @author André Iske
 * @since 2017-08-01
 */
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    public void addMatch(Match match) {
        matchRepository.save(match);
    }

}
