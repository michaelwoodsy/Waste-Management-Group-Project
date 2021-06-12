package org.seng302.project.webLayer.controller;

import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.serviceLayer.dto.AddProductDTO;
import org.seng302.project.serviceLayer.dto.EditProductDTO;
import org.seng302.project.serviceLayer.service.ProductCatalogueService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * Rest controller for products.
 */
@RestController
public class ProductCatalogueController {

    private final ProductCatalogueService productCatalogueService;

    @Autowired
    public ProductCatalogueController(ProductCatalogueService productCatalogueService) {
        this.productCatalogueService = productCatalogueService;
    }



    /**
     * Gets a list of products that belongs to a business.
     *
     * @param businessId ID of the business to get the products of.
     * @param appUser    AppUserDetails from spring security
     * @return List of products that belongs to the business.
     */
    @GetMapping("/businesses/{businessId}/products")
    public List<Product> getBusinessesProducts(@PathVariable int businessId, @AuthenticationPrincipal AppUserDetails appUser) {
        return productCatalogueService.getBusinessesProducts(businessId, appUser);
    }


    /**
     * @param businessId ID of the business to add product to.
     * @param appUser    AppUserDetails of current user
     * @param requestDTO       The fields of the new product
     */
    @PostMapping("/businesses/{businessId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public void newProduct(@PathVariable int businessId, @Valid @RequestBody AddProductDTO requestDTO,
                           @AuthenticationPrincipal AppUserDetails appUser) {
        requestDTO.setBusinessId(businessId);
        requestDTO.setAppUser(appUser);

        productCatalogueService.newProduct(requestDTO);
    }


    /**
     * Edits product with id productId
     * @param businessId ID of the business the product is under.
     * @param productId ID of the product
     * @param appUser AppUserDetails of current user
     * @param requestDTO The fields of the product to edit
     */
    @PutMapping("/businesses/{businessId}/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void editProduct(@PathVariable int businessId, @PathVariable String productId,
                            @Valid @RequestBody EditProductDTO requestDTO, @AuthenticationPrincipal AppUserDetails appUser) {
        requestDTO.setBusinessId(businessId);
        requestDTO.setProductId(productId);
        requestDTO.setAppUser(appUser);
        productCatalogueService.editProduct(requestDTO);
    }

}
