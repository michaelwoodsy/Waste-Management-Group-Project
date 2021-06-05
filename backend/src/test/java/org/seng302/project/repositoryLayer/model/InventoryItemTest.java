package org.seng302.project.repositoryLayer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.repositoryLayer.repository.InventoryItemRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

/**
 * Tests related to the InventoryItem class.
 */
@SpringBootTest
public class InventoryItemTest {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void initTest() {
        inventoryItemRepository.deleteAll();
        productRepository.deleteAll();
    }

    /**
     * Creates a test InventoryItem, inspired by the API.
     * Then checks all of the attributes of the new InventoryItem
     */
    @Test
    public void createTestInventoryItem() {
        Product testInventoryItemProduct = new Product("VALUE-420-BEANS", "Value Baked Beans - 420g can",
                "Cheap Baked Beans", "", 1.1, 1);

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

    /**
     * Creates a test InventoryItem, and query's the repository to get the inventory item
     */
    @Test
    public void testRepository() {
        Product testInventoryItemProduct= new Product("VALUE-420-BEANS", "Value Baked Beans - 420g can",
                "Cheap Baked Beans", "", 1.1, 1);

        productRepository.save(testInventoryItemProduct);

        InventoryItem testInventoryItem = new InventoryItem(testInventoryItemProduct, 4, 1.0,
                4.0, "", "", "", "2021-11-27");

        inventoryItemRepository.save(testInventoryItem);

        //Test finding single inventory item
        Optional<InventoryItem> retrievedInventoryItemOptions = inventoryItemRepository.findById(testInventoryItem.getId());
        Assertions.assertTrue(retrievedInventoryItemOptions.isPresent());

        InventoryItem retrievedInventoryItem = retrievedInventoryItemOptions.get();

        Assertions.assertEquals("VALUE-420-BEANS", retrievedInventoryItem.getProduct().getId());
        Assertions.assertEquals(4, retrievedInventoryItem.getQuantity());
        Assertions.assertEquals(1.0, retrievedInventoryItem.getPricePerItem());
        Assertions.assertEquals(4.0, retrievedInventoryItem.getTotalPrice());
        Assertions.assertEquals("", retrievedInventoryItem.getManufactured());
        Assertions.assertEquals("", retrievedInventoryItem.getSellBy());
        Assertions.assertEquals("", retrievedInventoryItem.getBestBefore());
        Assertions.assertEquals("2021-11-27", retrievedInventoryItem.getExpires());



        //Test finding all businesses inventory items
        List<InventoryItem> retrievedInventoryItems = inventoryItemRepository.findAllByBusinessId(1);

        Assertions.assertEquals(1, retrievedInventoryItems.size());

        retrievedInventoryItem = retrievedInventoryItems.get(0);

        Assertions.assertEquals("VALUE-420-BEANS", retrievedInventoryItem.getProduct().getId());
        Assertions.assertEquals(4, retrievedInventoryItem.getQuantity());
        Assertions.assertEquals(1.0, retrievedInventoryItem.getPricePerItem());
        Assertions.assertEquals(4.0, retrievedInventoryItem.getTotalPrice());
        Assertions.assertEquals("", retrievedInventoryItem.getManufactured());
        Assertions.assertEquals("", retrievedInventoryItem.getSellBy());
        Assertions.assertEquals("", retrievedInventoryItem.getBestBefore());
        Assertions.assertEquals("2021-11-27", retrievedInventoryItem.getExpires());

    }

}
