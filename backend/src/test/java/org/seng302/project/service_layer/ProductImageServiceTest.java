package org.seng302.project.service_layer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Image;
import org.seng302.project.repository_layer.model.Product;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.ProductRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.SetPrimaryProductImageDTO;
import org.seng302.project.service_layer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.service_layer.exceptions.product.ProductImageNotFoundException;
import org.seng302.project.service_layer.exceptions.product.ProductNotFoundException;
import org.seng302.project.service_layer.service.ProductImageService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@SpringBootTest
public class ProductImageServiceTest extends AbstractInitializer {

    @Autowired
    private ProductImageService productImageService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BusinessRepository businessRepository;
    @MockBean
    private ProductRepository productRepository;

    private User testUser;
    private User testSystemAdmin;
    private User testUserBusinessAdmin;
    private Business testBusiness;
    private Product testProduct;

    @BeforeEach
    public void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testSystemAdmin = this.getTestSystemAdmin();
        testUserBusinessAdmin = this.getTestUserBusinessAdmin();
        testBusiness = this.getTestBusiness();
        testProduct = Mockito.spy(this.getTestProduct());
        given(userRepository.findByEmail("john.smith@gmail.com")).willReturn(List.of(testUser));
        given(userRepository.findById(1)).willReturn(Optional.of(testUser));
        given(userRepository.findByEmail("admin@resale.com")).willReturn(List.of(testSystemAdmin));
        given(userRepository.findById(2)).willReturn(Optional.of(testSystemAdmin));
        given(userRepository.findByEmail("jane.doe@gmail.com")).willReturn(List.of(testUserBusinessAdmin));
        given(userRepository.findById(3)).willReturn(Optional.of(testUserBusinessAdmin));
        given(businessRepository.findByName("Test Business")).willReturn(List.of(testBusiness));
        given(businessRepository.findById(1)).willReturn(Optional.of(testBusiness));
        Image image1 = new Image(1);
        Image image2 = new Image(2);
        Image image3 = new Image(3);
        given(testProduct.getImages()).willReturn(List.of(image1, image2, image3));
        given(productRepository.findByIdAndBusinessId("TEST-PROD", 1)).willReturn(Optional.of(testProduct));
    }

    /**
     * Tests that setting a primary image as a business admin results in a success.
     */
    @Test
    public void setPrimaryImage_withBusinessAdmin_success() {
        SetPrimaryProductImageDTO dto = new SetPrimaryProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                2,
                new AppUserDetails(testUserBusinessAdmin)
        );
        productImageService.setPrimaryImage(dto);
        ArgumentCaptor<Integer> imageIdArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(testProduct).setPrimaryImageId(imageIdArgumentCaptor.capture());

        Assertions.assertEquals(2, imageIdArgumentCaptor.getValue());
    }

    /**
     * Tests that setting a primary image as a system admin results in a success.
     */
    @Test
    public void setPrimaryImage_withSystemAdmin_success() {
        SetPrimaryProductImageDTO dto = new SetPrimaryProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                2,
                new AppUserDetails(testSystemAdmin)
        );
        productImageService.setPrimaryImage(dto);
        ArgumentCaptor<Integer> imageIdArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(testProduct).setPrimaryImageId(imageIdArgumentCaptor.capture());

        Assertions.assertEquals(2, imageIdArgumentCaptor.getValue());
    }

    /**
     * Tests that setting a primary image as a user who is not an admin results in an error.
     */
    @Test
    public void setPrimaryImage_notAdmin_throwsException() {
        SetPrimaryProductImageDTO dto = new SetPrimaryProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                2,
                new AppUserDetails(testUser)
        );
        Assertions.assertThrows(ForbiddenAdministratorActionException.class,
                () -> productImageService.setPrimaryImage(dto));
    }

    /**
     * Tests that the correct exception is thrown if a business does not exist.
     */
    @Test
    public void setPrimaryImage_noBusinessExists() {
        given(businessRepository.findById(4)).willReturn(Optional.empty());
        SetPrimaryProductImageDTO dto = new SetPrimaryProductImageDTO(
                4,
                testProduct.getId(),
                2,
                new AppUserDetails(testUserBusinessAdmin)
        );
        Assertions.assertThrows(BusinessNotFoundException.class,
                () -> productImageService.setPrimaryImage(dto));
    }

    /**
     * Tests that the correct exception is thrown if a product does not exist.
     */
    @Test
    public void setPrimaryImage_noProductExists() {
        given(productRepository.findByIdAndBusinessId("NotAProduct", 1)).willReturn(Optional.empty());
        SetPrimaryProductImageDTO dto = new SetPrimaryProductImageDTO(
                testBusiness.getId(),
                "NotAProduct",
                2,
                new AppUserDetails(testUserBusinessAdmin)
        );
        Assertions.assertThrows(ProductNotFoundException.class,
                () -> productImageService.setPrimaryImage(dto));
    }

    /**
     * Tests that the correct exception is thrown if a particular image does not exist.
     */
    @Test
    public void setPrimaryImage_noImageExists() {
        SetPrimaryProductImageDTO dto = new SetPrimaryProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                7,
                new AppUserDetails(testUserBusinessAdmin)
        );
        Assertions.assertThrows(ProductImageNotFoundException.class,
                () -> productImageService.setPrimaryImage(dto));
    }

}
