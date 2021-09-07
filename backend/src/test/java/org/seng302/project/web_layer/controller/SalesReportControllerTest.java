package org.seng302.project.web_layer.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.service.SalesReportService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SalesReportControllerTest extends AbstractInitializer {

    private User testUser;
    private User owner;
    private Business business;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalesReportService salesReportService;

    @BeforeEach
    public void setup() {
        testUser = this.getTestUser();
        owner = this.getTestUserBusinessAdmin();
        business = this.getTestBusiness();
    }

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
                .param("granularity", "week")
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
                .param("periodStart", LocalDate.now().minusDays(30).toString())
                .param("periodEnd", LocalDate.now().toString())
                .param("granularity", "day")
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Test that getting a sales report when not an admin gives a 403 response
     */
    @Test
    void getSalesReport_notAdmin_403() throws Exception {
        Mockito.doThrow(new ForbiddenException("message"))
                .when(salesReportService).getSalesReport(any(Integer.class), any(String.class),
                any(String.class), any(String.class), any(AppUserDetails.class));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{id}/salesReport", business.getId())
                .param("periodStart", LocalDate.now().minusDays(30).toString())
                .param("periodEnd", LocalDate.now().toString())
                .param("granularity", "day")
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isForbidden());
    }


    /**
     * Test that getting a sales report for a business that doesn't exist gives a 406 response
     */
    @Test
    void getSalesReport_invalidBusiness_406() throws Exception {
        Mockito.doThrow(new NotAcceptableException("message"))
                .when(salesReportService).getSalesReport(any(Integer.class), any(String.class),
                any(String.class), any(String.class), any(AppUserDetails.class));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{id}/salesReport", 7)
                .param("periodStart", LocalDate.now().minusDays(30).toString())
                .param("periodEnd", LocalDate.now().toString())
                .param("granularity", "day")
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }
}
