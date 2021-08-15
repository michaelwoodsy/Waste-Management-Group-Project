package org.seng302.project.web_layer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.service_layer.dto.user.ChangePasswordDTO;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.service.LostPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * Rest controller for lost passwords.
 */
@RestController
public class LostPasswordController {

    private static final Logger logger = LoggerFactory.getLogger(LostPasswordController.class.getName());


    private final LostPasswordService lostPasswordService;

    @Autowired
    public LostPasswordController(
            LostPasswordService lostPasswordService) {
        this.lostPasswordService = lostPasswordService;
    }

    /**
     * Authenticates a conformation token used when a user is changing their password.
     *
     * @param token The Token to validate.
     * @return the users Email that corresponds with the conformation token.
     */
    @GetMapping("/lostpassword/validate")
    public JSONObject validateLostPasswordToken(
            @RequestParam("token") String token) {
        logger.info("Validating Lost Password Token: {}", token);
        try {
            return lostPasswordService.validateToken(token);
        } catch (NotAcceptableException exception) {
            logger.info(exception.getMessage());
            throw exception;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while validating Lost Password Token: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Changes a users password that corresponds to the token in the dto
     *
     * @param dto DTO containing the lost password token and the users new password (validated)
     */
    @PatchMapping("/lostpassword/edit")
    public void changeLostPassword(@RequestBody @Valid ChangePasswordDTO dto) {
        logger.info("Changing users password with token: {}", dto.getToken());
        try {
            lostPasswordService.changePassword(dto);
            logger.info("Successfully changed users password!");
        } catch (NotAcceptableException exception) {
            logger.info(exception.getMessage());
            throw exception;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while changing users password: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }
}