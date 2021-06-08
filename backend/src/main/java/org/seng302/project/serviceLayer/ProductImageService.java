package org.seng302.project.serviceLayer;

import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductImageService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductImageService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProductImage() {

    }

}
