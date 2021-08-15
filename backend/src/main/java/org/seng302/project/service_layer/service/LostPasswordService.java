package org.seng302.project.service_layer.service;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.user.ChangePasswordDTO;
import org.seng302.project.service_layer.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LostPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(LostPasswordService.class.getName());

    private final ConformationTokenRepository conformationTokenRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public LostPasswordService(ConformationTokenRepository conformationTokenRepository,
                               UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.conformationTokenRepository = conformationTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
     * Changes a users password that corresponds to the ConformationToken user.
     *
     * @param dto DTO containing the token and the users new password, the password being already validated
     */
    public void changePassword(ChangePasswordDTO dto) {
        //Get ConformationToken from repository
        var conformationToken = conformationTokenRepository.findByToken(dto.getToken()).orElseThrow(() ->
                new NotAcceptableException("Lost Password Token is not valid"));
        var user = conformationToken.getUser();
        //Change users password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        //Remove the token after it has been used
        conformationTokenRepository.delete(conformationToken);
    }
}
