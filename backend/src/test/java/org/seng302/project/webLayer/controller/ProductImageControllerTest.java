package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.Product;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.exceptions.NoBusinessExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.productImages.NoProductImageWithIdException;
import org.seng302.project.serviceLayer.exceptions.productImages.ProductNotFoundException;
import org.seng302.project.serviceLayer.service.ProductImageService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductImageControllerTest extends AbstractInitializer {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductImageService productImageService;

    private User testUser;
    private User testUserBusinessAdmin;
    private User testSystemAdmin;
    private Business testBusiness;
    private Product testProduct;

    @BeforeEach
    public void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testUserBusinessAdmin = this.getTestUserBusinessAdmin();
        testSystemAdmin = this.getTestSystemAdmin();
        testBusiness = this.getTestBusiness();
        testProduct = this.getTestProduct();
    }

    /**
     * Tests successful setting of a product's primary image.
     * Request made by business admin.
     * Expect 200 response and the product's primary image to be updated.
     */
    @Test
    void setPrimaryImage_requestByBusinessAdmin_success() throws Exception {
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                testBusiness.getId(), testProduct.getId(), 2)
                .with(user(new AppUserDetails(testUserBusinessAdmin))))
                .andExpect(status().isOk());
    }

    /**
     * Tests successful setting of a product's primary image.
     * Request made by system admin.
     * Expect 200 response and the product's primary image to be updated.
     */
    @Test
    void setPrimaryImage_requestByGAA_success() throws Exception {
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                testBusiness.getId(), testProduct.getId(), 2)
                .with(user(new AppUserDetails(testSystemAdmin))))
                .andExpect(status().isOk());
    }

    /**
     * Tests that request to update primary image fails
     * if request made by someone that isn't a business admin.
     * Expect 403 response
     */
    @Test
    void setPrimaryImage_notAdmin() throws Exception {
        doThrow(new ForbiddenAdministratorActionException(testBusiness.getId()))
                .when(productImageService).setPrimaryImage(Mockito.any(SetPrimaryProductImageDTO.class));
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                testBusiness.getId(), testProduct.getId(), 2)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that request to update primary image fails
     * for invalid businessId.
     * Expect 406 response
     */
    @Test
    void setPrimaryImage_noBusinessExists() throws Exception {
        doThrow(new NoBusinessExistsException(4))
                .when(productImageService).setPrimaryImage(Mockito.any(SetPrimaryProductImageDTO.class));
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                4, testProduct.getId(), 2)
                .with(user(new AppUserDetails(testUserBusinessAdmin))))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to update primary image fails
     * for invalid productId.
     * Expect 406 response
     */
    @Test
    void setPrimaryImage_noProductExists() throws Exception {
        doThrow(new ProductNotFoundException("NotAProduct", testBusiness.getId()))
                .when(productImageService).setPrimaryImage(Mockito.any(SetPrimaryProductImageDTO.class));
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                testBusiness.getId(), "NotAProduct", 2)
                .with(user(new AppUserDetails(testUserBusinessAdmin))))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to update primary image fails
     * for invalid imageId.
     * Expect 406 response
     */
    @Test
    void setPrimaryImage_noImageExists() throws Exception {
        doThrow(new NoProductImageWithIdException(testProduct.getId(), 7))
                .when(productImageService).setPrimaryImage(Mockito.any(SetPrimaryProductImageDTO.class));
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                testBusiness.getId(), testProduct.getId(), 7)
                .with(user(new AppUserDetails(testUserBusinessAdmin))))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testUploadNewImage_notLoggedIn_returnsStatus401() throws Exception {
        RequestBuilder postProductImageRequest = MockMvcRequestBuilders
                .post("/businesses/1/products/1/images");

        mockMvc.perform(postProductImageRequest)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testUploadNewImage_notAdmin_returnStatus403() {

    }

}
