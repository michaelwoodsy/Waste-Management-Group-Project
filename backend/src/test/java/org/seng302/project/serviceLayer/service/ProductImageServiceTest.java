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
import org.seng302.project.serviceLayer.dto.product.AddProductImageDTO;
import org.seng302.project.serviceLayer.dto.product.DeleteProductImageDTO;
import org.seng302.project.serviceLayer.dto.product.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.product.ProductImageNotFoundException;
import org.seng302.project.serviceLayer.exceptions.product.ProductNotFoundException;
import org.seng302.project.serviceLayer.util.ImageUtil;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

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
    @MockBean
    private ImageUtil imageUtil;

    private User testUser;
    private User testSystemAdmin;
    private User testUserBusinessAdmin;
    private Business testBusiness;
    private Product testProduct;
    private List<Image> testImages;
    private MockMultipartFile testImageFile;

    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testSystemAdmin = this.getTestSystemAdmin();
        testUserBusinessAdmin = this.getTestUserBusinessAdmin();
        testBusiness = this.getTestBusiness();
        testImages = this.getTestImages();
        testProduct = Mockito.spy(this.getTestProduct());
        testProduct.setImages(new ArrayList<>(testImages));
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
    void addProductImage_withBusinessAdmin_success() throws IOException {
        Mockito.when(imageRepository.save(Mockito.any(Image.class)))
                .thenAnswer(invocation -> {
                    Image image = invocation.getArgument(0);
                    image.setId(4);
                    testImages.add(image);
                    return image;
                });

        AddProductImageDTO dto = new AddProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                new AppUserDetails(testUserBusinessAdmin),
                testImageFile
        );
        productImageService.addProductImage(dto);
        Assertions.assertEquals(4, testProduct.getImages().size());
        ArgumentCaptor<String> imagePathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BufferedImage> imageArgumentCaptor = ArgumentCaptor.forClass(BufferedImage.class);
        Mockito.verify(imageUtil).saveImage(imageArgumentCaptor.capture(), imagePathCaptor.capture());
    }

    /**
     * Tests that an image is successfully created and added to the product.
     */
    @Test
    void addProductImage_withSystemAdmin_success() throws IOException {
        Mockito.when(imageRepository.save(Mockito.any(Image.class)))
                .thenAnswer(invocation -> {
                    Image image = invocation.getArgument(0);
                    image.setId(4);
                    testImages.add(image);
                    return image;
                });

        AddProductImageDTO dto = new AddProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                new AppUserDetails(testSystemAdmin),
                testImageFile
        );
        productImageService.addProductImage(dto);
        Assertions.assertEquals(4, testProduct.getImages().size());
        ArgumentCaptor<String> imagePathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BufferedImage> imageArgumentCaptor = ArgumentCaptor.forClass(BufferedImage.class);
        Mockito.verify(imageUtil).saveImage(imageArgumentCaptor.capture(), imagePathCaptor.capture());
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

    /**
     * Tests that deleting a product image as a user who is not an admin results in an error.
     */
    @Test
    void deleteImage_notAdmin_throwsException() {
        DeleteProductImageDTO deleteProductImageDTO = new DeleteProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                2,
                new AppUserDetails(testUser)
        );
        Assertions.assertThrows(ForbiddenAdministratorActionException.class,
                () -> productImageService.deleteImage(deleteProductImageDTO));
    }

    /**
     * Tests that deleting a product image for a business that does not exist results in an error.
     */
    @Test
    void deleteImage_noBusinessExists_throwsException() {
        given(businessRepository.findById(4)).willReturn(Optional.empty());
        DeleteProductImageDTO deleteProductImageDTO = new DeleteProductImageDTO(
                4,
                testProduct.getId(),
                2,
                new AppUserDetails(testUserBusinessAdmin)
        );
        Assertions.assertThrows(BusinessNotFoundException.class,
                () -> productImageService.deleteImage(deleteProductImageDTO));
    }

    /**
     * Tests that deleting a product image for a product that does not exist results in an error.
     */
    @Test
    void deleteImage_noProductExists_throwsException() {
        given(productRepository.findByIdAndBusinessId("NotAProduct", 1)).willReturn(Optional.empty());
        DeleteProductImageDTO deleteProductImageDTO = new DeleteProductImageDTO(
                testBusiness.getId(),
                "NotAProduct",
                2,
                new AppUserDetails(testUserBusinessAdmin)
        );
        Assertions.assertThrows(ProductNotFoundException.class,
                () -> productImageService.deleteImage(deleteProductImageDTO));
    }

    /**
     * Tests that deleting a product image for an image that does not exist results in an error.
     */
    @Test
    void deleteImage_noImageExists_throwsException() {
        DeleteProductImageDTO deleteProductImageDTO = new DeleteProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                7,
                new AppUserDetails(testUserBusinessAdmin)
        );
        Assertions.assertThrows(ProductImageNotFoundException.class,
                () -> productImageService.deleteImage(deleteProductImageDTO));
    }


    /**
     * Tests the success case for deleting a product image.
     * Expects the imageUtil.deleteImage() to be called twice,
     * once for the image file, once for the thumbnail file.
     *
     * @throws IOException exception thrown by imageUtil.deleteImage()
     */
    @Test
    void deleteImage_withBusinessAdmin_success() throws IOException {
        DeleteProductImageDTO deleteProductImageDTO = new DeleteProductImageDTO(
                testBusiness.getId(),
                testProduct.getId(),
                2,
                new AppUserDetails(testUserBusinessAdmin)
        );

        productImageService.deleteImage(deleteProductImageDTO);

        ArgumentCaptor<String> imagePathCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(imageUtil, times(2)).deleteImage(imagePathCaptor.capture());

        Assertions.assertEquals(2, testProduct.getImages().size());
    }

}
