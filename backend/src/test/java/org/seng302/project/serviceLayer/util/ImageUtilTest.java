package org.seng302.project.serviceLayer.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SpringBootTest
class ImageUtilTest extends AbstractInitializer {

    private ImageUtil imageUtil;
    private MockMultipartFile testImageFile;

    @BeforeEach
    void setup() {
        this.initialise();
        this.imageUtil = this.getImageUtil();
        this.testImageFile = this.getTestImageFile();
    }

    /**
     * Checks that a thumbnail is created.
     * Used in the documented manual test to create the thumbnail.
     * The manual test then checks for the thumbnail's dimensions.
     */
    @Test
    void testCreateThumbnail() throws IOException {
        //create new thumbnail, current working directory is team-200/src/main/resources/public
        imageUtil.createThumbnail("src/test/resources/public/media/asparagus.jpg");

        //assert new thumbnail exists
        File newThumbnail = new File("src/test/resources/public/media/asparagus_thumbnail.jpg");
        Assertions.assertTrue(newThumbnail.exists());
        Assertions.assertTrue(newThumbnail.delete());
    }

    /**
     * Tests that a BufferedImage is successfully read from a multipart file.
     */
    @Test
    void readFromMultipartFile() throws IOException {
        BufferedImage image = imageUtil.readImageFromMultipartFile(testImageFile);
        Assertions.assertNotNull(image);
    }

}
