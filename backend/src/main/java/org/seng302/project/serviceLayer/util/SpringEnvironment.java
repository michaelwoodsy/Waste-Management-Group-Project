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

    @Value("${spring.web.resources.static-locations}")
    private List<String> mediaFolderPathList;

    /**
     * Sets the environment based on spring profile
     */
    public void setEnvironment() {
        if (activeProfile.contains("local") || activeProfile.contains("test")) {
            DEV_MODE = true;
            TEST_DATA = true;
            if (DGAA_EMAIL == null) {
                DGAA_EMAIL = "admin";
            }
            if (DGAA_PASSWORD == null) {
                DGAA_PASSWORD = "password";
            }
        }
        if (activeProfile.contains("test")) {
            TEST_DATA = false;
        }

    }

    /**
     * Sets the media folder path based on the spring profile
     */
    public String getMediaFolderPath() {
        if (activeProfile.contains("test")) {
            return "build/resources/test/public";
        }
        /*
        if (activeProfile.contains("local")) {
            return "build/resources/main/public";
        }
        else {
            //Returns the path to the directory with the media folder in it, removes the starting substring 'file:'
            return mediaFolderPathList.get(0).substring(mediaFolderPathList.get(0).indexOf(":") + 1);
        }
        */
        //return mediaFolderPathList.get(0).substring(mediaFolderPathList.get(0).indexOf(":") + 1);
        return "build/resources/main/public";
    }

}