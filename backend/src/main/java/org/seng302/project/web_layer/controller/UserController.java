package org.seng302.project.web_layer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.register.*;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.seng302.project.service_layer.exceptions.dgaa.DGAARevokeAdminSelfException;
import org.seng302.project.service_layer.exceptions.dgaa.ForbiddenDGAAActionException;
import org.seng302.project.service_layer.util.DateArithmetic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final AddressRepository addressRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(BCryptPasswordEncoder passwordEncoder,
                          UserRepository userRepository,
                          AddressRepository addressRepository,
                          AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
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
            // If any of the required fields are empty
            if (newUser.getFirstName() == null || newUser.getFirstName().equals("") ||
                    newUser.getLastName() == null || newUser.getLastName().equals("") ||
                    newUser.getEmail() == null || newUser.getEmail().equals("") ||
                    newUser.getDateOfBirth() == null || newUser.getDateOfBirth().equals("") ||
                    newUser.getHomeAddress() == null || newUser.getHomeAddress().getCountry().equals("") ||
                    newUser.getPassword() == null || newUser.getPassword().equals("")
            ) {
                RequiredFieldsMissingException requiredFieldsMissingException = new RequiredFieldsMissingException();
                logger.warn(requiredFieldsMissingException.getMessage());
                throw requiredFieldsMissingException;
            }

            // If email is in incorrect format
            String emailRegEx = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@" +
                    "((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            if (!newUser.getEmail().matches(emailRegEx)) {
                InvalidEmailException emailException = new InvalidEmailException();
                logger.warn(emailException.getMessage());
                throw emailException;
            }

            // If phone number is in incorrect format or empty
            String phoneRegEx = "^(\\+\\d{1,2}\\s*)?\\(?\\d{1,6}\\)?[\\s.-]?\\d{3,6}[\\s.-]?\\d{3,8}$";
            if ((newUser.getPhoneNumber() != null && !newUser.getPhoneNumber().equals(""))
                    && !newUser.getPhoneNumber().matches(phoneRegEx)) {
                InvalidPhoneNumberException phoneNumberException = new InvalidPhoneNumberException();
                logger.warn(phoneNumberException.getMessage());
                throw phoneNumberException;
            }

            // If email address is empty
            if (!userRepository.findByEmail(newUser.getEmail()).isEmpty()) {
                ExistingRegisteredEmailException emailException = new ExistingRegisteredEmailException();
                logger.warn(emailException.getMessage());
                throw emailException;
            }

            // Check if address has a street number with no street name
            if ((newUser.getHomeAddress().getStreetName() == null || newUser.getHomeAddress().getStreetName().equals("")) &&
                    (newUser.getHomeAddress().getStreetNumber() != null && !newUser.getHomeAddress().getStreetNumber().equals(""))) {
                InvalidAddressException addressException = new InvalidAddressException();
                logger.error(addressException.getMessage());
                throw addressException;
            }

            // Check that the password meets requirements.
            String passwordRegEx = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}";
            if (!newUser.getPassword().matches(passwordRegEx)) {
                InvalidPasswordException passwordException = new InvalidPasswordException();
                logger.warn(passwordException.getMessage());
                throw passwordException;
            }

            //
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

            // Check that the user is over 13 and has selected a realistic date of birth (under 200)
            if (DateArithmetic.getDiffYears(dateOfBirthDate, currentDate) < 13) {
                UserUnderageException underageException = new UserUnderageException("an account", 13);
                logger.warn(underageException.getMessage());
                throw underageException;
            } else if (DateArithmetic.getDiffYears(dateOfBirthDate, currentDate) > 200) {
                InvalidDateException invalidDateException = new InvalidDateException(
                        "InvalidDateException: birth date is unrealistic"
                );
                logger.warn(invalidDateException.getMessage());
                throw invalidDateException;
            }

            LoginCredentials credentials = new LoginCredentials(newUser.getEmail(), newUser.getPassword());
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            newUser.setRole("user");
            addressRepository.save(newUser.getHomeAddress());
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

    /**
     * Promote a user account to the Global Application Admin role
     */
    @PutMapping("/users/{id}/makeadmin")
    public void dgaaMakeAdmin(@PathVariable int id, @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            //Check if person making request is DGAA
            User requestMaker = userRepository.findByEmail(appUser.getUsername()).get(0);

            logger.info(String.format("requestMaker: %s", requestMaker));
            if (!requestMaker.getRole().equals("defaultGlobalApplicationAdmin")) {
                throw new ForbiddenDGAAActionException();
            }

            //Check if user exists
            User user = userRepository.findById(id).orElseThrow(() -> new NoUserExistsException(id));

            //Update user's role
            user.setRole("globalApplicationAdmin");
            userRepository.save(user);

        } catch (ForbiddenDGAAActionException | NoUserExistsException handledException) {
            logger.warn(handledException.getMessage());
            throw handledException;
        } catch (Exception unHandledException) {
            logger.error(String.format(
                    "Unexpected error while giving user application admin rights: %s",
                    unHandledException.getMessage())
            );
        }
    }


    /**
     * Promote a user account to the Global Application Admin role
     */
    @PutMapping("/users/{id}/revokeadmin")
    public void dgaaRevokeAdmin(@PathVariable int id, @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            //Check if person making request is DGAA
            User requestMaker = userRepository.findByEmail(appUser.getUsername()).get(0);

            if (!requestMaker.getRole().equals("defaultGlobalApplicationAdmin")) {
                throw new ForbiddenDGAAActionException();
            }

            //Check if user exists
            User user = userRepository.findById(id).orElseThrow(() -> new NoUserExistsException(id));

            //Check that DGAA is not revoking themselves
            if (user.getEmail().equals(appUser.getUsername())) {
                throw new DGAARevokeAdminSelfException();
            }

            //Update user's role
            user.setRole("user");
            userRepository.save(user);

        } catch (ForbiddenDGAAActionException | NoUserExistsException | DGAARevokeAdminSelfException handledException) {
            logger.warn(handledException.getMessage());
            throw handledException;
        } catch (Exception unHandledException) {
            logger.error(String.format(
                    "Unexpected error while revoking user application admin rights: %s",
                    unHandledException.getMessage())
            );
        }
    }
}
