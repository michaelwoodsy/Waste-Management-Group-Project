package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.SaleListing;
import org.seng302.project.repositoryLayer.repository.*;
import org.seng302.project.repositoryLayer.specification.BusinessSpecifications;
import org.seng302.project.repositoryLayer.specification.SaleListingSpecifications;
import org.seng302.project.serviceLayer.dto.saleListings.GetSalesListingDTO;
import org.seng302.project.serviceLayer.dto.saleListings.SearchSaleListingsDTO;
import org.seng302.project.serviceLayer.exceptions.InvalidDateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SaleListingService {

    private static final Logger logger = LoggerFactory.getLogger(SaleListingService.class.getName());
    private static final String AND_SPACE_REGEX = "( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String QUOTE_REGEX = "^\".*\"$";

    private final SaleListingRepository saleListingRepository;
    private final BusinessRepository businessRepository;

    @Autowired
    public SaleListingService(SaleListingRepository saleListingRepository,
                              BusinessRepository businessRepository) {
        this.saleListingRepository = saleListingRepository;
        this.businessRepository = businessRepository;
    }


    /**
     * Searches sales listings to match specific requirements set out in the SearchSaleListingsDTO
     *
     * @param dto DTO that holds the search requirements:
     *            the Search Query,
     *            whether to search for Product Name, Business Name and/or Business Location
     *            the lower and upper price range
     *            the lower and upper closing date range
     * @return List of the paginated list of sales listings, and the total number of sales listings
     */
    public List<Object> searchSaleListings(SearchSaleListingsDTO dto) {
        Specification<SaleListing> spec = Specification.where(null);
        List<SaleListing> listings;
        long totalCount;
        boolean sortASC;
        List<Object> sortChecker = checkSort(dto.getSortBy());
        sortASC = (boolean) sortChecker.get(0);
        String sortBy = (String) sortChecker.get(1);
        String searchQuery = dto.getSearchQuery().toLowerCase(); // Convert search query to all lowercase.
        String[] conjunctions = searchQuery.split(" or (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by OR

        //Product name
        if (dto.isMatchProductName()) {
            spec = spec.and(searchNameField(conjunctions));
        }

        //Business name
        if (dto.isMatchBusinessName()) {
            spec = spec.and(searchByBusinessName(conjunctions));
        }

        //Business location
        if (dto.isMatchBusinessLocation()) {
            spec = spec.and(searchByBusinessCountry(conjunctions));
        }

        //Price range
        spec = spec.and(searchPriceInBetween(dto.getPriceRangeLower(), dto.getPriceRangeUpper()));

        //Closing date range
        try {
            LocalDateTime closingDateLower = null;
            LocalDateTime closingDateUpper = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (dto.getClosingDateLower() != null) {
                closingDateLower = LocalDateTime.parse(dto.getClosingDateLower(), formatter);
            }
            if (dto.getClosingDateUpper() != null) {
                closingDateUpper = LocalDateTime.parse(dto.getClosingDateUpper(), formatter);
            }
            spec = spec.and(searchClosesInBetween(closingDateLower, closingDateUpper));
        } catch (DateTimeParseException parseException) {
            InvalidDateException invalidDateException = new InvalidDateException();
            logger.warn(invalidDateException.getMessage());
            throw invalidDateException;
        }


        if (!sortBy.isEmpty()) {
            Page<SaleListing> page = sortListingSearch(spec, sortBy, sortASC, dto.getPageNumber());
            totalCount = page.getTotalElements();
            listings = page.getContent();
        } else {
            Pageable pageable = PageRequest.of(dto.getPageNumber(), 10);
            Page<SaleListing> page = saleListingRepository.findAll(spec, pageable);
            totalCount = page.getTotalElements();
            listings = page.getContent();
        }

        logger.info("Retrieved {} Sales Listings, showing {}", totalCount, listings.size());

        // Map user objects to GetUserDTO objects and return
        return Arrays.asList(listings.stream().map(GetSalesListingDTO::new).collect(Collectors.toList()), totalCount);
    }




    /**
     * Helper function for listing search, does the sorting
     * @param spec the specification used to search by
     * @param sortBy the column that is to be sorted
     * @param sortASC the direction of the sort
     * @return the sorted list of sales listings searched for
     */
    public Page<SaleListing> sortListingSearch(Specification<SaleListing> spec, String sortBy, boolean sortASC, Integer pageNumber){
        if(sortASC){
            sortBy = sortBy.substring(0, sortBy.lastIndexOf("A"));
            Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by(Sort.Order.asc(sortBy).ignoreCase()));
            return saleListingRepository.findAll(spec, pageable);
        } else {
            sortBy = sortBy.substring(0, sortBy.lastIndexOf("D"));
            Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by(Sort.Order.desc(sortBy).ignoreCase()));
            return saleListingRepository.findAll(spec, pageable);
        }
    }

    public List<Object> checkSort(String sortBy){
        switch(sortBy){
            //TODO: Order by product name and seller somehow
            case "quantityASC": case "quantityDESC": case "priceASC": case "priceDESC":
            case "createdASC": case  "createdDESC": case "closesASC": case  "closesDESC":
                break;
            default:
                sortBy = "";
        }
        return List.of(sortBy.contains("ASC"), sortBy);
    }






    /**
     * Searches the product name field, handling ORs, ANDs, spaces and quotes
     *
     * @param conjunctions The list of strings that have been split by OR
     * @return specification you can add to the current specification
     */
    private Specification<SaleListing> searchNameField(String[] conjunctions) {

        Specification<SaleListing> spec = null;

        for (String conjunction : conjunctions) {
            Specification<SaleListing> newSpec = Specification.where(null);

            String[] terms = conjunction.split(AND_SPACE_REGEX); // Split by AND and spaces
            for (String term : terms) {
                //Remove quotes from quoted string, then search by full contents inside the quotes
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                    newSpec = newSpec.and(SaleListingSpecifications.hasProductName(term));
                } else {
                    newSpec = newSpec.and(SaleListingSpecifications.hasProductName(term))
                            .or(SaleListingSpecifications.containsProductName(term));
                }
            }
            if (spec == null) {
                spec = newSpec;
            } else {
                spec = spec.or(newSpec);
            }
        }
        //Return the two hasSpec and containsSpec added together with a .or
        return spec;
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
     * @param afterDate  The minimum closes date for a sales listing
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

        Specification<Business> spec = null;

        for (String conjunction : conjunctions) {
            Specification<Business> newSpec = Specification.where(null);

            String[] terms = conjunction.split(AND_SPACE_REGEX); // Split by AND and spaces
            for (String term : terms) {
                //Remove quotes from quoted string, then search by full contents inside the quotes
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                    newSpec = newSpec.and(BusinessSpecifications.hasCountry(term));
                } else {
                    newSpec = newSpec.and(BusinessSpecifications.hasCountry(term))
                            .or(BusinessSpecifications.containsCountry(term));
                }
            }
            if (spec == null) {
                spec = newSpec;
            } else {
                spec = spec.or(newSpec);
            }
        }

        //Get businesses with country matching and add their id's to spec
        List<Business> businesses = businessRepository.findAll(spec);

        Specification<SaleListing> listingSpec = null;
        for (Business business : businesses) {
            if (listingSpec == null) {
                listingSpec = SaleListingSpecifications.isBusinessId(business.getId());
            } else {
                listingSpec = listingSpec.or(SaleListingSpecifications.isBusinessId(business.getId()));
            }
        }

        return listingSpec;
    }

    /**
     * Searches for sales listings by business name
     *
     * @param conjunctions list of strings representing conjunctive search terms
     * @return specification for querying the JPA repository of sale listings with
     */
    public Specification<SaleListing> searchByBusinessName(String[] conjunctions) {
        Specification<Business> businessSpec = null;

        for (String conjunction : conjunctions) {
            Specification<Business> newSpec = Specification.where(null);

            String[] terms = conjunction.split(AND_SPACE_REGEX);
            for (String term : terms) {
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                    newSpec = newSpec.and(BusinessSpecifications.hasName(term));
                } else {
                    newSpec = newSpec.and(BusinessSpecifications.hasName(term)
                            .or(BusinessSpecifications.containsName(term)));
                }
            }
            if (businessSpec == null) {
                businessSpec = newSpec;
            } else {
                businessSpec = businessSpec.or(newSpec);
            }
        }

        Specification<SaleListing> spec = null;
        List<Business> businesses = businessRepository.findAll(businessSpec);
        for (Business business : businesses) {
            if (spec == null) {
                spec = SaleListingSpecifications.isBusinessId(business.getId());
            } else {
                spec = spec.or(SaleListingSpecifications.isBusinessId(business.getId()));
            }
        }
        return Objects.requireNonNullElseGet(spec, () -> SaleListingSpecifications.isBusinessId(-1));
    }
}
