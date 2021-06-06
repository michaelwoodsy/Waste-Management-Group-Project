package org.seng302.project.serviceLayer.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class containing constants that are set up based on active spring profile
 */
@Component
public class SpringEnvironment {

    /**
     * Turns on/off DEV_MODE
     */
    public Boolean DEV_MODE = false;
    /**
     * Toggles whether repositories should be filled with test data
     */
    public Boolean TEST_DATA = false;
    /**
     * DGAA email obtained from environment variable
     */
    public String DGAA_EMAIL = System.getenv("DGAA_EMAIL");
    /**
     * DGAA password obtained from environment variable
     */
    public String DGAA_PASSWORD = System.getenv("DGAA_PASSWORD");
    /**
     * Spring profile that is currently being used
     */
    @Value("${spring.profiles.active}")
    private List<String> activeProfile;

    /**
     * Sets the environment based on spring profile
     */
    public void setEnvironment() {
        if (activeProfile.contains("local")) {
            DEV_MODE = true;
            TEST_DATA = true;
            if (DGAA_EMAIL == null) {
                DGAA_EMAIL = "admin";
            }
            if (DGAA_PASSWORD == null) {
                DGAA_PASSWORD = "password";
            }
        }
    }

}