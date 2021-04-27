package org.seng302.project.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Unit test for the SaleListing class.
 */
@SpringBootTest
public class SaleListingTest {

    @Test
    public void testCreateListing() {

        // Create inventory item
        InventoryItem inventoryItem = new InventoryItem(null, 20,
                10.99, 219.8, "2021-04-25",
                "2021-04-25", "2021-04-25", "2021-04-25");
        LocalDateTime now = LocalDateTime.now();
        SaleListing listing = new SaleListing(1, inventoryItem, 3.99,
                "no", now, 1);

        // Check fields
        assertEquals(listing.getBusinessId(), 1);
        assertEquals(listing.getPrice(), 3.99);
        assertEquals(listing.getMoreInfo(), "no");
        assertEquals(listing.getCloses(), now);
        assertEquals(listing.getQuantity(), 1);
    }

}
