package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.model.Product;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.AddProductImageDTO;
import org.seng302.project.serviceLayer.dto.AddProductImageResponseDTO;
import org.seng302.project.serviceLayer.dto.product.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.product.ProductImageNotFoundException;
import org.seng302.project.serviceLayer.exceptions.product.ProductNotFoundException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@SpringBootTest
class ProductImageServiceTest extends AbstractInitializer {

    @Autowired
    private ProductImageService productImageService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BusinessRepository businessRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private ImageRepository imageRepository;

    private User testUser;
    private User testSystemAdmin;
    private User testUserBusinessAdmin;
    private Business testBusiness;
    private Product testProduct;
    private MockMultipartFile testImageFile;

    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testSystemAdmin = this.getTestSystemAdmin();
        testUserBusinessAdmin = this.getTestUserBusinessAdmin();
        testBusiness = this.getTestBusiness();
        testProduct = Mockito.spy(this.getTestProduct());
        testProduct.setImages(this.getTestImages());
        testImageFile = this.getTestImageFile();
        this.mocks();
    }

    /**
     * Sets up mocks used by multiple tests.
     */
    void mocks() {
        given(userRepository.findByEmail("john.smith@gmail.com")).willReturn(List.of(testUser));
        given(userRepository.findById(1)).willReturn(Optional.of(testUser));
        given(userRepository.findByEmail("admin@resale.com")).willReturn(List.of(testSystemAdmin));
        given(userRepository.findById(2)).willReturn(Optional.of(testSystemAdmin));
        given(userRepository.findByEmail("jane.doe@gmail.com")).willReturn(List.of(testUserBusinessAdmin));
        given(userRepository.findById(3)).willReturn(Optional.of(testUserBusinessAdmin));
        given(businessRepository.findByName("Test Business")).willReturn(List.of(testBusiness));
        given(businessRepository.findById(1)).willReturn(Optional.of(testBusiness));
        given(productRepository.findByIdAndBusinessId("TEST-PROD", 1)).willReturn(Optional.of(testProduct));
    }

    /**
     * Tests that an image is successfully created and added to the product.
     */
    @Test
    void addProductImage_withBusinessAdmin_success() {
        Mockito.when(imageRepository.save(Mockito.any(Image.class)))
                .thenAnswer(invocation -> null);

        AddProductImageDTO dto = new AddProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                new AppUserDetails(testUserBusinessAdmin),
                testImageFile
        );
        AddProductImageResponseDTO responseDTO = productImageService.addProductImage(dto);
        System.out.println(testProduct.getImages());
        Assertions.assertEquals(4, testProduct.getImages().size());
        String imageFileName = testProduct.getImages().get(3).getFilename();
        File imageFile = new File("src/main/resources/public/media/" + imageFileName);
        Assertions.assertTrue(imageFile.delete());
    }

    /**
     * Tests that an image is successfully created and added to the product.
     */
    @Test
    void addProductImage_withSystemAdmin_success() {
        Mockito.when(imageRepository.save(Mockito.any(Image.class)))
                .thenAnswer(invocation -> null);

        AddProductImageDTO dto = new AddProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                new AppUserDetails(testSystemAdmin),
                testImageFile
        );
        AddProductImageResponseDTO responseDTO = productImageService.addProductImage(dto);
        Assertions.assertEquals(4, testProduct.getImages().size());
        String imageFileName = testProduct.getImages().get(3).getFilename();
        File imageFile = new File("src/main/resources/public/media/" + imageFileName);
        Assertions.assertTrue(imageFile.delete());
    }

    /**
     * Tests that the correct exception is thrown if a user is not an admin and tries to add a new image.
     */
    @Test
    void addProductImage_withNotAdmin_throwsException() {
        AddProductImageDTO dto = new AddProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                new AppUserDetails(testUser),
                testImageFile
        );
        Assertions.assertThrows(ForbiddenAdministratorActionException.class,
                () -> productImageService.addProductImage(dto));
    }

    /**
     * Tests that setting a primary image as a business admin results in a success.
     */
    @Test
    void setPrimaryImage_withBusinessAdmin_success() {
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
    void setPrimaryImage_withSystemAdmin_success() {
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
    void setPrimaryImage_notAdmin_throwsException() {
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
    void setPrimaryImage_noBusinessExists() {
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
    void setPrimaryImage_noProductExists() {
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
    void setPrimaryImage_noImageExists() {
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
