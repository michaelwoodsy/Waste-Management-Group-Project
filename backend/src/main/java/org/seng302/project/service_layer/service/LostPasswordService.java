package org.seng302.project.service_layer.service;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
