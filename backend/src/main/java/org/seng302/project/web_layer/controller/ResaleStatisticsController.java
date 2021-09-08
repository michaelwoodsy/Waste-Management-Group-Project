package org.seng302.project.web_layer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.service_layer.service.ResaleStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Rest controller for resale statistics.
 */
@RestController
public class ResaleStatisticsController {
    private static final Logger logger = LoggerFactory.getLogger(ResaleStatisticsController.class.getName());

    private final ResaleStatisticsService resaleStatisticsService;

    @Autowired
    public ResaleStatisticsController(
            ResaleStatisticsService resaleStatisticsService) {
        this.resaleStatisticsService = resaleStatisticsService;
    }

    /**
     * Sends a request to get the current Resale statistics
     * @return JSONObject with the total number of users, number of available sale listings, and total number of sold sales
     */
    @GetMapping("/statistics")
    @ResponseStatus(HttpStatus.OK)
    public JSONObject getStatistics(){
        logger.info("Request to get Resale statistics");
        return resaleStatisticsService.getStatistics();
    }
}
