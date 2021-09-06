package org.seng302.project.web_layer.controller;

import org.seng302.project.service_layer.dto.contact.PostContactDTO;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.service.EmailService;
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

    private EmailService emailService;

    @Autowired
    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/contact")
    @ResponseStatus(HttpStatus.CREATED)
    public void contactResale(@RequestBody @Valid PostContactDTO body) {
        throw new NotAcceptableException("");
    }
}
