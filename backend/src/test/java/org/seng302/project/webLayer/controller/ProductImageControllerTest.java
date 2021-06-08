package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.model.Product;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
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
    private UserRepository userRepository;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductImageService productImageService;

    private Product testProduct;

    @BeforeEach
    public void setup() {
        this.initialise();
        given(userRepository.findByEmail("john.smith@gmail.com")).willReturn(List.of(this.getTestUser()));
        given(userRepository.findById(1)).willReturn(Optional.of(this.getTestUser()));
        given(userRepository.findByEmail("admin@resale.com")).willReturn(List.of(this.getTestSystemAdmin()));
        given(userRepository.findById(2)).willReturn(Optional.of(this.getTestSystemAdmin()));
        given(userRepository.findByEmail("jane.doe@gmail.com")).willReturn(List.of(this.getTestUserBusinessAdmin()));
        given(userRepository.findById(3)).willReturn(Optional.of(this.getTestUserBusinessAdmin()));
        given(businessRepository.findByName("Test Business")).willReturn(List.of(this.getTestBusiness()));
        given(businessRepository.findById(1)).willReturn(Optional.of(this.getTestBusiness()));
        testProduct = Mockito.spy(this.getTestProduct());
        Image image1 = new Image(1);
        Image image2 = new Image(2);
        Image image3 = new Image(3);
        given(testProduct.getImages()).willReturn(List.of(image1, image2, image3));
        given(productRepository.findByIdAndBusinessId("TEST-PROD", 1)).willReturn(Optional.of(testProduct));
    }

    /**
     * Tests successful setting of a product's primary image.
     * Request made by business admin.
     * Expect 200 response and the product's primary image to be updated.
     */
    @Test
    void setPrimaryImage_requestByBusinessAdmin_success() throws Exception {
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                this.getTestBusiness().getId(), testProduct.getId(), 2)
                .with(user(new AppUserDetails(this.getTestUserBusinessAdmin()))))
                .andExpect(status().isOk());

        //ArgumentCaptor<Integer> imageIdArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        //verify(testProduct).setPrimaryImageId(imageIdArgumentCaptor.capture());

        //Assertions.assertEquals(2, imageIdArgumentCaptor.getValue());
    }

    /**
     * Tests successful setting of a product's primary image.
     * Request made by system admin.
     * Expect 200 response and the product's primary image to be updated.
     */
    @Test
    void setPrimaryImage_requestByGAA_success() throws Exception {
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                this.getTestBusiness().getId(), testProduct.getId(), 2)
                .with(user(new AppUserDetails(this.getTestSystemAdmin()))))
                .andExpect(status().isOk());

        //ArgumentCaptor<Integer> imageIdArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        //verify(testProduct).setPrimaryImageId(imageIdArgumentCaptor.capture());

        //Assertions.assertEquals(2, imageIdArgumentCaptor.getValue());
    }

    /**
     * Tests that request to update primary image fails
     * if request made by someone that isn't a business admin.
     * Expect 403 response
     */
    @Test
    void setPrimaryImage_notAdmin() throws Exception {
        doThrow(new ForbiddenAdministratorActionException(this.getTestBusiness().getId()))
                .when(productImageService).setPrimaryImage(Mockito.any(SetPrimaryProductImageDTO.class));
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                this.getTestBusiness().getId(), testProduct.getId(), 2)
                .with(user(new AppUserDetails(this.getTestUser()))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that request to update primary image fails
     * for invalid businessId.
     * Expect 406 response
     */
    @Test
    void setPrimaryImage_noBusinessExists() throws Exception {
        //given(businessRepository.findById(4)).willReturn(Optional.empty());
        doThrow(new NoBusinessExistsException(4))
                .when(productImageService).setPrimaryImage(Mockito.any(SetPrimaryProductImageDTO.class));
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                4, testProduct.getId(), 2)
                .with(user(new AppUserDetails(this.getTestUserBusinessAdmin()))))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to update primary image fails
     * for invalid productId.
     * Expect 406 response
     */
    @Test
    void setPrimaryImage_noProductExists() throws Exception {
        //given(productRepository.findByIdAndBusinessId("NotAProduct", 1)).willReturn(Optional.empty());
        doThrow(new ProductNotFoundException("NotAProduct", this.getTestBusiness().getId()))
                .when(productImageService).setPrimaryImage(Mockito.any(SetPrimaryProductImageDTO.class));
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                this.getTestBusiness().getId(), "NotAProduct", 2)
                .with(user(new AppUserDetails(this.getTestUserBusinessAdmin()))))
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
                this.getTestBusiness().getId(), testProduct.getId(), 7)
                .with(user(new AppUserDetails(this.getTestUserBusinessAdmin()))))
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
