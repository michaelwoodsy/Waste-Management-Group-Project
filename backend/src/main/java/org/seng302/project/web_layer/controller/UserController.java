package org.seng302.project.web_layer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.service_layer.dto.user.*;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.register.*;
import org.seng302.project.service_layer.service.UserService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.seng302.project.service_layer.exceptions.dgaa.DGAARevokeAdminSelfException;
import org.seng302.project.service_layer.exceptions.dgaa.ForbiddenDGAAActionException;
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
    public JSONObject authenticate(@RequestBody LoginCredentialsDTO loginCredentials) throws InvalidLoginException {
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
     * @param dto request body in the form of a PostUserDTO Object
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public JSONObject createUser(@RequestBody @Valid PostUserDTO dto) {
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
     * Creates a new user account.
     * Then logs the user in.
     * Handles cases that may result in an error.
     * NOTE: All elements in the dto are required, but some can be null, as this is a PUT request and not a PATCH request
     *
     * @param dto request body in the form of a PostUserDTO Object
     */
    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void edit(@RequestBody @Valid PutUserDTO dto, @PathVariable Integer userId,
                     @AuthenticationPrincipal AppUserDetails appUser) {
        try {
            logger.info("Request to edit user with ID: {}", userId);
            dto.setId(userId);
            userService.checkForbidden(userId, appUser);
            userService.editUser(dto);
        } catch (InvalidEmailException | InvalidPhoneNumberException | ExistingRegisteredEmailException
                | InvalidDateException | UserUnderageException
                | RequiredFieldsMissingException | BadRequestException expectedException) {
            logger.info(expectedException.getMessage());
            throw expectedException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while editing user: %s", exception.getMessage()));
            throw exception;
        }
    }

    /**
     * Retrieve a specific user account.
     * Handles cases that may result in an error
     *
     * @param userId ID of user to get information from.
     * @return Response back to client encompassing status code, headers, and body.
     */
    @GetMapping("/users/{userId}")
    public GetUserDTO getUser(@PathVariable Integer userId) {
        try {
            logger.info("Request to get user with ID: {}", userId);
            return userService.getUser(userId);
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
    public List<Object> searchUsers(@RequestParam("searchQuery") String searchQuery, @RequestParam("pageNumber") Integer pageNumber
    , @RequestParam("sortBy") String sortBy) {

        logger.info("Request to search users with query: {}", searchQuery);

        try {
            return userService.searchUsers(searchQuery, pageNumber, sortBy);
        } catch (BadRequestException badRequestException) {
            logger.error(badRequestException.getMessage());
            throw badRequestException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while searching users: %s", exception.getMessage()));
            throw exception;
        }

    }
}
