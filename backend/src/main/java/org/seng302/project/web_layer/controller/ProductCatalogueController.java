package org.seng302.project.web_layer.controller;

import org.seng302.project.service_layer.dto.product.AddProductDTO;
import org.seng302.project.service_layer.dto.product.EditProductDTO;
import org.seng302.project.service_layer.dto.product.GetProductDTO;
import org.seng302.project.service_layer.dto.product.ProductSearchDTO;
import org.seng302.project.service_layer.service.ProductCatalogueService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
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
    public List<GetProductDTO> getBusinessesProducts(@PathVariable int businessId, @AuthenticationPrincipal AppUserDetails appUser) {
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

    /**
     * Searches a business's catalogue for products
     * @param businessId id of the business to search products of
     * @param matchingId Whether the Id field is being searched by
     * @param matchingName Whether the Name field is being searched by
     * @param matchingDescription Whether the Description field is being searched by
     * @param matchingManufacturer Whether the Manufacturer field is being searched by
     * @param appUser the user making the request
     */
    @GetMapping("/businesses/{businessId}/products/search")
    @ResponseStatus(HttpStatus.OK)
    public List<GetProductDTO> searchProducts(@PathVariable Integer businessId,
                                              @RequestParam("searchQuery") String searchQuery,
                                              @RequestParam(name="matchingId", required = false) Boolean matchingId,
                                              @RequestParam(name="matchingName", required = false) Boolean matchingName,
                                              @RequestParam(name="matchingDescription", required = false) Boolean matchingDescription,
                                              @RequestParam(name="matchingManufacturer", required = false) Boolean matchingManufacturer,
                                              @AuthenticationPrincipal AppUserDetails appUser) {

        var requestDTO = new ProductSearchDTO(matchingId, matchingName, matchingDescription, matchingManufacturer);
        return productCatalogueService.searchProducts(businessId, searchQuery, requestDTO, appUser);
    }

}
