package org.seng302.project.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * Unit tests for Product class
 */
@SpringBootTest
public class ProductTest {
    /**
     * Creates the example product from the API.
     * Then checks all of the attributes of the new Product.
     * (Except the images attribute)
     */
    @Test
    public void createTestProduct() {
        Product testProduct = new Product("WATT-420-BEANS", "Watties Baked Beans - 420g can",
                "Baked Beans as they should be.", 2.2);

        Assertions.assertEquals("WATT-420-BEANS", testProduct.getId());
        Assertions.assertEquals("Watties Baked Beans - 420g can", testProduct.getName());
        Assertions.assertEquals("Baked Beans as they should be.", testProduct.getDescription());
        Assertions.assertEquals(2.2, testProduct.getRecommendedRetailPrice());
        Assertions.assertTrue(testProduct.getCreated().isBefore(LocalDateTime.now()));
        Assertions.assertTrue(testProduct.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
    }
}
