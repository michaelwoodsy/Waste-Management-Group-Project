package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.user.AddUserImageDTO;
import org.seng302.project.serviceLayer.dto.user.DeleteUserImageDTO;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.user.ForbiddenUserException;
import org.seng302.project.serviceLayer.util.ImageUtil;
import org.seng302.project.serviceLayer.util.SpringEnvironment;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

@SpringBootTest
class UserImageServiceTest extends AbstractInitializer {

    @Autowired
    private UserImageService userImageService;
    @Autowired
    private SpringEnvironment springEnvironment;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ImageRepository imageRepository;
    @MockBean
    private ImageUtil imageUtil;

    private User testUser;
    private User testSystemAdmin;
    private User testUserBusinessAdmin;
    private List<Image> testImages;
    private MockMultipartFile testImageFile;

    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testSystemAdmin = this.getTestSystemAdmin();
        testUserBusinessAdmin = this.getTestUserBusinessAdmin();
        testImages = this.getTestImages();
        testImageFile = this.getTestImageFile();
        testUser.setImages(new HashSet<>(testImages));
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
    }

    /**
     * Tests that the correct exception is thrown if a user tries to add an image for a user
     * that is not them and the user is not a GAA.
     */
    @Test
    void addUserImage_differentUser_throwsException() {
        AddUserImageDTO dto = new AddUserImageDTO(
                testUser.getId(),
                new AppUserDetails(testUserBusinessAdmin),
                testImageFile
        );

        Assertions.assertThrows(ForbiddenUserException.class,
                () -> userImageService.addUserImage(dto));
    }

    /**
     * Tests that the correct exception is thrown if an image is attempted to be added
     * for a user that does not exist.
     */
    @Test
    void addUserImage_noUserExists_throwsException() {
        AddUserImageDTO dto = new AddUserImageDTO(
                100,
                new AppUserDetails(testUserBusinessAdmin),
                testImageFile
        );

        Assertions.assertThrows(NoUserExistsException.class,
                () -> userImageService.addUserImage(dto));
    }

    /**
     * Tests that an image is successfully created and added to a user.
     */
    @Test
    void addUserImage_sameUser_success() throws IOException {
        Mockito.when(imageRepository.save(Mockito.any(Image.class)))
                .thenAnswer(invocation -> {
                    Image image = invocation.getArgument(0);
                    image.setId(4);
                    testImages.add(image);
                    return image;
                });

        AddUserImageDTO dto = new AddUserImageDTO(
                testUser.getId(),
                new AppUserDetails(testUser),
                testImageFile
        );
        userImageService.addUserImage(dto);
        Assertions.assertEquals(4, testUser.getImages().size());
        ArgumentCaptor<String> imagePathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BufferedImage> imageArgumentCaptor = ArgumentCaptor.forClass(BufferedImage.class);
        Mockito.verify(imageUtil).saveImage(imageArgumentCaptor.capture(), imagePathCaptor.capture());
    }

    /**
     * Tests that an image is successfully created and added to a user.
     */
    @Test
    void addUserImage_withSystemAdmin_success() throws IOException {
        Mockito.when(imageRepository.save(Mockito.any(Image.class)))
                .thenAnswer(invocation -> {
                    Image image = invocation.getArgument(0);
                    image.setId(4);
                    testImages.add(image);
                    return image;
                });

        AddUserImageDTO dto = new AddUserImageDTO(
                testUser.getId(),
                new AppUserDetails(testSystemAdmin),
                testImageFile
        );
        userImageService.addUserImage(dto);
        Assertions.assertEquals(4, testUser.getImages().size());
        ArgumentCaptor<String> imagePathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BufferedImage> imageArgumentCaptor = ArgumentCaptor.forClass(BufferedImage.class);
        Mockito.verify(imageUtil).saveImage(imageArgumentCaptor.capture(), imagePathCaptor.capture());
    }

    /**
     * Tests that deleting a user image as a user who is not themselves nor a GAA results in an error.
     */
    @Test
    void deleteImage_differentUser_throwsException() {
        DeleteUserImageDTO deleteUserImageDTO = new DeleteUserImageDTO (
                testUser.getId(),
                2,
                new AppUserDetails(testUserBusinessAdmin)
        );
        Assertions.assertThrows(ForbiddenUserException.class,
                () -> userImageService.deleteImage(deleteUserImageDTO));
    }

    /**
     * Tests that deleting a user image for a user that does not exist results in an error.
     */
    @Test
    void deleteImage_noUserExists_throwsException() {
        given(userRepository.findById(4)).willReturn(Optional.empty());
        DeleteUserImageDTO deleteUserImageDTO = new DeleteUserImageDTO(
                4,
                2,
                new AppUserDetails(testUser)
        );
        Assertions.assertThrows(NoUserExistsException.class,
                () -> userImageService.deleteImage(deleteUserImageDTO));
    }

    /**
     * Tests that deleting a user image for an image that does not exist results in an error.
     */
    @Test
    void deleteImage_noImageExists_throwsException() {
        DeleteUserImageDTO deleteUserImageDTO = new DeleteUserImageDTO(
                testUser.getId(),
                7,
                new AppUserDetails(testUser)
        );
        Assertions.assertThrows(NotAcceptableException.class,
                () -> userImageService.deleteImage(deleteUserImageDTO));
    }


    /**
     * Tests the success case for deleting a user image.
     * Expects the imageUtil.deleteImage() to be called twice,
     * once for the image file, once for the thumbnail file.
     *
     * @throws IOException exception thrown by imageUtil.deleteImage()
     */
    @Test
    void deleteImage_sameUser_success() throws IOException {
        DeleteUserImageDTO deleteUserImageDTO = new DeleteUserImageDTO(
                testUser.getId(),
                2,
                new AppUserDetails(testUser)
        );

        userImageService.deleteImage(deleteUserImageDTO);

        ArgumentCaptor<String> imagePathCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(imageUtil, times(2)).deleteImage(imagePathCaptor.capture());

        Assertions.assertEquals(2, testUser.getImages().size());
    }

    /**
     * Tests the success case for deleting a user image.
     * Expects the imageUtil.deleteImage() to be called twice,
     * once for the image file, once for the thumbnail file.
     *
     * @throws IOException exception thrown by imageUtil.deleteImage()
     */
    @Test
    void deleteImage_systemAdmin_success() throws IOException {
        DeleteUserImageDTO deleteUserImageDTO = new DeleteUserImageDTO(
                testUser.getId(),
                2,
                new AppUserDetails(testSystemAdmin)
        );

        userImageService.deleteImage(deleteUserImageDTO);

        ArgumentCaptor<String> imagePathCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(imageUtil, times(2)).deleteImage(imagePathCaptor.capture());

        Assertions.assertEquals(2, testUser.getImages().size());
    }

    /**
     * Tests that setting a primary image as a user results in a success.
     */
    @Test
    void setPrimaryImage_withUser_success() {
        //Set Users Images, Make the first image be the primary one
        testUser.setImages(Set.of(testImages.get(0), testImages.get(1)));
        testUser.setPrimaryImageId(testImages.get(0).getId());

        userImageService.setPrimaryImage(testUser.getId(), testImages.get(1).getId(), new AppUserDetails(testUser));

        Assertions.assertEquals(testImages.get(1).getId(), testUser.getPrimaryImageId());
    }

    /**
     * Tests that setting a primary image as a System Admin results in a success.
     */
    @Test
    void setPrimaryImage_withSystemAdmin_success() {
        //Set Users Images, Make the first image be the primary one
        testUser.setImages(Set.of(testImages.get(0), testImages.get(1)));
        testUser.setPrimaryImageId(testImages.get(0).getId());

        userImageService.setPrimaryImage(testUser.getId(), testImages.get(1).getId(), new AppUserDetails(testSystemAdmin));

        Assertions.assertEquals(testImages.get(1).getId(), testUser.getPrimaryImageId());
    }

    /**
     * Tests that setting a primary image with an invalid userId results in a fail
     * and a NoUserExistsException exception is thrown.
     */
    @Test
    void setPrimaryImage_withInvalidUserId_Fails() {
        var imageId = testImages.get(1).getId();
        var appUser = new AppUserDetails(testUser);

        Assertions.assertThrows(NoUserExistsException.class,
                () -> userImageService.setPrimaryImage(100, imageId , appUser));
    }

    /**
     * Tests that setting a primary image with an invalid imageId results in a fail
     * and a NotAcceptableException exception is thrown.
     */
    @Test
    void setPrimaryImage_withInvalidImageId_Fails() {
        var userId = testUser.getId();
        var appUser = new AppUserDetails(testUser);

        Assertions.assertThrows(NotAcceptableException.class,
                () -> userImageService.setPrimaryImage(userId, 100, appUser));
    }

    /**
     * Tests that setting a primary image as a different user results in a fail
     * and a ForbiddenUserException exception is thrown.
     */
    @Test
    void setPrimaryImage_asDifferentUser_Fails() {
        var userId = testUser.getId();
        var appUser = new AppUserDetails(testUserBusinessAdmin);
        Assertions.assertThrows(ForbiddenUserException.class,
                () -> userImageService.setPrimaryImage(userId, 100, appUser));
    }
}
