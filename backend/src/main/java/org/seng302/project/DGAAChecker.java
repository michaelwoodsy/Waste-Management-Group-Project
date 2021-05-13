package org.seng302.project;

import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

/**
 * Singleton class for checking & creating existence of DGAA
 */

public class DGAAChecker {

    private static final Logger logger = LoggerFactory.getLogger(DGAAChecker.class.getName());

    private final UserRepository userRepository;

    private static DGAAChecker dgaaCheckerInstance = null;

    /**
     * Private constructor for this singleton
     */
    private DGAAChecker(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Method for getting the DGAAChecker instance
     * @return  DGAAChecker
     */
    public static DGAAChecker getInstance(UserRepository userRepository) {
        if (dgaaCheckerInstance == null) {
            dgaaCheckerInstance = new DGAAChecker(userRepository);
        }
        return dgaaCheckerInstance;
    }

    /**
     * Checks that a DGAA exists.
     * If no DGAA exists, one is created and an error log entry is made.
     */
    public void dgaaExists() {
        List<User> dgaaUsers = userRepository.findByRole("defaultGlobalApplicationAdmin");

        if (dgaaUsers.size() == 0) {
            logger.error("No DGAA found. Creating new DGAA...");
            //TODO: call function to create DGAA
        }
    }
}
