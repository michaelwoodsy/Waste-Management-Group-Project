package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageDTO;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageResponseDTO;
import org.seng302.project.serviceLayer.dto.business.DeleteBusinessImageDTO;
import org.seng302.project.serviceLayer.dto.product.DeleteProductImageDTO;
import org.seng302.project.serviceLayer.exceptions.ForbiddenException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.business.NoBusinessExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.service.BusinessImageService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BusinessImageControllerTest extends AbstractInitializer {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BusinessImageService businessImageService;

    private User testUser;
    private User testUserBusinessAdmin;
    private User testSystemAdmin;
    private Business testBusiness;
    private MockMultipartFile testFile;

    @BeforeEach
    public void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testUserBusinessAdmin = this.getTestUserBusinessAdmin();
        testSystemAdmin = this.getTestSystemAdmin();
        testBusiness = this.getTestBusiness();
        testFile = this.getTestFile();
    }

    /**
     * Tests that trying to add a new business image when not logged in returns a status 401
     */
    @Test
    void addBusinessImage_notLoggedIn_returnsStatus401() throws Exception {
        RequestBuilder postBusinessImageRequest = MockMvcRequestBuilders
                .multipart("/businesses/{businessId}/images",
                        testBusiness.getId())
                .file(testFile);

        mockMvc.perform(postBusinessImageRequest).andExpect(status().isUnauthorized());
    }

    /**
     * Tests that a status code 403 is returned when a user not an admin of the business nor a system admin.
     */
    @Test
    void addBusinessImage_notAdmin_returnStatus403() throws Exception {
        Mockito.when(businessImageService.addBusinessImage(Mockito.any(AddBusinessImageDTO.class)))
                .thenThrow(new ForbiddenAdministratorActionException(testBusiness.getId()));

        RequestBuilder postBusinessImageRequest = MockMvcRequestBuilders
                .multipart("/businesses/{businessId}/images",
                        testBusiness.getId())
                .file(testFile)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(postBusinessImageRequest).andExpect(status().isForbidden());
    }

    /**
     * Tests that a 406 status is returned when a business does not exist.
     */
    @Test
    void addBusinessImage_noBusinessExists_returnsStatus406() throws Exception {
        doThrow(NoBusinessExistsException.class)
                .when(businessImageService)
                .addBusinessImage(Mockito.any(AddBusinessImageDTO.class));

        RequestBuilder postBusinessImageRequest = MockMvcRequestBuilders
                .multipart("/businesses/{businessId}/images",
                        100)
                .file(testFile)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(postBusinessImageRequest).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that adding a new image as a business admin results in a 201 response
     */
    @Test
    void addBusinessImage_asAdmin_created201() throws Exception {
        Mockito.when(businessImageService.addBusinessImage(Mockito.any(AddBusinessImageDTO.class)))
                .thenReturn(new AddBusinessImageResponseDTO(1));

        RequestBuilder postBusinessImageRequest = MockMvcRequestBuilders
                .multipart("/businesses/{businessId}/images",
                        testBusiness.getId())
                .file(testFile)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(postBusinessImageRequest).andExpect(status().isCreated());
    }

    /**
     * Tests that adding a new image as a system admin results in a 201 response
     */
    @Test
    void addBusinessImage_asSystemAdmin_created201() throws Exception {
        Mockito.when(businessImageService.addBusinessImage(Mockito.any(AddBusinessImageDTO.class)))
                .thenReturn(new AddBusinessImageResponseDTO(1));

        RequestBuilder postBusinessImageRequest = MockMvcRequestBuilders
                .multipart("/businesses/{businessId}/images",
                        testBusiness.getId())
                .file(testFile)
                .with(user(new AppUserDetails(testSystemAdmin)));

        mockMvc.perform(postBusinessImageRequest).andExpect(status().isCreated());
    }

    /**
     * Tests that trying to a businesses primary image when not logged in returns a status 401
     */
    @Test
    void setBusinessPrimaryImage_returnStatus401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/businesses/{businessId}/images/{imageId}/makeprimary", testBusiness.getId(), 1))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tests that a status code 403 is returned when a user not an admin of the business nor a system admin.
     */
    @Test
    void setBusinessPrimaryImage_notAdmin_returnStatus403() throws Exception {
        doThrow(ForbiddenException.class)
                .when(businessImageService)
                .setPrimaryImage(Mockito.any(Integer.class), Mockito.any(Integer.class), Mockito.any(AppUserDetails.class));

        mockMvc.perform(MockMvcRequestBuilders
                .put("/businesses/{businessId}/images/{imageId}/makeprimary", testBusiness.getId(), 1)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that a status code 406 is returned when a user or business is not found.
     */
    @Test
    void setBusinessPrimaryImage_notExists_returnStatus406() throws Exception {
        doThrow(NotAcceptableException.class)
                .when(businessImageService)
                .setPrimaryImage(Mockito.any(Integer.class), Mockito.any(Integer.class), Mockito.any(AppUserDetails.class));

        mockMvc.perform(MockMvcRequestBuilders
                .put("/businesses/{businessId}/images/{imageId}/makeprimary", testBusiness.getId(), 1)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to delete a business image fails
     * when a user is not logged in.
     * Expect 401 response
     */
    @Test
    void deleteBusinessImage_notLoggedIn_returnsStatus401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/businesses/{businessId}/images/{imageId}", testBusiness.getId(), 1))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tests that request to delete a business image fails
     * when a user is neither an admin nor a GAA.
     * Expect 403 response
     */
    @Test
    void deleteBusinessImage_notAdmin_returnsStatus403() throws Exception {
        doThrow(new ForbiddenAdministratorActionException(testBusiness.getId()))
                .when(businessImageService).deleteImage(Mockito.any(DeleteBusinessImageDTO.class));

        RequestBuilder deleteBusinessImageRequest = MockMvcRequestBuilders
                .delete("/businesses/{businessId}/images/{imageId}",
                        testBusiness.getId(), 1)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(deleteBusinessImageRequest).andExpect(status().isForbidden());
    }

    /**
     * Tests that request to delete a business image fails
     * when the business does not exist
     * Expect 406 response
     */
    @Test
    void deleteBusinessImage_noBusinessExists_returnsStatus406() throws Exception {
        doThrow(new BusinessNotFoundException(1000))
                .when(businessImageService).deleteImage(Mockito.any(DeleteBusinessImageDTO.class));

        RequestBuilder deleteBusinessImageRequest = MockMvcRequestBuilders
                .delete("/businesses/{businessId}/images/{imageId}",
                        1000, 1)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(deleteBusinessImageRequest).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to delete a business image fails
     * when the business image does not exist
     * Expect 406 response
     */
    @Test
    void deleteBusinessImage_noBusinessImageExists_returnsStatus406() throws Exception {
        doThrow(new BusinessNotFoundException(1000))
                .when(businessImageService).deleteImage(Mockito.any(DeleteBusinessImageDTO.class));

        RequestBuilder deleteBusinessImageRequest = MockMvcRequestBuilders
                .delete("/businesses/{businessId}/images/{imageId}",
                        testBusiness.getId(), 1000)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(deleteBusinessImageRequest).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that request to delete a business image is successful
     * when the user is a business admin
     * Expect 200 response
     */
    @Test
    void deleteBusinessImage_asBusinessAdmin_returnsStatus200() throws Exception {
        RequestBuilder deleteBusinessImageRequest = MockMvcRequestBuilders
                .delete("/businesses/{businessId}/images/{imageId}",
                        testBusiness.getId(), 1)
                .with(user(new AppUserDetails(testUserBusinessAdmin)));

        mockMvc.perform(deleteBusinessImageRequest).andExpect(status().isOk());
    }

    /**
     * Tests that request to delete a business image is successful
     * when the user is a business admin
     * Expect 200 response
     */
    @Test
    void deleteBusinessImage_asSystemAdmin_returnsStatus200() throws Exception {
        RequestBuilder deleteBusinessImageRequest = MockMvcRequestBuilders
                .delete("/businesses/{businessId}/images/{imageId}",
                        testBusiness.getId(), 1)
                .with(user(new AppUserDetails(testSystemAdmin)));

        mockMvc.perform(deleteBusinessImageRequest).andExpect(status().isOk());
    }
}
