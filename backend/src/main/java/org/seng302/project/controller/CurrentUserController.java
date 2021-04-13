package org.seng302.project.controller;

import org.seng302.project.exceptions.AdministratorAlreadyExistsException;
import org.seng302.project.exceptions.NoCookieExistsException;

import java.util.HashMap;
import java.util.Map;

public class CurrentUserController {

    //Instance of the CurrentUserController
    private static CurrentUserController instance;

    private Map<String, Integer> users = new HashMap<>();


    /**
     * Singleton method to store the currentley logged in user
     * @return Single DataHandler object
     */
    public static CurrentUserController GetInstance() {
        if (instance == null) {
            instance = new CurrentUserController();
        }
        return instance;
    }

    /**
     * Sets the User object for the currently logged in user. Called when lodging in or registering
     * @param id The id of the user with cookie
     * @param cookie The cookie of the session
     */
    public void setId(int id, String cookie) {
        instance.users.put(cookie, id);
    }

    /**
     * Gets the user id for the currently logged in user with cookie. Called when a controller needs to know who is currently logged in
     */
    public int getId(String cookie) {
        try {
            return instance.users.get(cookie);
        }
        catch (NullPointerException e) {
            throw new NoCookieExistsException();
        }
    }
}
