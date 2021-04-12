package org.seng302.project.controller;

public class CurrentUserController {

    //Instance of the CurrentUserController
    private static CurrentUserController instance;

    private int id;

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
     * Sets the id for the currently logged in user. Called when lodging in or registering
     * @param id Id of the logged in user
     */
    public void setId(int id) {
        instance.id = id;
    }

    /**
     * Gets the id for the currently logged in user. Called when a controller needs to know who is currently logged in
     */
    public int getId() {
        return instance.id;
    }
}
