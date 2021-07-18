package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.service.BusinessImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void addBusinessImage_notLoggedIn_returnsStatus401() throws Exception {

    }

    @Test
    void addBusinessImage_notAdmin_returnStatus403() throws Exception {

    }

    @Test
    void addBusinessImage_noBusinessExists_returnsStatus406() throws Exception {

    }

    @Test
    void addBusinessImage_asAdmin_created201() throws Exception {

    }

    @Test
    void addBusinessImage_asSystemAdmin_created201() throws Exception {

    }
}
