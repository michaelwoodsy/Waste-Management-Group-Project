package org.seng302.project.util;

import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Singleton class for checking & creating existence of DGAA
 */
@Component
public class DGAAChecker {

    private static final Logger logger = LoggerFactory.getLogger(DGAAChecker.class.getName());
    private final UserRepository userRepository;

    /**
     * Private constructor for this singleton
     */
    @Autowired
    private DGAAChecker(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns true when a DGAA exists
     * @return true if DGAA exists, false otherwise
     */
    public Boolean dgaaExists() {
        List<User> dgaaUsers = userRepository.findByRole("defaultGlobalApplicationAdmin");
        return dgaaUsers.size() != 0;
    }

    /**
     * Checks that a DGAA exists.
     * If no DGAA exists, one is created and an error log entry is made.
     */
    public void dgaaCheck() {
        logger.info("Running check for DGAA...");

        if (!dgaaExists()) {
            logger.error("No DGAA found. Creating new DGAA...");
            //TODO: call function to create DGAA
        }
        else {
            logger.info("Check for DGAA found DGAA");
        }
    }
}
