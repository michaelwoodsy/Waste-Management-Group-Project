package org.seng302.project.repositoryLayer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Model tests for user images
 */
@SpringBootTest
class UserImageTest {

    /**
     * Tests that adding an image to a User's list of images is successful
     */
    @Test
    void addImage() {
        User testUser = new User("John", "Smith", "Arthur", "Jonny",
                "Likes long walks on the beach", "johnsmith9999@gmail.com",
                "1999-04-27", "+64 3 555 0129", null,
                "1337-H%nt3r2");
        Image image = new Image("testImage.jpg", "testImageThumbnail.jpg");
        testUser.addImage(image);
        Assertions.assertEquals(1, testUser.getImages().size());
        boolean matchesFilename = false;
        boolean matchesThumbnailFilename = false;

        //Iterating is the only way to get values in a set
        for (Image userImage : testUser.getImages()) {
            if (userImage.getFilename().equals("testImage.jpg")) {
                matchesFilename = true;
            }
            if (userImage.getThumbnailFilename().equals("testImageThumbnail.jpg")) {
                matchesThumbnailFilename = true;
            }
        }

        Assertions.assertTrue(matchesFilename);
        Assertions.assertTrue(matchesThumbnailFilename);
    }

    /**
     * Tests that (adding then) removing an image from a User's list of images is successful
     */
    @Test
    void removeImage() {
        User testUser = new User("John", "Smith", "Arthur", "Jonny",
                "Likes long walks on the beach", "johnsmith9999@gmail.com",
                "1999-04-27", "+64 3 555 0129", null,
                "1337-H%nt3r2");
        Image image = new Image("testImage.jpg", "testImageThumbnail.jpg");
        testUser.addImage(image);
        Assertions.assertEquals(1, testUser.getImages().size());
        testUser.removeImage(image);
        Assertions.assertEquals(0, testUser.getImages().size());
    }
}
