package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.user.AddUserImageDTO;
import org.seng302.project.serviceLayer.dto.user.AddUserImageResponseDTO;
import org.seng302.project.serviceLayer.dto.user.DeleteUserImageDTO;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
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

    @BeforeEach
    public void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testUserBusinessAdmin = this.getTestUserBusinessAdmin();
        testSystemAdmin = this.getTestSystemAdmin();
        testFile = this.getTestFile();
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
     * Tests that request to delete a user image fails
     * when a user is not logged in.
     * Expect 401 response
     */
    @Test
    void deleteUserImage_notLoggedIn_returnsStatus401() throws Exception {
        RequestBuilder deleteUserImageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/images/{imageId}",
                        testUser.getId(),
                        2);

        mockMvc.perform(deleteUserImageRequest).andExpect(status().isUnauthorized());
    }


    /**
     * Tests that request to delete a user image fails
     * when a user is neither themselves nor a GAA.
     * Expect 403 response
     */
    @Test
    void deleteUserImage_differentUser_returnsStatus403() throws Exception {
        doThrow(ForbiddenUserException.class)
                .when(userImageService).deleteImage(Mockito.any(DeleteUserImageDTO.class));

        RequestBuilder deleteUserImageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/images/{imageId}",
                        testUser.getId(),
                        2)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(deleteUserImageRequest).andExpect(status().isForbidden());
    }


    /**
     * Tests that request to delete a user image fails
     * when the user does not exist
     * Expect 406 response
     */
    @Test
    void deleteUserImage_noUserExists_returnsStatus406() throws Exception {
        doThrow(NotAcceptableException.class)
                .when(userImageService).deleteImage(Mockito.any(DeleteUserImageDTO.class));

        RequestBuilder deleteUserImageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/images/{imageId}",
                        100,
                        2)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(deleteUserImageRequest).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to delete a user image fails
     * when the image does not exist
     * Expect 406 response
     */
    @Test
    void deleteUserImage_noImageExists_returnsStatus406() throws Exception {
        doThrow(new UserImageNotFoundException(testUser.getId(), 7))
                .when(userImageService).deleteImage(Mockito.any(DeleteUserImageDTO.class));


        RequestBuilder deleteUserImageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/images/{imageId}",
                        testUser.getId(),
                        7)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(deleteUserImageRequest).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to delete a user image is successful
     * when the user is deleting their own image
     * Expect 200 response
     */
    @Test
    void deleteUserImage_sameUser_ok200() throws Exception {
        RequestBuilder deleteUserImageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/images/{imageId}",
                        testUser.getId(),
                        2)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(deleteUserImageRequest).andExpect(status().isOk());
    }

    /**
     * Tests that request to delete a user image is successful
     * when the user is a GAA
     * Expect 200 response
     */
    @Test
    void deleteUserImage_asSystemAdmin_ok200() throws Exception {
        RequestBuilder deleteUserImageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/images/{imageId}",
                        testUser.getId(),
                        2)
                .with(user(new AppUserDetails(testSystemAdmin)));

        mockMvc.perform(deleteUserImageRequest).andExpect(status().isOk());
    }

}
