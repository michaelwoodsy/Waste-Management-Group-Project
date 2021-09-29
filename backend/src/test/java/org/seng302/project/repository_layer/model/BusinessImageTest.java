package org.seng302.project.repository_layer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Model tests for business images
 */
@SpringBootTest
class BusinessImageTest extends AbstractInitializer {

    private Business testBusiness;

    @BeforeEach
    void setup() {
        this.initialise();
        this.testBusiness = this.getTestBusiness();
    }

    /**
     * Tests that adding an image to a Business' set of images is successful
     */
    @Test
    void addImage() {
        Image image = new Image("testImage.jpg", "testImageThumbnail.jpg");
        testBusiness.addImage(image);
        Assertions.assertEquals(1, testBusiness.getImages().size());
        boolean matchesFilename = false;
        boolean matchesThumbnailFilename = false;

        //Iterating is the only way to get values in a set
        for (Image userImage : testBusiness.getImages()) {
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
     * Tests that (adding then) removing an image from a Business' set of images is successful
     */
    @Test
    void removeImage() {
        Image image = new Image("testImage.jpg", "testImageThumbnail.jpg");
        testBusiness.addImage(image);
        Assertions.assertEquals(1, testBusiness.getImages().size());
        testBusiness.removeImage(image);
        Assertions.assertEquals(0, testBusiness.getImages().size());
    }

}
