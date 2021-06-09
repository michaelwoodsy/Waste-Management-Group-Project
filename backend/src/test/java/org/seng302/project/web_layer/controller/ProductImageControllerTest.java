package org.seng302.project.web_layer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Product;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.service_layer.dto.AddProductImageDTO;
import org.seng302.project.service_layer.dto.AddProductImageResponseDTO;
import org.seng302.project.service_layer.dto.SetPrimaryProductImageDTO;
import org.seng302.project.service_layer.exceptions.NoBusinessExistsException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.service_layer.exceptions.productImages.NoProductImageWithIdException;
import org.seng302.project.service_layer.exceptions.productImages.ProductNotFoundException;
import org.seng302.project.service_layer.service.ProductImageService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProductImageControllerTest extends AbstractInitializer {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductImageService productImageService;

    private User testUser;
    private User testUserBusinessAdmin;
    private User testSystemAdmin;
    private Business testBusiness;
    private Product testProduct;
    private MockMultipartFile testFile;

    @BeforeEach
    public void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testUserBusinessAdmin = this.getTestUserBusinessAdmin();
        testSystemAdmin = this.getTestSystemAdmin();
        testBusiness = this.getTestBusiness();
        testProduct = this.getTestProduct();
        testFile = this.getTestFile();
    }

    /**
     * Tests that trying to add a new product image when not logged in returns a status 401
     */
    @Test
    void addProductImage_notLoggedIn_returnsStatus401() throws Exception {
        RequestBuilder postProductImageRequest = MockMvcRequestBuilders
                .multipart("/businesses/{businessId}/products/{productId}/images",
                        testBusiness.getId(),
                        testProduct.getId())
                .file(testFile);

        mockMvc.perform(postProductImageRequest).andExpect(status().isUnauthorized());
    }

    /**
     * Tests that a status code 403 is returned when a user is not a business admin or system admin.
     */
    @Test
    void addProductImage_notAdmin_returnStatus403() throws Exception {
        Mockito.when(productImageService.addProductImage(Mockito.any(AddProductImageDTO.class)))
                .thenThrow(new ForbiddenAdministratorActionException(testBusiness.getId()));

        RequestBuilder request = MockMvcRequestBuilders
                .multipart("/businesses/{businessId}/products/{productId}/images",
                        testBusiness.getId(),
                        testProduct.getId())
                .file(testFile)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    /**
     * Tests that adding a new image as a business admin results in a success.
     */
    @Test
    void addProductImage_asAdmin_success() throws Exception {
        Mockito.when(productImageService.addProductImage(Mockito.any(AddProductImageDTO.class)))
                .thenReturn(new AddProductImageResponseDTO(1));

        RequestBuilder request = MockMvcRequestBuilders
                .multipart("/businesses/{businessId}/products/{productId}/images",
                        testBusiness.getId(),
                        testProduct.getId())
                .file(testFile)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Tests that adding a new image as a system admin results in a success.
     */
    @Test
    void addProductImage_asSystemAdmin_success() throws Exception {
        Mockito.when(productImageService.addProductImage(Mockito.any(AddProductImageDTO.class)))
                .thenReturn(new AddProductImageResponseDTO(1));

        RequestBuilder request = MockMvcRequestBuilders
                .multipart("/businesses/{businessId}/products/{productId}/images",
                        testBusiness.getId(),
                        testProduct.getId())
                .file(testFile)
                .with(user(new AppUserDetails(testSystemAdmin)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Tests that a 406 status is returned when a business does not exists.
     */
    @Test
    void addProductImage_noBusinessExists() throws Exception {
        Mockito.when(productImageService.addProductImage(Mockito.any(AddProductImageDTO.class)))
                .thenThrow(new NoBusinessExistsException(4));

        RequestBuilder request = MockMvcRequestBuilders
                .multipart("/businesses/{businessId}/products/{productId}/images",
                        4,
                        testProduct.getId(),
                        2)
                .file(testFile)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests successful setting of a product's primary image.
     * Request made by business admin.
     * Expect 200 response and the product's primary image to be updated.
     */
    @Test
    void setPrimaryImage_requestByBusinessAdmin_success() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                        testBusiness.getId(),
                        testProduct.getId(),
                        2)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Tests successful setting of a product's primary image.
     * Request made by system admin.
     * Expect 200 response and the product's primary image to be updated.
     */
    @Test
    void setPrimaryImage_requestByGAA_success() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                        testBusiness.getId(),
                        testProduct.getId(),
                        2)
                .with(user(new AppUserDetails(testSystemAdmin)));

        mockMvc.perform(request).andExpect(status().isOk());
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

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                        testBusiness.getId(),
                        testProduct.getId(),
                        2)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isForbidden());
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

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                        4,
                        testProduct.getId(),
                        2)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
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

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                        testBusiness.getId(),
                        "NotAProduct",
                        2)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
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

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                        testBusiness.getId(),
                        testProduct.getId(),
                        7)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }
}
