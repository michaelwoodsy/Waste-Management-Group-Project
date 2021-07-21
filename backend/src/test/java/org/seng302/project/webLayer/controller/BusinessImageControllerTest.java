package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageDTO;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageResponseDTO;
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

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BusinessImageControllerTest extends AbstractInitializer {

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
}
