package org.seng302.project.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.exceptions.*;
import org.seng302.project.model.Business;
import org.seng302.project.model.LoginCredentials;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.seng302.project.util.DateArithmetic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * REST controller for handling requests to do with users.
 */
@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class.getName());
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(BCryptPasswordEncoder passwordEncoder,
                          UserRepository userRepository,
                          AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Attempts to authenticate a user account. Mapping to HTTP post request.
     * Handles cases that may result in an error.
     *
     * @param loginCredentials a small object that just contains a username and password
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public JSONObject authenticate(@RequestBody LoginCredentials loginCredentials) throws InvalidLoginException {
        logger.info(String.format("Request to login user: %s", loginCredentials.getEmail()));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginCredentials.getEmail(), loginCredentials.getPassword());
        try {
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            Integer userId = userRepository.findByEmail(loginCredentials.getEmail()).get(0).getId();
            JSONObject response = new JSONObject();
            response.put("userId", userId);
            logger.info("Login successful");
            return response;
        } catch (AuthenticationException exception) {
            InvalidLoginException loginException = new InvalidLoginException();
            logger.error(loginException.getMessage());
            throw loginException;
        }
    }

    /**
     * Creates a new user account.
     * Then logs the user in.
     * Handles cases that may result in an error.
     *
     * @param newUser request body in the form of a User object
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public JSONObject createUser(@RequestBody User newUser) {
        logger.info("Request to create user");

        String emailRegEx = "^[\\w\\-]+(\\.[\\w\\-]+)*@\\w+(\\.\\w+)+$";
        if (!(newUser.getEmail().matches(emailRegEx))) {
            InvalidEmailException exception = new InvalidEmailException();
            logger.error(exception.getMessage());
            throw exception;
        }

        String phoneRegEx = "^\\+[1-9]\\d{1,14}$";
        if (!newUser.getPhoneNumber().equals("") && !(newUser.getPhoneNumber().replaceAll("[\\s-]", "")).matches(phoneRegEx)) {
            InvalidPhoneNumberException exception = new InvalidPhoneNumberException();
            logger.error(exception.getMessage());
            throw exception;
        }

        if (!userRepository.findByEmail(newUser.getEmail()).isEmpty()) {
            ExistingRegisteredEmailException exception = new ExistingRegisteredEmailException();
            logger.error(exception.getMessage());
            throw exception;
        }

        Date dateOfBirthDate;
        Date currentDate = new Date();
        try {
            dateOfBirthDate = new SimpleDateFormat("yyyy-MM-dd").parse(newUser.getDateOfBirth());
        } catch (ParseException ex) {
            InvalidDateException exception = new InvalidDateException();
            logger.error(exception.getMessage());
            throw exception;
        }

        if (DateArithmetic.getDiffYears(dateOfBirthDate, currentDate) < 13) {
            UserUnderageException exception = new UserUnderageException();
            logger.error(exception.getMessage());
            throw exception;
        }

        if (newUser.getFirstName().equals("") ||
                newUser.getLastName().equals("") ||
                newUser.getEmail().equals("") ||
                newUser.getDateOfBirth().equals("") ||
                newUser.getHomeAddress().equals("")) {
            RequiredFieldsMissingException exception = new RequiredFieldsMissingException();
            logger.error(exception.getMessage());
            throw exception;
        }

        LoginCredentials credentials = new LoginCredentials(newUser.getEmail(), newUser.getPassword());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole("user");
        userRepository.save(newUser);
        logger.info(String.format("Successful registration of user %d", newUser.getId()));
        return authenticate(credentials);
    }

    /**
     * Retrieve a specific user account.
     * Handles cases that may result in an error
     *
     * @param id ID of user to get information from.
     * @return Response back to client encompassing status code, headers, and body.
     */
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id) {

        logger.info(String.format("Request to get user %d", id));
        try {
            User currUser = userRepository.findById(id).orElseThrow(() -> new NoUserExistsException(id));

            //Do this so the return is not an infinite loop of businesses and users
            for (Business business: currUser.getBusinessesAdministered()) {
                business.setAdministrators(new ArrayList<>());
            }

            return currUser;
        } catch (NoUserExistsException exception) {
            logger.error(exception.getMessage());
            throw exception;
        }
    }
}
