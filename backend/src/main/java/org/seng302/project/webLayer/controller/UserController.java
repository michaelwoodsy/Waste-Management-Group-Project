package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.dto.user.*;
import org.seng302.project.serviceLayer.exceptions.*;
import org.seng302.project.serviceLayer.exceptions.register.*;
import org.seng302.project.serviceLayer.service.UserService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.seng302.project.serviceLayer.exceptions.dgaa.DGAARevokeAdminSelfException;
import org.seng302.project.serviceLayer.exceptions.dgaa.ForbiddenDGAAActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for handling requests to do with users.
 */
@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class.getName());
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Attempts to authenticate a user account. Mapping to HTTP post request.
     * Handles cases that may result in an error.
     *
     * @param loginCredentials a small object that just contains a username and password
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserLoginResponseDTO authenticate(@RequestBody LoginCredentialsDTO loginCredentials) throws InvalidLoginException {
        try {
            logger.info("Request to login user {}", loginCredentials.getEmail());
            return userService.login(loginCredentials);
        } catch (AuthenticationException authException) {
            var loginException = new InvalidLoginException();
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
     * @param dto request body in the form of a CreateUserDTO Object
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserLoginResponseDTO createUser(@RequestBody @Valid CreateUserDTO dto) {
        try {
            logger.info("Request to create a new user");
            LoginCredentialsDTO loginCredentials = userService.createUser(dto);
            return userService.login(loginCredentials);
        } catch (InvalidEmailException | InvalidPhoneNumberException | ExistingRegisteredEmailException
                | InvalidDateException | UserUnderageException | RequiredFieldsMissingException expectedException) {
            logger.info(expectedException.getMessage());
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
    public UserResponseDTO getUser(@PathVariable Integer id) {
        try {
            logger.info(String.format("Request to get user %d", id));
            return userService.getUser(id);
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
            logger.info("User with email: {} requesting to make user with ID: {} a GlobalApplicationAdmin",
                    appUser.getUsername(), id);
            userService.checkRequesterIsDGAA(appUser);
            userService.dgaaMakeAdmin(new DGAAMakeRevokeAdminDTO(id, appUser));
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
            logger.info("User with email: {} requesting to revoke user with ID: {}'s GAA privileges",
                    appUser.getUsername(), id);
            userService.checkRequesterIsDGAA(appUser);
            userService.dgaaRevokeAdmin(new DGAAMakeRevokeAdminDTO(id, appUser));

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


    /**
     * Receives a request containing a search query to search users by name and retrieves a list
     * of users based on the query.
     *
     * @param searchQuery user’s full name or one or more of their names/nickname.
     * @return 200 response with (potentially empty) list of users or 401 if not authenticated.
     */
    @GetMapping("/users/search")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDTO> searchUsers(@RequestParam("searchQuery") String searchQuery) {

        logger.info(String.format("Request to search users with query: %s", searchQuery));

        try {
            return userService.searchUsers(searchQuery);
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while searching users: %s", exception.getMessage()));
            throw exception;
        }

    }
}
