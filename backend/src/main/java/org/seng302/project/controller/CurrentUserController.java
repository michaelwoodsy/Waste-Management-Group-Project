package org.seng302.project.controller;

import org.seng302.project.model.User;

public class CurrentUserController {

    //Instance of the CurrentUserController
    private static CurrentUserController instance;

    private User currUser;

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
     * @param user the User object of the logged in user
     */
    public void setUser(User user) {
        instance.currUser = user;
    }

    /**
     * Gets the User object for the currently logged in user. Called when a controller needs to know who is currently logged in
     */
    public User getUser() {
        return instance.currUser;
    }
}
