package org.seng302.project.service_layer.service;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.repository.SaleHistoryRepository;
import org.seng302.project.repository_layer.repository.SaleListingRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.user.LoginCredentialsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Provides logic for resale statistics
 */
@Service
public class ResaleStatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(ResaleStatisticsService.class.getName());
    private final UserRepository userRepository;
    private final SaleListingRepository saleListingRepository;
    private final SaleHistoryRepository saleHistoryRepository;

    @Autowired
    public ResaleStatisticsService(UserRepository userRepository,
                                   SaleListingRepository saleListingRepository,
                                   SaleHistoryRepository saleHistoryRepository){
        this.userRepository = userRepository;
        this.saleListingRepository = saleListingRepository;
        this.saleHistoryRepository = saleHistoryRepository;

    }

    /**
     * Retrieves the statistics of our website (total number of users, number of available sales listings,
     * total number of sales)
     * @return Json with the specified statistics above
     */
    public JSONObject getStatistics() {
        var totalUserCount = userRepository.findAll().size();
        var numAvailableListings = saleListingRepository.findAll().size();
        var totalNumSales = saleHistoryRepository.findAll().size();

        JSONObject returnJSON = new JSONObject();
        returnJSON.put("totalUserCount", totalUserCount);
        returnJSON.put("numAvailableListings", numAvailableListings);
        returnJSON.put("totalNumSales", totalNumSales);
        return returnJSON;
    }
}
