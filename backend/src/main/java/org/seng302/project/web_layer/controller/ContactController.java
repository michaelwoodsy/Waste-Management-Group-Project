package org.seng302.project.web_layer.controller;

import org.seng302.project.service_layer.dto.contact.PostContactDTO;
import org.seng302.project.service_layer.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Controller for endpoints related to contacting resale.
 */
@RestController
public class ContactController {

    private final EmailService emailService;
    private static final String RESALE_EMAIL = "s302resale@gmail.com";
    private static final Logger logger = LoggerFactory.getLogger(ContactController.class.getName());

    @Autowired
    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Method for contacting the resale team.
     * Sends an email to the resale team with the users message and email.
     *
     * @param reqBody Request body containing the users email and message.
     */
    @PostMapping("/contact")
    @ResponseStatus(HttpStatus.CREATED)
    public void contactResale(@RequestBody @Valid PostContactDTO reqBody) {
        var logMessage = String.format("Contact from %s: %s", reqBody.getEmail(), reqBody.getMessage());
        logger.info(logMessage);

        var emailMessage = String.format("Contact from %s: %n%n%s", reqBody.getEmail(), reqBody.getMessage());
        emailService.sendEmail(RESALE_EMAIL, "New \"Contact Us\"", emailMessage);
    }
}
