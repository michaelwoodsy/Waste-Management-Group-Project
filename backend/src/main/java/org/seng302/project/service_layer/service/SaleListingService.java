package org.seng302.project.service_layer.service;


import org.seng302.project.repository_layer.model.LikedSaleListing;
import org.seng302.project.repository_layer.model.SaleListing;
import org.seng302.project.repository_layer.repository.LikedSaleListingRepository;
import org.seng302.project.repository_layer.repository.SaleListingRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.repository_layer.specification.SaleListingSpecifications;
import org.seng302.project.service_layer.dto.sale_listings.GetSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.SearchSaleListingsDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.InvalidDateException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SaleListingService {

    private static final Logger logger = LoggerFactory.getLogger(SaleListingService.class.getName());
    private static final String AND_SPACE_REGEX = "( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String QUOTE_REGEX = "^\".*\"$";

    private final SaleListingRepository saleListingRepository;
    private final LikedSaleListingRepository likedSaleListingRepository;
    private final UserRepository userRepository;

    @Autowired
    public SaleListingService(SaleListingRepository saleListingRepository,
                              LikedSaleListingRepository likedSaleListingRepository, UserRepository userRepository) {
        this.saleListingRepository = saleListingRepository;
        this.likedSaleListingRepository = likedSaleListingRepository;
        this.userRepository = userRepository;
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
        List<SaleListing> listings;
        long totalCount;
        String searchQuery = dto.getSearchQuery().toLowerCase(); // Convert search query to all lowercase.
        String[] conjunctions = searchQuery.split(" or (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by OR

        Specification<SaleListing> spec = buildListingSpec(dto, conjunctions);

        Sort sort = buildListingSort(dto.getSortBy());

        Pageable pageable;
        if (sort != null) {
            pageable = PageRequest.of(dto.getPageNumber(), 10, sort);
        } else {
            pageable = PageRequest.of(dto.getPageNumber(), 10);
        }

        Page<SaleListing> page = saleListingRepository.findAll(spec, pageable);
        totalCount = page.getTotalElements();
        listings = page.getContent();

        logger.info("Retrieved {} Sales Listings, showing {}", totalCount, listings.size());

        return Arrays.asList(listings.stream().map(GetSaleListingDTO::new).collect(Collectors.toList()), totalCount);
    }

    /**
     * Method which builds the specification used to search sale listings with.
     *
     * @param dto          DTO containing parameters to search by
     * @param conjunctions Search query split by AND keyword
     * @return Spec used to search sale listing repository by
     */
    private Specification<SaleListing> buildListingSpec(SearchSaleListingsDTO dto, String[] conjunctions) {
        Specification<SaleListing> spec = null;

        //Product name
        if (dto.isMatchProductName()) {
            //Always first
            spec = searchNameField(conjunctions);
        }

        //Business name
        if (dto.isMatchBusinessName()) {
            if (spec == null) {
                spec = searchByBusinessName(conjunctions);
            } else {
                spec = spec.or(searchByBusinessName(conjunctions));
            }
        }

        //Business location
        if (dto.isMatchBusinessLocation()) {
            if (spec == null) {
                spec = searchByBusinessCountry(conjunctions);
            } else {
                spec = spec.or(searchByBusinessCountry(conjunctions));
            }
        }

        //Business type
        if (dto.isMatchBusinessType()) {
            if (spec == null) {
                spec = searchByBusinessType(conjunctions);
            } else {
                spec = spec.or(searchByBusinessType(conjunctions));
            }
        }

        //Price range
        if (spec == null) {
            spec = searchPriceInBetween(dto.getPriceRangeLower(), dto.getPriceRangeUpper());
        } else {
            spec = spec.and(searchPriceInBetween(dto.getPriceRangeLower(), dto.getPriceRangeUpper()));
        }

        //Closing date range
        try {
            LocalDateTime closingDateLower = null;
            LocalDateTime closingDateUpper = null;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            if (dto.getClosingDateLower() != null) {
                Date date = formatter.parse(dto.getClosingDateLower());
                closingDateLower = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            if (dto.getClosingDateUpper() != null) {
                Date date = formatter.parse(dto.getClosingDateUpper());
                closingDateUpper = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            spec = spec.and(searchClosesInBetween(closingDateLower, closingDateUpper));
        } catch (ParseException parseException) {
            InvalidDateException invalidDateException = new InvalidDateException();
            logger.warn(invalidDateException.getMessage());
            throw invalidDateException;
        }

        return spec;
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
        Specification<SaleListing> spec = null;

        for (String conjunction : conjunctions) {
            Specification<SaleListing> newSpec = Specification.where(null);

            String[] terms = conjunction.split(AND_SPACE_REGEX); // Split by AND and spaces
            for (String term : terms) {
                //Remove quotes from quoted string, then search by full contents inside the quotes
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                    newSpec = newSpec.and(SaleListingSpecifications.hasCountry(term));
                } else {
                    newSpec = newSpec.and(SaleListingSpecifications.hasCountry(term))
                            .or(SaleListingSpecifications.containsCountry(term));
                }
            }
            if (spec == null) {
                spec = newSpec;
            } else {
                spec = spec.or(newSpec);
            }
        }
        return spec;
    }

    /**
     * Searches for sales listings by business name
     *
     * @param conjunctions list of strings representing conjunctive search terms
     * @return specification for querying the JPA repository of sale listings with
     */
    public Specification<SaleListing> searchByBusinessName(String[] conjunctions) {
        Specification<SaleListing> spec = null;

        for (String conjunction : conjunctions) {
            Specification<SaleListing> newSpec = Specification.where(null);

            String[] terms = conjunction.split(AND_SPACE_REGEX);
            for (String term : terms) {
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                    newSpec = newSpec.and(SaleListingSpecifications.hasBusinessName(term));
                } else {
                    newSpec = newSpec.and(SaleListingSpecifications.hasBusinessName(term)
                            .or(SaleListingSpecifications.containsBusinessName(term)));
                }
            }
            if (spec == null) {
                spec = newSpec;
            } else {
                spec = spec.or(newSpec);
            }
        }
        return spec;
    }

    /**
     * Searches for sales listings by business type
     *
     * @param conjunctions list of strings representing conjunctive search terms
     * @return specification for querying the JPA repository of sale listings with
     */
    public Specification<SaleListing> searchByBusinessType(String[] conjunctions) {
        Specification<SaleListing> spec = null;

        for (String conjunction : conjunctions) {
            Specification<SaleListing> newSpec = Specification.where(null);

            String[] terms = conjunction.split(AND_SPACE_REGEX);
            for (String term : terms) {
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                    newSpec = newSpec.and(SaleListingSpecifications.hasBusinessType(term));
                } else {
                    newSpec = newSpec.and(SaleListingSpecifications.hasBusinessType(term)
                            .or(SaleListingSpecifications.containsBusinessType(term)));
                }
            }
            if (spec == null) {
                spec = newSpec;
            } else {
                spec = spec.or(newSpec);
            }
        }
        return spec;
    }

    /**
     * Given a sort query string, returns a Sort object used to sort sale listings by.
     *
     * @param sortQuery String query to sort by.
     * @return Sort object used to sort entries retrieved from the Sale Listing Repository
     */
    private Sort buildListingSort(String sortQuery) {
        Sort sort = null;
        switch (sortQuery) {
            case "priceAsc":
                sort = Sort.by(Sort.Order.asc("price"));
                break;
            case "priceDesc":
                sort = Sort.by(Sort.Order.desc("price"));
                break;
            case "productName":
                sort = Sort.by(Sort.Order.asc("inventoryItem.product.name"));
                break;
            case "country":
                sort = Sort.by(Sort.Order.asc("business.address.country"));
                break;
            case "city":
                sort = Sort.by(Sort.Order.asc("business.address.city"));
                break;
            case "expiryDateAsc":
                sort = Sort.by(Sort.Order.asc("inventoryItem.expires"));
                break;
            case "expiryDateDesc":
                sort = Sort.by(Sort.Order.desc("inventoryItem.expires"));
                break;
            case "seller":
                sort = Sort.by(Sort.Order.asc("business.name"));
                break;
            default:
                break;
        }
        return sort;
    }

    /**
     * Likes a sale listing if it is liked by a user
     * @param listingId ID of the sale listing to like
     * @param user      User who is liking the sale listing
     */
    @Transactional
    public void likeSaleListing(Integer listingId, AppUserDetails user){
        // Get the logged in user from the users email
        String userEmail = user.getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        //Get Sale Listing from repository
        Optional<SaleListing> foundSaleListingOptional = saleListingRepository.findById(listingId);
        // Check if the listing exists
        if (foundSaleListingOptional.isEmpty()){
            throw new NotAcceptableException(String.format("There is no sale listing that exists with the id %d",
                    listingId));
        }
        SaleListing listing = foundSaleListingOptional.get();

        //Check that the user hasn't already liked the sale listing
        if (likedSaleListingRepository.findByListingAndUser(listing, loggedInUser).isEmpty()){
            //Make the new liked sale listing
            LikedSaleListing likedSaleListing = new LikedSaleListing(loggedInUser, listing);
            //Save the liked sale listing
            likedSaleListingRepository.save(likedSaleListing);
            //Add liked sale listing to the list of liked sale listings of user
            var currentlyLikedSaleListings = loggedInUser.getLikedSaleListings();
            currentlyLikedSaleListings.add(likedSaleListing);
            //Save the list to the user
            loggedInUser.setLikedSaleListings(currentlyLikedSaleListings);
            userRepository.save(loggedInUser);
        } else {
            throw new BadRequestException("This user has already liked this sale listing");
        }

    }
}
