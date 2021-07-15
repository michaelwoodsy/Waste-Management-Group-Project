package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.service.UserImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void addUserImage_notLoggedIn_returnsStatus401() throws Exception {

    }

    @Test
    void addUserImage_differentUser_returnsStatus403() throws Exception {

    }

    @Test
    void addUserImage_userNotFound_returnsStatus406() throws Exception {

    }

    @Test
    void addUserImage_sameUser_created201() throws Exception {

    }

    @Test
    void addUserImage_asSystemAdmin_created201() throws Exception {

    }
}
