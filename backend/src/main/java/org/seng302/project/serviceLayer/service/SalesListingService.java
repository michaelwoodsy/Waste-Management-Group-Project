package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.SaleListing;
import org.seng302.project.repositoryLayer.repository.*;
import org.seng302.project.repositoryLayer.specification.SaleListingSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

@Service
public class SalesListingService {
    private static final Logger logger = LoggerFactory.getLogger(SalesListingService.class.getName());

    private final SaleListingRepository saleListingRepository;

    private static final String AND_SPACE_REGEX = "( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String QUOTE_REGEX = "^\".*\"$";

    @Autowired
    public SalesListingService(SaleListingRepository saleListingRepository) {
        this.saleListingRepository = saleListingRepository;
    }


    /**
     * Searches the product name field, handling ORs, ANDs, spaces and quotes
     * Updates the set of Sales Listings.
     * @param currentResult The listings that have already been retrieved
     * @param conjunctions The list of strings that have been split by OR
     */
    private void searchNameField(Set<SaleListing> currentResult, String[] conjunctions) {
        for (String conjunction : conjunctions) {
            String[] terms = conjunction.split(AND_SPACE_REGEX); // Split by AND and spaces
            Specification<SaleListing> spec = Specification.where(null);//empty spec to start off with
            for (String term : terms) {
                //Remove quotes from quoted string, then search by full contents inside the quotes
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                }
                spec = spec.and(SaleListingSpecifications.containsProductName(term));
            }
            currentResult.addAll(saleListingRepository.findAll(spec));
        }
    }
}
