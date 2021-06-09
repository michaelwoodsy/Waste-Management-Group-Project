package org.seng302.project.repository_layer.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                "Baked Beans as they should be.", "Watties", 2.2, 1);

        assertEquals("WATT-420-BEANS", testProduct.getId());
        assertEquals("Watties Baked Beans - 420g can", testProduct.getName());
        assertEquals("Baked Beans as they should be.", testProduct.getDescription());
        assertEquals("Watties", testProduct.getManufacturer());
        assertEquals(2.2, testProduct.getRecommendedRetailPrice());
        assertEquals(1, testProduct.getBusinessId());
        assertTrue(testProduct.getCreated().isBefore(LocalDateTime.now()) || testProduct.getCreated().isEqual(LocalDateTime.now()));
        assertTrue(testProduct.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
    }
}
