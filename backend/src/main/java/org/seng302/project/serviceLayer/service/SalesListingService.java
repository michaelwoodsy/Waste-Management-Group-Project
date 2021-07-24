package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.SaleListing;
import org.seng302.project.repositoryLayer.repository.*;
import org.seng302.project.repositoryLayer.specification.SaleListingSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
     *
     * @param currentResult The listings that have already been retrieved
     * @param conjunctions The list of strings that have been split by OR
     */
    private void searchNameField(Set<SaleListing> currentResult, String[] conjunctions) {
        for (String conjunction : conjunctions) {
            String[] terms = conjunction.split(AND_SPACE_REGEX); // Split by AND and spaces
            Specification<SaleListing> hasSpec = Specification.where(null);//empty spec to start off with
            Specification<SaleListing> containsSpec = Specification.where(null);//empty spec to start off with
            var searchContains = false;
            for (String term : terms) {
                //Remove quotes from quoted string, then search by full contents inside the quotes
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                    hasSpec = hasSpec.and(SaleListingSpecifications.hasProductName(term));
                } else {
                    hasSpec = hasSpec.and(SaleListingSpecifications.hasProductName(term));
                    containsSpec = containsSpec.and(SaleListingSpecifications.containsProductName(term));
                    searchContains = true;
                }
            }
            currentResult.addAll(saleListingRepository.findAll(hasSpec));
            if (searchContains) {
                currentResult.addAll(saleListingRepository.findAll(containsSpec));
            }
        }
    }

    /**
     * Searches the price field of Sales Listings to find sales listings with a price between two Doubles
     * Updates the set of Sales Listings.
     * This is assuming that if one of the prices is null, they only want to search by the other price
     *
     * @param currentResult The listings that have already been retrieved
     * @param minimum The minimum price for a sales listing
     * @param maximum The maximum price for a sales listing
     */
    private void searchPriceInBetween(Set<SaleListing> currentResult, Double minimum, Double maximum) {
        Specification<SaleListing> priceSpec = Specification.where(null);

        //Minimum price spec
        if (minimum != null) {
            priceSpec = priceSpec.and(Specification.where(SaleListingSpecifications.priceGreaterThan(minimum)));
        }

        //Maximum price spec
        if (maximum != null) {
            priceSpec = priceSpec.and(Specification.where(SaleListingSpecifications.priceLessThan(maximum)));
        }

        currentResult.addAll(saleListingRepository.findAll(priceSpec));
    }

    /**
     * Searches the closes field of Sales Listings to find sales listings with a closing date between two LocalDateTimes
     * Updates the set of Sales Listings.
     * This is assuming that if one of the dates is null, they only want to search by the other date
     *
     * @param currentResult The listings that have already been retrieved
     * @param afterDate The minimum closes date for a sales listing
     * @param beforeDate The maximum closes date for a sales listing
     */
    private void searchClosesInBetween(Set<SaleListing> currentResult, LocalDateTime afterDate, LocalDateTime beforeDate) {
        Specification<SaleListing> closingSpec = Specification.where(null);

        //Minimum closing date spec
        if (afterDate != null) {
            closingSpec = closingSpec.and(Specification.where(SaleListingSpecifications.closesAfter(afterDate)));
        }

        //Maximum closing date spec
        if (beforeDate != null) {
            closingSpec = closingSpec.and(Specification.where(SaleListingSpecifications.closesBefore(beforeDate)));
        }

        currentResult.addAll(saleListingRepository.findAll(closingSpec));
    }
}
