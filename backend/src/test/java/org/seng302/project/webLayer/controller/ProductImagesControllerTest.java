package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductImagesController.class)
class ProductImagesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ImageRepository imageRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User businessAdmin;
    private User systemAdmin;
    private User otherUser;
    private Business testBusiness;
    private Product testProduct;

    @BeforeEach
    void setup() {
        //Mock a business admin
        businessAdmin = new User("Business", "Admin", "", "",
                "I am a business admin", "admin@business.com", "1999-07-28",
                "+64 123 4567", null, "Th1s1sMyBusiness");

        businessAdmin.setId(1);
        businessAdmin.setPassword(passwordEncoder.encode(businessAdmin.getPassword()));
        given(userRepository.findByEmail("admin@business.com")).willReturn(List.of(businessAdmin));
        given(userRepository.findById(1)).willReturn(Optional.of(businessAdmin));

        //Mock a system admin
        systemAdmin = new User("System", "Admin", "", "",
                "I am a system admin", "admin@resale.com", "1999-07-28",
                "+64 123 4567", null, "Th1s1sMyApplication");

        systemAdmin.setId(2);
        systemAdmin.setRole("globalApplicationAdmin");
        systemAdmin.setPassword(passwordEncoder.encode(systemAdmin.getPassword()));
        given(userRepository.findByEmail("admin@resale.com")).willReturn(List.of(systemAdmin));
        given(userRepository.findById(2)).willReturn(Optional.of(systemAdmin));

        //Mock a different user
        otherUser = new User("Random", "User", "", "",
                "I am a random user", "random@gmail.com", "1999-07-28",
                "+64 123 4567", null, "JustAUser123");

        otherUser.setId(3);
        otherUser.setPassword(passwordEncoder.encode(otherUser.getPassword()));
        given(userRepository.findByEmail("random@gmail.com")).willReturn(List.of(otherUser));
        given(userRepository.findById(3)).willReturn(Optional.of(otherUser));

        //Mock a business
        testBusiness = new Business("Food with Photos", "Images to accompany your food",
                null, "Retail Trade", 1);
        testBusiness.setId(1);
        given(businessRepository.findByName("Food with Photos")).willReturn(List.of(testBusiness));
        given(businessRepository.findById(1)).willReturn(Optional.of(testBusiness));

        //Mock a product with images
        testProduct = spy(new Product("PP1", "Potatoes & Pictures", "Delicious spuds with some shots",
                "Food with Photos", 5.00, 1));
        Image image4 = new Image("image4.jpg", "image4_thumbnail.jpg" );
        image4.setId(4);
        Image image5 = new Image("image5.jpg", "image5_thumbnail.jpg" );
        image5.setId(5);
        Image image6 = new Image("image6.jpg", "image6_thumbnail.jpg" );
        image6.setId(6);
        given(imageRepository.findAll()).willReturn(List.of(image4, image5, image6));
        given(testProduct.getImages()).willReturn(List.of(image4, image5, image6));
        given(productRepository.findByIdAndBusinessId("PP1", 1)).willReturn(Optional.of(testProduct));

    }

    /**
     * Tests successful setting of a product's primary image.
     * Request made by business admin.
     * Expect 200 response and the product's primary image to be updated.
     */
    @Test
    void setPrimaryImage_requestByBusinessAdmin_success() throws Exception {
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                testBusiness.getId(), testProduct.getId(), 5)
                .with(user(new AppUserDetails(businessAdmin))))
                .andExpect(status().isOk());

        ArgumentCaptor<Integer> imageIdArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(testProduct).setPrimaryImageId(imageIdArgumentCaptor.capture());

        Assertions.assertEquals(5, imageIdArgumentCaptor.getValue());
    }

    /**
     * Tests successful setting of a product's primary image.
     * Request made by system admin.
     * Expect 200 response and the product's primary image to be updated.
     */
    @Test
    void setPrimaryImage_requestByGAA_success() throws Exception {
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                testBusiness.getId(), testProduct.getId(), 5)
                .with(user(new AppUserDetails(systemAdmin))))
                .andExpect(status().isOk());

        ArgumentCaptor<Integer> imageIdArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(testProduct).setPrimaryImageId(imageIdArgumentCaptor.capture());

        Assertions.assertEquals(5, imageIdArgumentCaptor.getValue());
    }

    /**
     * Tests that request to update primary image fails
     * if request made by someone that isn't a business admin.
     * Expect 403 response
     */
    @Test
    void setPrimaryImage_notAdmin() throws Exception {
        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                testBusiness.getId(), testProduct.getId(), 5)
                .with(user(new AppUserDetails(otherUser))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that request to update primary image fails
     * for invalid businessId.
     * Expect 406 response
     */
    @Test
    void setPrimaryImage_noBusinessExists() throws Exception {
        given(businessRepository.findById(4)).willReturn(Optional.empty());

        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                4, testProduct.getId(), 5)
                .with(user(new AppUserDetails(businessAdmin))))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to update primary image fails
     * for invalid productId.
     * Expect 406 response
     */
    @Test
    void setPrimaryImage_noProductExists() throws Exception {
        given(productRepository.findByIdAndBusinessId("NotAProduct", 1)).willReturn(Optional.empty());

        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                testBusiness.getId(), "NotAProduct", 5)
                .with(user(new AppUserDetails(businessAdmin))))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to update primary image fails
     * for invalid imageId.
     * Expect 406 response
     */
    @Test
    void setPrimaryImage_noImageExists() throws Exception {
        given(productRepository.save(any(Product.class))).willReturn(testProduct);

        mockMvc.perform(put("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary",
                testBusiness.getId(), testProduct.getId(), 7)
                .with(user(new AppUserDetails(businessAdmin))))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests successful deleting of a product's image.
     * Request made by business admin.
     * Expect 200 response and the product's image to be deleted.
     */
    @Test
    void deleteImage_requestByBusinessAdmin_success() throws Exception {
        mockMvc.perform(delete("/businesses/{businessId}/products/{productId}/images/{imageId}",
                testBusiness.getId(), testProduct.getId(), 5)
                .with(user(new AppUserDetails(businessAdmin))))
                .andExpect(status().isOk());
    }

    /**
     * Tests successful deleting of a product's image.
     * Request made by GAA.
     * Expect 200 response and the product's image to be deleted.
     */
    @Test
    void deleteImage_requestByGAA_success() throws Exception {
        mockMvc.perform(delete("/businesses/{businessId}/products/{productId}/images/{imageId}",
                testBusiness.getId(), testProduct.getId(), 5)
                .with(user(new AppUserDetails(systemAdmin))))
                .andExpect(status().isOk());
    }

    /**
     * Tests that request to delete a product image fails
     * if request made by someone that isn't a business admin.
     * Expect 403 response
     */
    @Test
    void deleteImage_notAdmin() throws Exception {
        mockMvc.perform(delete("/businesses/{businessId}/products/{productId}/images/{imageId}",
                testBusiness.getId(), testProduct.getId(), 5)
                .with(user(new AppUserDetails(otherUser))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that request to delete a product image fails
     * for invalid businessId.
     * Expect 406 response
     */
    @Test
    void deleteImage_noBusinessExists() throws Exception {
        given(businessRepository.findById(4)).willReturn(Optional.empty());

        mockMvc.perform(delete("/businesses/{businessId}/products/{productId}/images/{imageId}",
                4, testProduct.getId(), 5)
                .with(user(new AppUserDetails(businessAdmin))))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to delete a product image fails
     * for invalid productId.
     * Expect 406 response
     */
    @Test
    void deleteImage_noProductExists() throws Exception {
        given(productRepository.findByIdAndBusinessId("NotAProduct", 1)).willReturn(Optional.empty());

        mockMvc.perform(delete("/businesses/{businessId}/products/{productId}/images/{imageId}",
                testBusiness.getId(), 1, 5)
                .with(user(new AppUserDetails(businessAdmin))))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to delete a product image fails
     * for invalid imageId.
     * Expect 406 response
     */
    @Test
    void deleteImage_noImageExists() throws Exception {
        given(productRepository.save(any(Product.class))).willReturn(testProduct);

        mockMvc.perform(delete("/businesses/{businessId}/products/{productId}/images/{imageId}",
                testBusiness.getId(), testProduct.getId(), 7)
                .with(user(new AppUserDetails(businessAdmin))))
                .andExpect(status().isNotAcceptable());
    }
}
