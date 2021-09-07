package org.seng302.project.web_layer.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SalesReportControllerTest extends AbstractInitializer {

    private User testUser;
    private User owner;
    private User systemAdmin;
    private Business business;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        testUser = this.getTestUser();
        owner = this.getTestUserBusinessAdmin();
        systemAdmin = this.getTestSystemAdmin();
        business = this.getTestBusiness();
    }

    //TODO: tests for the getSalesReport endpoint:
    //403
    //406

    /**
     * Test that trying to get a sales report when not logged in
     * results in a 401 response
     */
    @Test
    void getSalesReport_notLoggedIn_401() throws Exception {
        mockMvc.perform(get("/businesses/{id}/salesReport", business.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test that getting a sales report without providing a start and end date
     * gives a 400 response
     */
    @Test
    void getSalesReport_missingDateRange_400() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{id}/salesReport", business.getId())
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }


    /**
     * Test that successfully getting a sales report gives a 200 response
     */
    @Test
    void getSalesReport_success_200() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{id}/salesReport", business.getId())
                .param("periodStart", LocalDate.now().minusDays(30).toString()) //TODO: this doesn't work
                //Can't convert string back to LocalDate :(
                .param("periodEnd", LocalDate.now().toString())
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

}
