package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUploadNewImage_notLoggedIn_returnsStatus401() throws Exception {
        RequestBuilder postProductImageRequest = MockMvcRequestBuilders
                .post("/businesses/1/products/1/images");

        mockMvc.perform(postProductImageRequest)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testUploadNewImage_notAdmin_returnStatus403() throws Exception {

    }

}
