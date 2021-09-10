package org.seng302.project.web_layer.controller;

import org.junit.jupiter.api.Test;
import org.seng302.project.service_layer.service.ResaleStatisticsService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ResaleStatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResaleStatisticsService resaleStatisticsService;

    /**
     * Tests that getting resale statistics results in a 200 OK
     */
    @Test
    void getResaleStatistics_200() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/statistics");

        mockMvc.perform(request).andExpect(status().isOk());
    }
}
