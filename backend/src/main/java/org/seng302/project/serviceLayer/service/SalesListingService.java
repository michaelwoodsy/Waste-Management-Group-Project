package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.SaleListing;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.*;
import org.seng302.project.repositoryLayer.specification.BusinessSpecifications;
import org.seng302.project.repositoryLayer.specification.SaleListingSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class SalesListingService {
    private static final Logger logger = LoggerFactory.getLogger(SalesListingService.class.getName());

    private final SaleListingRepository saleListingRepository;
    private final BusinessRepository businessRepository;

    private static final String AND_SPACE_REGEX = "( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String QUOTE_REGEX = "^\".*\"$";

    @Autowired
    public SalesListingService(SaleListingRepository saleListingRepository, BusinessRepository businessRepository) {
        this.saleListingRepository = saleListingRepository;
        this.businessRepository = businessRepository;
    }


    /**
     * Searches the product name field, handling ORs, ANDs, spaces and quotes
     *
     * @param conjunctions The list of strings that have been split by OR
     * @return specification you can add to the current specification
     */
    private Specification<SaleListing> searchNameField(String[] conjunctions) {

        Specification<SaleListing> hasSpec = Specification.where(null);//empty spec to start off with
        Specification<SaleListing> containsSpec = Specification.where(null);//empty spec to start off with

        for (String conjunction : conjunctions) {
            Specification<SaleListing> newHasSpec = Specification.where(null);
            Specification<SaleListing> newContainsSpec = Specification.where(null);

            String[] terms = conjunction.split(AND_SPACE_REGEX); // Split by AND and spaces
            for (String term : terms) {
                //Remove quotes from quoted string, then search by full contents inside the quotes
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                    newHasSpec = newHasSpec.and(SaleListingSpecifications.hasProductName(term));
                } else {
                    newHasSpec = newHasSpec.and(SaleListingSpecifications.hasProductName(term));
                    newContainsSpec = newContainsSpec.and(SaleListingSpecifications.containsProductName(term));
                }
            }
            //Add the new specification to the full specification using .or
            hasSpec = hasSpec.or(newHasSpec);
            containsSpec = containsSpec.or(newContainsSpec);
        }
        //Return the two hasSpec and containsSpec added together with a .or
        return hasSpec.or(containsSpec);
    }

    /**
     * Searches the price field of Sales Listings to find sales listings with a price between two Doubles
     * This is assuming that if one of the prices is null, they only want to search by the other price
     *
     * @param minimum The minimum price for a sales listing
     * @param maximum The maximum price for a sales listing
     * @return specification you can add to the current specification
     */
    private Specification<SaleListing> searchPriceInBetween(Double minimum, Double maximum) {
        Specification<SaleListing> priceSpec = Specification.where(null);

        //Minimum price spec
        if (minimum != null) {
            priceSpec = priceSpec.and(Specification.where(SaleListingSpecifications.priceGreaterThan(minimum)));
        }

        //Maximum price spec
        if (maximum != null) {
            priceSpec = priceSpec.and(Specification.where(SaleListingSpecifications.priceLessThan(maximum)));
        }

        return priceSpec;
    }

    /**
     * Searches the closes field of Sales Listings to find sales listings with a closing date between two LocalDateTimes
     * This is assuming that if one of the dates is null, they only want to search by the other date
     *
     * @param afterDate The minimum closes date for a sales listing
     * @param beforeDate The maximum closes date for a sales listing
     * @return specification you can add to the current specification
     */
    private Specification<SaleListing> searchClosesInBetween(LocalDateTime afterDate, LocalDateTime beforeDate) {
        Specification<SaleListing> closingSpec = Specification.where(null);

        //Minimum closing date spec
        if (afterDate != null) {
            closingSpec = closingSpec.and(Specification.where(SaleListingSpecifications.closesAfter(afterDate)));
        }

        //Maximum closing date spec
        if (beforeDate != null) {
            closingSpec = closingSpec.and(Specification.where(SaleListingSpecifications.closesBefore(beforeDate)));
        }

        return closingSpec;
    }

    /**
     * Searches for products by businesses that are located in specified countries, handling ORs, ANDs, spaces and quotes
     *
     * @param conjunctions The list of strings that have been split by OR
     * @return specification you can add to the current specification
     */
    private Specification<SaleListing> searchByBusinessCountry(String[] conjunctions) {

        Specification<Business> hasSpec = Specification.where(null);//empty spec to start off with
        Specification<Business> containsSpec = Specification.where(null);//empty spec to start off with

        for (String conjunction : conjunctions) {
            Specification<Business> newHasSpec = Specification.where(null);
            Specification<Business> newContainsSpec = Specification.where(null);

            String[] terms = conjunction.split(AND_SPACE_REGEX); // Split by AND and spaces
            for (String term : terms) {
                //Remove quotes from quoted string, then search by full contents inside the quotes
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                    newHasSpec = newHasSpec.and(BusinessSpecifications.hasCountry(term));
                } else {
                    newHasSpec = newHasSpec.and(BusinessSpecifications.hasCountry(term));
                    newContainsSpec = newContainsSpec.and(BusinessSpecifications.containsCountry(term));
                }
            }
            //Add the new specification to the full specification using .or
            hasSpec = hasSpec.or(newHasSpec);
            containsSpec = containsSpec.or(newContainsSpec);
        }
        hasSpec = hasSpec.or(containsSpec);

        //Get businesses with country matching and add their id's to spec
        List<Business> businesses = businessRepository.findAll(hasSpec);

        Specification<SaleListing> spec = Specification.where(null);
        for (Business business: businesses) {
            spec.or(SaleListingSpecifications.isBusinessId(business.getId()));
        }

        return spec;
    }
}
