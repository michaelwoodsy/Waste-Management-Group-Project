package org.seng302.project.repositoryLayer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Model tests for product images
 */
public class UserImageTest {

    /**
     * Tests that adding an image to a product's list of images is successful
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
        Assertions.assertEquals("testImage.jpg", testUser.getImages().get(0).getFilename());
        Assertions.assertEquals("testImageThumbnail.jpg", testUser.getImages().get(0).getThumbnailFilename());
    }

    /**
     * Tests that (adding then) removing an image from a product's list of images is successful
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
