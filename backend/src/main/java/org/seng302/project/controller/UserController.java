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
        } catch (AuthenticationException authException) {
            InvalidLoginException loginException = new InvalidLoginException();
            logger.info(loginException.getMessage());
            throw loginException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while authenticating user: %s", exception.getMessage()));
            throw exception;
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

        try {
            if (newUser.getFirstName() == null || newUser.getFirstName().equals("") ||
                    newUser.getLastName() == null || newUser.getLastName().equals("") ||
                    newUser.getEmail() == null || newUser.getEmail().equals("") ||
                    newUser.getDateOfBirth() == null || newUser.getDateOfBirth().equals("") ||
                    newUser.getHomeAddress() == null || newUser.getHomeAddress().equals("")) { // TODO change to address class once implemented.
                RequiredFieldsMissingException requiredFieldsMissingException = new RequiredFieldsMissingException();
                logger.warn(requiredFieldsMissingException.getMessage());
                throw requiredFieldsMissingException;
            }

            String emailRegEx = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@" +
                    "((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            if (!newUser.getEmail().matches(emailRegEx)) {
                InvalidEmailException emailException = new InvalidEmailException();
                logger.warn(emailException.getMessage());
                throw emailException;
            }

            String phoneRegEx = "^\\+?\\d{1,15}$";
            if ((newUser.getPhoneNumber() != null && !newUser.getPhoneNumber().equals(""))
                    && !(newUser.getPhoneNumber().replaceAll("[\\s-]", "")).matches(phoneRegEx)) {
                InvalidPhoneNumberException phoneNumberException = new InvalidPhoneNumberException();
                logger.warn(phoneNumberException.getMessage());
                throw phoneNumberException;
            }

            String passwordRegEx = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}";
            if (!newUser.getPassword().matches(passwordRegEx)) {
                InvalidPasswordException passwordException = new InvalidPasswordException();
                logger.warn(passwordException.getMessage());
                throw passwordException;
            }

            Date dateOfBirthDate;
            Date currentDate = new Date();
            try {
                dateOfBirthDate = new SimpleDateFormat("yyyy-MM-dd").parse(newUser.getDateOfBirth());
            } catch (ParseException parseException) {
                InvalidDateException invalidDateException = new InvalidDateException();
                logger.warn(invalidDateException.getMessage());
                throw invalidDateException;
            } catch (Exception exception) {
                logger.error(String.format("Unexpected error while parsing date: %s", exception.getMessage()));
                throw exception;
            }

            if (DateArithmetic.getDiffYears(dateOfBirthDate, currentDate) < 13) {
                UserUnderageException underageException = new UserUnderageException();
                logger.warn(underageException.getMessage());
                throw underageException;
            }

            if (!userRepository.findByEmail(newUser.getEmail()).isEmpty()) {
                ExistingRegisteredEmailException emailException = new ExistingRegisteredEmailException();
                logger.warn(emailException.getMessage());
                throw emailException;
            }

            LoginCredentials credentials = new LoginCredentials(newUser.getEmail(), newUser.getPassword());
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            newUser.setRole("user");
            userRepository.save(newUser);
            logger.info(String.format("Successful registration of user %d", newUser.getId()));
            return authenticate(credentials);
        } catch (InvalidEmailException | InvalidPhoneNumberException | ExistingRegisteredEmailException
                | InvalidDateException | UserUnderageException | RequiredFieldsMissingException expectedException) {
            throw expectedException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while creating user: %s", exception.getMessage()));
            throw exception;
        }
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
            for (Business business : currUser.getBusinessesAdministered()) {
                business.setAdministrators(new ArrayList<>());
            }

            return currUser;
        } catch (NoUserExistsException noUserExistsException) {
            logger.info(noUserExistsException.getMessage());
            throw noUserExistsException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while getting user: %s", exception.getMessage()));
            throw exception;
        }
    }
}
