package org.seng302.project.controller;

import org.seng302.project.model.BusinessRepository;
import org.seng302.project.model.Product;
import org.seng302.project.model.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller for products.
 */
@RestController
public class ProductCatalogueController {

    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogueController.class.getName());
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ProductCatalogueController(BusinessRepository businessRepository, ProductRepository productRepository) {
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
    }

    /**
     * Gets a list of products that belongs to a business.
     * @param id ID of the business to get the products of.
     * @return List of products that belongs to the business.
     */
    @GetMapping("/businesses/{id}/products")
    public List<Product> getBusinessesProducts(@PathVariable int id) { return List.of(); }

}
