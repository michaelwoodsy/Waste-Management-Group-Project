package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Image;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.ImageRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.business.AddBusinessImageDTO;
import org.seng302.project.service_layer.dto.business.DeleteBusinessImageDTO;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.service_layer.exceptions.business.NoBusinessExistsException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.service_layer.util.ImageUtil;
import org.seng302.project.service_layer.util.SpringEnvironment;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

@SpringBootTest
class BusinessImageServiceTest extends AbstractInitializer {

    @Autowired
    private BusinessImageService businessImageService;
    @Autowired
    private SpringEnvironment springEnvironment;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BusinessRepository businessRepository;
    @MockBean
    private ImageRepository imageRepository;
    @MockBean
    private ImageUtil imageUtil;

    private User testUser;
    private User testSystemAdmin;
    private User testUserBusinessAdmin;
    private Business testBusiness;
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
        testBusiness.setImages(new ArrayList<>(testImages));
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
    }

    /**
     * Tests that the correct exception is thrown if a user tries to add an image for a business
     * that they don't administer and the user is not a GAA.
     */
    @Test
    void addBusinessImage_withNotAdmin_throwsException() {
        AddBusinessImageDTO dto = new AddBusinessImageDTO (
                testBusiness.getId(),
                new AppUserDetails(testUser),
                testImageFile
        );

        Assertions.assertThrows(ForbiddenAdministratorActionException.class,
                () -> businessImageService.addBusinessImage(dto));
    }

    /**
     * Tests that the correct exception is thrown if an image is attempted to be added
     * for a business that does not exist.
     */
    @Test
    void addBusinessImage_noBusinessExists_throwsException() {
        AddBusinessImageDTO dto = new AddBusinessImageDTO (
                100,
                new AppUserDetails(testUserBusinessAdmin),
                testImageFile
        );

        Assertions.assertThrows(NoBusinessExistsException.class,
                () -> businessImageService.addBusinessImage(dto));
    }

    /**
     * Tests that an image is successfully created and added to a business.
     */
    @Test
    void addBusinessImage_withBusinessAdmin_success() throws IOException {
        Mockito.when(imageRepository.save(Mockito.any(Image.class)))
                .thenAnswer(invocation -> {
                    Image image = invocation.getArgument(0);
                    image.setId(4);
                    testImages.add(image);
                    return image;
                });

        AddBusinessImageDTO dto = new AddBusinessImageDTO (
                testBusiness.getId(),
                new AppUserDetails(testUserBusinessAdmin),
                testImageFile
        );
        businessImageService.addBusinessImage(dto);
        Assertions.assertEquals(4, testBusiness.getImages().size());
        ArgumentCaptor<String> imagePathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BufferedImage> imageArgumentCaptor = ArgumentCaptor.forClass(BufferedImage.class);
        Mockito.verify(imageUtil).saveImage(imageArgumentCaptor.capture(), imagePathCaptor.capture());
    }

    /**
     * Tests that an image is successfully created and added to a business.
     */
    @Test
    void addBusinessImage_withSystemAdmin_success() throws IOException {
        Mockito.when(imageRepository.save(Mockito.any(Image.class)))
                .thenAnswer(invocation -> {
                    Image image = invocation.getArgument(0);
                    image.setId(4);
                    testImages.add(image);
                    return image;
                });

        AddBusinessImageDTO dto = new AddBusinessImageDTO (
                testBusiness.getId(),
                new AppUserDetails(testSystemAdmin),
                testImageFile
        );
        businessImageService.addBusinessImage(dto);
        Assertions.assertEquals(4, testBusiness.getImages().size());
        ArgumentCaptor<String> imagePathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BufferedImage> imageArgumentCaptor = ArgumentCaptor.forClass(BufferedImage.class);
        Mockito.verify(imageUtil).saveImage(imageArgumentCaptor.capture(), imagePathCaptor.capture());
    }

    /**
     * Tests changing the primary image of a business when you are not a business admin or a GAA
     */
    @Test
    void makePrimaryImage_withNotAdmin_throwsException() {
        var imageId = testImages.get(1).getId();

        Assertions.assertThrows(ForbiddenException.class,
                () -> businessImageService.setPrimaryImage(testBusiness.getId(), imageId , new AppUserDetails(testUser)));
    }

    /**
     * Tests changing the primary image of a business, and the business is not found
     */
    @Test
    void makePrimaryImage_noBusinessFound_throwsException() {
        given(businessRepository.findById(1000)).willReturn(Optional.empty());
        var imageId = testImages.get(1).getId();

        Assertions.assertThrows(NotAcceptableException.class,
                () -> businessImageService.setPrimaryImage(1000, imageId , new AppUserDetails(testUserBusinessAdmin)));
    }

    /**
     * Tests changing the primary image of a business, and the image is not found
     */
    @Test
    void makePrimaryImage_noImageFound_throwsException() {
        var imageId = testImages.get(1).getId();

        Assertions.assertThrows(NotAcceptableException.class,
                () -> businessImageService.setPrimaryImage(testBusiness.getId(), 1000 , new AppUserDetails(testUserBusinessAdmin)));
    }

    /**
     * Tests successfully changing the primary image of a business as a business administrator
     */
    @Test
    void makePrimaryImage_withBusinessAdmin_success() {
        testBusiness.setPrimaryImageId(testImages.get(0).getId());
        businessImageService.setPrimaryImage(testBusiness.getId(), testImages.get(1).getId(), new AppUserDetails(testUserBusinessAdmin));
        Assertions.assertEquals(testImages.get(1).getId(), testBusiness.getPrimaryImageId());
    }

    /**
     * Tests successfully changing the primary image of a business as a system administrator
     */
    @Test
    void makePrimaryImage_withSystemAdmin_success() {
        testBusiness.setPrimaryImageId(testImages.get(0).getId());
        businessImageService.setPrimaryImage(testBusiness.getId(), testImages.get(1).getId(), new AppUserDetails(testSystemAdmin));
        Assertions.assertEquals(testImages.get(1).getId(), testBusiness.getPrimaryImageId());
    }

    /**
     * Tests that deleting a business image as a user who is not a business admin results in an error.
     */
    @Test
    void deleteImage_notAdmin_throwsException() {
        DeleteBusinessImageDTO deleteBusinessImageDTO = new DeleteBusinessImageDTO(
                testBusiness.getId(),
                testImages.get(1).getId(),
                new AppUserDetails(testUser)
        );
        Assertions.assertThrows(ForbiddenAdministratorActionException.class,
                () -> businessImageService.deleteImage(deleteBusinessImageDTO));
    }

    /**
     * Tests that deleting a image for a business that does not exist results in an error.
     */
    @Test
    void deleteImage_noBusinessExists_throwsException() {
        given(businessRepository.findById(1000)).willReturn(Optional.empty());
        DeleteBusinessImageDTO deleteBusinessImageDTO = new DeleteBusinessImageDTO(
                1000,
                testImages.get(1).getId(),
                new AppUserDetails(testUserBusinessAdmin)
        );

        Assertions.assertThrows(BusinessNotFoundException.class,
                () -> businessImageService.deleteImage(deleteBusinessImageDTO));
    }

    /**
     * Tests that deleting a business image for an image that does not exist results in an error.
     */
    @Test
    void deleteImage_noImageExists_throwsException() {
        given(imageRepository.findById(1000)).willReturn(Optional.empty());
        DeleteBusinessImageDTO deleteBusinessImageDTO = new DeleteBusinessImageDTO(
                testBusiness.getId(),
                1000,
                new AppUserDetails(testUserBusinessAdmin)
        );

        Assertions.assertThrows(NotAcceptableException.class,
                () -> businessImageService.deleteImage(deleteBusinessImageDTO));
    }

    /**
     * Tests the success case for deleting a business image as a business admin.
     * Expects the imageUtil.deleteImage() to be called twice,
     * once for the image file, once for the thumbnail file.
     *
     * @throws IOException exception thrown by imageUtil.deleteImage()
     */
    @Test
    void deleteImage_withBusinessAdmin_success() throws IOException {
        DeleteBusinessImageDTO deleteBusinessImageDTO = new DeleteBusinessImageDTO(
                testBusiness.getId(),
                testImages.get(1).getId(),
                new AppUserDetails(testUserBusinessAdmin)
        );

        businessImageService.deleteImage(deleteBusinessImageDTO);

        ArgumentCaptor<String> imagePathCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(imageUtil, times(2)).deleteImage(imagePathCaptor.capture());

        Assertions.assertEquals(2, testBusiness.getImages().size());
    }

    /**
     * Tests the success case for deleting a business image as a system admin.
     * Expects the imageUtil.deleteImage() to be called twice,
     * once for the image file, once for the thumbnail file.
     *
     * @throws IOException exception thrown by imageUtil.deleteImage()
     */
    @Test
    void deleteImage_withSystemAdmin_success() throws IOException {
        DeleteBusinessImageDTO deleteBusinessImageDTO = new DeleteBusinessImageDTO(
                testBusiness.getId(),
                testImages.get(1).getId(),
                new AppUserDetails(testSystemAdmin)
        );

        businessImageService.deleteImage(deleteBusinessImageDTO);

        ArgumentCaptor<String> imagePathCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(imageUtil, times(2)).deleteImage(imagePathCaptor.capture());

        Assertions.assertEquals(2, testBusiness.getImages().size());
    }

}
