package org.seng302.project.service_layer.service;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.model.ConformationToken;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.user.ChangePasswordDTO;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.util.SpringEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LostPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(LostPasswordService.class.getName());

    private final ConformationTokenRepository conformationTokenRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SpringEnvironment springEnvironment;

    @Autowired
    public LostPasswordService(ConformationTokenRepository conformationTokenRepository,
                               UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                               EmailService emailService,
                               SpringEnvironment springEnvironment) {
        this.conformationTokenRepository = conformationTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.springEnvironment = springEnvironment;
    }

    /**
     * Authenticates a received token by checking if it is in the ConformationTokenRepository.
     *
     * @param token Token to authenticate.
     * @return The email of the user the token belongs to.
     */
    public JSONObject validateToken(String token) {
        var conformationToken = conformationTokenRepository.findByToken(token).orElseThrow(() ->
                new BadRequestException("Lost Password Token is not valid"));
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

    /**
     * Changes a users password that corresponds to the ConformationToken user.
     *
     * @param dto DTO containing the token and the users new password, the password being already validated
     */
    public void changePassword(ChangePasswordDTO dto) {
        //Get ConformationToken from repository
        var conformationToken = conformationTokenRepository.findByToken(dto.getToken()).orElseThrow(() ->
                new BadRequestException("Lost Password Token is not valid"));
        var user = conformationToken.getUser();
        //Change users password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        //Remove the token after it has been used
        conformationTokenRepository.delete(conformationToken);
    }


    /**
     * Sends a password reset email to the given email address
     * @param emailAddress email address to send the password reset email to
     */
    public void sendPasswordResetEmail(String emailAddress) {
        List<User> usersWithEmail = userRepository.findByEmail(emailAddress);
        if (usersWithEmail.isEmpty()) {
            throw new BadRequestException(String.format("No user exists with the email: %s",emailAddress));
        }
        String emailBody = "You have requested to reset your password. " +
                "Please click on the link below to do so. The link will expire after 1 hour.\n ";

        ConformationToken token = new ConformationToken(usersWithEmail.get(0));
        conformationTokenRepository.save(token);
        emailBody += springEnvironment.getPasswordResetURL(token.getToken());
        emailService.sendEmail(emailAddress, "Resale: Reset your password", emailBody);
    }
}
