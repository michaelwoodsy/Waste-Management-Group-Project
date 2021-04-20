package org.seng302.project.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests related to the InventoryItem class.
 */
@SpringBootTest
public class InventoryItemTest {

    /**
     * Creates a test InventoryItem, inspired by the API.
     * Then checks all of the attributes of the new InventoryItem
     */
    @Test
    public void createTestInventoryItem() {
        Product testInventoryItemProduct= new Product("VALUE-420-BEANS", "Value Baked Beans - 420g can",
                "Cheap Baked Beans", 1.1, 1);

        InventoryItem testInventoryItem = new InventoryItem(testInventoryItemProduct, 4, 1.0,
                4.0, "", "", "", "2021-11-27");

        Assertions.assertNull(testInventoryItem.getId());
        Assertions.assertEquals("VALUE-420-BEANS", testInventoryItem.getProduct().getId());
        Assertions.assertEquals(4, testInventoryItem.getQuantity());
        Assertions.assertEquals(1.0, testInventoryItem.getPricePerItem());
        Assertions.assertEquals(4.0, testInventoryItem.getTotalPrice());
        Assertions.assertEquals("", testInventoryItem.getManufactured());
        Assertions.assertEquals("", testInventoryItem.getSellBy());
        Assertions.assertEquals("", testInventoryItem.getBestBefore());
        Assertions.assertEquals("2021-11-27", testInventoryItem.getExpires());
    }

}
