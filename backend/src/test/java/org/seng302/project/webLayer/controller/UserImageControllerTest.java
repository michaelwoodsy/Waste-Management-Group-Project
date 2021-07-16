package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.product.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.dto.user.AddUserImageDTO;
import org.seng302.project.serviceLayer.dto.user.AddUserImageResponseDTO;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.product.ProductNotFoundException;
import org.seng302.project.serviceLayer.exceptions.user.ForbiddenUserException;
import org.seng302.project.serviceLayer.exceptions.user.UserImageNotFoundException;
import org.seng302.project.serviceLayer.service.UserImageService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserImageControllerTest extends AbstractInitializer {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserImageService userImageService;

    private User testUser;
    private User testUserBusinessAdmin;
    private User testSystemAdmin;
    private MockMultipartFile testFile;
    private List<Image> testImages;

    @BeforeEach
    public void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testUserBusinessAdmin = this.getTestUserBusinessAdmin();
        testSystemAdmin = this.getTestSystemAdmin();
        testFile = this.getTestFile();
        testImages = this.getTestImages();
    }

    /**
     * Tests that trying to add a new user image when not logged in returns a status 401
     */
    @Test
    void addUserImage_notLoggedIn_returnsStatus401() throws Exception {
        RequestBuilder postUserImageRequest = MockMvcRequestBuilders
                .multipart("/users/{userId}/images",
                        testUser.getId())
                .file(testFile);

        mockMvc.perform(postUserImageRequest).andExpect(status().isUnauthorized());
    }

    /**
     * Tests that a status code 403 is returned when a user is not themselves or system admin.
     */
    @Test
    void addUserImage_differentUser_returnsStatus403() throws Exception {
        Mockito.when(userImageService.addUserImage(Mockito.any(AddUserImageDTO.class)))
                .thenThrow(new ForbiddenUserException(testUser.getId()));

        RequestBuilder postUserImageRequest = MockMvcRequestBuilders
                .multipart("/users/{userId}/images",
                        testUser.getId())
                .file(testFile)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(postUserImageRequest).andExpect(status().isForbidden());
    }

    /**
     * Tests that a 406 status is returned when a user does not exist.
     */
    @Test
    void addUserImage_userNotFound_returnsStatus406() throws Exception {
        doThrow(NotAcceptableException.class)
                .when(userImageService)
                .addUserImage(Mockito.any(AddUserImageDTO.class));

        RequestBuilder postUserImageRequest = MockMvcRequestBuilders
                .multipart("/users/{userId}/images",
                        100)
                .file(testFile)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(postUserImageRequest).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that adding a new image as a valid user results in a 201 response
     */
    @Test
    void addUserImage_sameUser_created201() throws Exception {
        Mockito.when(userImageService.addUserImage(Mockito.any(AddUserImageDTO.class)))
                .thenReturn(new AddUserImageResponseDTO(1));

        RequestBuilder postUserImageRequest = MockMvcRequestBuilders
                .multipart("/users/{userId}/images",
                        testUser.getId())
                .file(testFile)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(postUserImageRequest).andExpect(status().isCreated());
    }

    /**
     * Tests that adding a new image as a system admin results in a 201 response
     */
    @Test
    void addUserImage_asSystemAdmin_created201() throws Exception {
        Mockito.when(userImageService.addUserImage(Mockito.any(AddUserImageDTO.class)))
                .thenReturn(new AddUserImageResponseDTO(1));

        RequestBuilder postUserImageRequest = MockMvcRequestBuilders
                .multipart("/users/{userId}/images",
                        testUser.getId())
                .file(testFile)
                .with(user(new AppUserDetails(testSystemAdmin)));

        mockMvc.perform(postUserImageRequest).andExpect(status().isCreated());
    }

    /**
     * Tests successful setting of a product's primary image.
     * Expect 200 response
     */
    @Test
    void setPrimaryImage_requestByUser_success() throws Exception {
        testUser.setPrimaryImageId(testImages.get(0).getId());
        testUser.setImages(Set.of(testImages.get(0), testImages.get(1)));
        RequestBuilder request = MockMvcRequestBuilders
                .put("/users/{userId}/images/{imageId}/makeprimary",
                        testUser.getId(),
                        testImages.get(1).getId())
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Tests that request to update primary image fails
     * for invalid userId.
     * Expect 406 response
     */
    @Test
    void setPrimaryImage_invalidUserId_Fail() throws Exception {
        doThrow(NoUserExistsException.class)
                .when(userImageService).setPrimaryImage(Mockito.any(Integer.class),
                Mockito.any(Integer.class),
                Mockito.any(AppUserDetails.class));

        testUser.setPrimaryImageId(1);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/users/{userId}/images/{imageId}/makeprimary",
                        100,
                        testImages.get(1).getId())
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to update primary image fails
     * for invalid imageId.
     * Expect 406 response
     */
    @Test
    void setPrimaryImage_invalidImageId_Fail() throws Exception {
        doThrow(UserImageNotFoundException.class)
                .when(userImageService).setPrimaryImage(Mockito.any(Integer.class),
                Mockito.any(Integer.class),
                Mockito.any(AppUserDetails.class));

        testUser.setPrimaryImageId(testImages.get(0).getId());
        testUser.setImages(Set.of(testImages.get(0), testImages.get(1)));

        RequestBuilder request = MockMvcRequestBuilders
                .put("/users/{userId}/images/{imageId}/makeprimary",
                        testUser.getId(),
                        testImages.get(2).getId())
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }
}
