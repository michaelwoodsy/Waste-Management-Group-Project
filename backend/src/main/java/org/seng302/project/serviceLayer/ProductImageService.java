package org.seng302.project.serviceLayer;

import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.serviceLayer.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.UUID;

@Component
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ImageUtil imageUtil;

    @Autowired
    public ProductImageService(ProductRepository productRepository,
                               ImageUtil imageUtil) {
        this.productRepository = productRepository;
        this.imageUtil = imageUtil;
    }

    public void addProductImage(Integer userId, Integer businessId, String productId, MultipartFile imageFile) {
        String imageFileName = UUID.randomUUID().toString();
    }

}
