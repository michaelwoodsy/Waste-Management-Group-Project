package org.seng302.project.service_layer.service;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.model.ConformationToken;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LostPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(LostPasswordService.class.getName());

    private final ConformationTokenRepository conformationTokenRepository;

    @Autowired
    public LostPasswordService(ConformationTokenRepository conformationTokenRepository) {
        this.conformationTokenRepository = conformationTokenRepository;
    }

    /**
     * Authenticates a received token by checking if it is in the ConformationTokenRepository.
     *
     * @param token Token to authenticate.
     * @return The email of the user the token belongs to.
     */
    public JSONObject validateToken(String token) {
        var conformationToken = conformationTokenRepository.findByToken(token).orElseThrow(() ->
                new NotAcceptableException("Lost Password Token is not valid"));
        var returnJSON = new JSONObject();
        returnJSON.put("email", conformationToken.getUser().getEmail());
        return returnJSON;
    }

    /**
     * Method that gets called every 60 seconds that removes all confirmation tokens
     * that have been active for an hour or longer.
     */
    @Scheduled(fixedRate = 60000)
    public void removeConfirmationTokenAfter1Hr() {
        logger.info("Removing confirmation tokens that have been active for an hour or more");
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<ConformationToken> currentTokens = conformationTokenRepository.findAll();

        for (ConformationToken token : currentTokens) {
            LocalDateTime tokenCreated = token.getCreated();
            if (tokenCreated.isBefore(oneHourAgo)) {
                conformationTokenRepository.delete(token);
            }
        }
    }
}
