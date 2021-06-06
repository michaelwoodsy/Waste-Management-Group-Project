package org.seng302.project.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class ImageUtilTest {


    private ImageUtil imageUtil;


    @BeforeEach
    public void init() {
        imageUtil = new ImageUtil();
    }

    /**
     * Checks that a thumbnail is created.
     * Used in the documented manual test to create the thumbnail.
     * The manual test then checks for the thumbnail's dimensions.
     */
    @Test
    void testCreateThumbnail() throws IOException {
        //delete existing thumbnail if it exists
        File existingThumbnail = new File("../media/asparagus_thumbnail.jpg");
        existingThumbnail.delete(); //Returns false if file does not exist

        //create new thumbnail, current working directory is team-200/backend
        imageUtil.createThumbnail("../media/asparagus.jpg");

        //assert new thumbnail exists
        File newThumbnail = new File("../media/asparagus_thumbnail.jpg");
        Assertions.assertTrue(newThumbnail.exists());

    }
}
