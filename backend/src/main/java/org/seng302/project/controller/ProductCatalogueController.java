package org.seng302.project.controller;

import org.seng302.project.model.BusinessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for products.
 */
@RestController
public class ProductCatalogueController {

    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogueController.class.getName());
    private final BusinessRepository businessRepository;

    @Autowired
    public ProductCatalogueController(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

}
