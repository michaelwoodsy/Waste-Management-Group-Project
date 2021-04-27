package org.seng302.project.controller;

import org.seng302.project.model.BusinessRepository;
import org.seng302.project.model.InventoryItemRepository;
import org.seng302.project.model.ProductRepository;
import org.seng302.project.model.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for sale listings.
 */
@RestController
public class SaleListingController {

    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogueController.class.getName());
    private final BusinessRepository businessRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public SaleListingController(
            BusinessRepository businessRepository,
            InventoryItemRepository inventoryItemRepository,
            UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.userRepository = userRepository;
    }

}
