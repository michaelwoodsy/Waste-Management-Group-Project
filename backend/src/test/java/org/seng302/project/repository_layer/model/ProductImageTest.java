package org.seng302.project.repository_layer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Model tests for product images
 */
@SpringBootTest
class ProductImageTest{


    /**
     * Tests that adding an image to a product's list of images is successful
     */
    @Test
    void addImage() {
        Product testProduct = new Product("BEANS", "Watties Beans", "Just Beans", "Watties", 3.5, 1);
        Image image = new Image("testImage.jpg", "testImageThumbnail.jpg");
        testProduct.addImage(image);
        Assertions.assertEquals(1, testProduct.getImages().size());
        Assertions.assertEquals("testImage.jpg", testProduct.getImages().get(0).getFilename());
        Assertions.assertEquals("testImageThumbnail.jpg", testProduct.getImages().get(0).getThumbnailFilename());
    }

    /**
     * Tests that (adding then) removing an image from a product's list of images is successful
     */
    @Test
    void removeImage() {
        Product testProduct = new Product("BEANS", "Watties Beans", "Just Beans", "Watties", 3.5, 1);
        Image image = new Image("testImage.jpg", "testImageThumbnail.jpg");
        testProduct.addImage(image);
        Assertions.assertEquals(1, testProduct.getImages().size());
        testProduct.removeImage(image);
        Assertions.assertEquals(0, testProduct.getImages().size());
    }
}