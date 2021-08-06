package org.seng302.project.service_layer.service;

import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.InventoryItem;
import org.seng302.project.repository_layer.model.SaleListing;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.repository_layer.specification.SaleListingSpecifications;
import org.seng302.project.service_layer.dto.sale_listings.PostSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.GetSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.SearchSaleListingsDTO;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    public SaleListingService(SaleListingRepository saleListingRepository,
                              BusinessRepository businessRepository,
                              UserRepository userRepository,
                              InventoryItemRepository inventoryItemRepository) {
        this.saleListingRepository = saleListingRepository;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }


    /**
     * Returns the current logged in user that made the request.
     * @param appUser The AppUserDetails object passed in from the authentication principle.
     * @return User: the user that made the request.
     */
    private User getLoggedInUser(AppUserDetails appUser) {
        String userEmail = appUser.getUsername();
        return userRepository.findByEmail(userEmail).get(0);
    }

    /**
     * Gets a business with the provided id.
     * @param businessId The id of the business to look for.
     * @return The business with the corresponding id.
     * @throws BusinessNotFoundException Thrown if the business doesn't exist.
     */
    private Business getBusiness(Integer businessId) throws BusinessNotFoundException {
        // Get business from repository
        Optional<Business> foundBusiness = businessRepository.findById(businessId);

        // Check if the business exists
        if (foundBusiness.isEmpty()) {
            BusinessNotFoundException exception = new BusinessNotFoundException(businessId);
            logger.warn(exception.getMessage());
            throw exception;
        }

        // Return the found business
        return foundBusiness.get();
    }

    /**
     * Checks if the user is the owner or administrator of the business. Throws an exception if they aren't
     * @param user The user to check.
     * @param business The business to check.
     * @throws ForbiddenAdministratorActionException Thrown if the user isn't and owner or admin of the business.
     */
    private void checkUserIsAdminOfBusiness(User user, Business business) throws ForbiddenAdministratorActionException {
        // Check if the logged in user is the business owner / administrator or a GAA
        if (!(business.userIsAdmin(user.getId()) ||
                business.getPrimaryAdministratorId().equals(user.getId())) && !user.isGAA()) {
            ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(business.getId());
            logger.warn(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Gets a list of sale listings for a business.
     * @param businessId Business to get the sale listings from.
     * @param appUser The user that made the request.
     * @return List of sale listings.
     */
    public List<GetSaleListingDTO> getBusinessListings(Integer businessId, AppUserDetails appUser) {
        try {
            // Get the user that made the request
            User user = getLoggedInUser(appUser);

            logger.info("User with id {} trying to get sale listings of business with id {}.", user.getId(), businessId );

            // To check the business exists
            getBusiness(businessId);

            // Get the sale listings of the business
            List<SaleListing> listings = saleListingRepository.findAllByBusinessId(businessId);
            return listings.stream().map(GetSaleListingDTO::new).collect(Collectors.toList());

        } catch (BusinessNotFoundException | ForbiddenAdministratorActionException exception) {
            throw exception;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while getting business sale listings: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Converts date from string to LocalDateTime
     * and checks it's a valid closing date
     * @param closesDateString the closing date in string format
     * @return the closing date in LocalDateTime format
     */
    private LocalDateTime getClosesDateTime(String closesDateString, InventoryItem item) {
        LocalDateTime closesDateTime;
        try {
            if (closesDateString != null && !closesDateString.equals("")) {
                closesDateTime = LocalDateTime.parse(closesDateString, DateTimeFormatter.ISO_DATE_TIME);

                //Check if closes date is in the past
                if ((LocalDateTime.now()).isAfter(closesDateTime)) {
                    BadRequestException exception = new BadRequestException("Closing date must be in the future.");
                    logger.warn(exception.getMessage());
                    throw exception;
                }
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date expiryDate = formatter.parse(item.getExpires());
                closesDateTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
        } catch (DateTimeParseException | ParseException parseException) {
            InvalidDateException invalidDateException = new InvalidDateException();
            logger.warn(invalidDateException.getMessage());
            throw invalidDateException;
        } catch (BadRequestException handledException) {
            throw handledException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while parsing date: %s", exception.getMessage()));
            throw exception;
        }
        return closesDateTime;

    }


    /**
     * Adds a new sale listing to a business.
     * @param requestDTO DTO containing fields for the new sale listing
     * @param businessId Business to get the sale listings from.
     * @param appUser The user that made the request.
     */
    public void newBusinessListing(PostSaleListingDTO requestDTO, Integer businessId, AppUserDetails appUser) {
        try {

            // Get the user that made the request
            User user = getLoggedInUser(appUser);

            logger.info("User with id {} trying to get sale listings of business with id {}.", user.getId(), businessId);

            // Get the business of the request
            Business business = getBusiness(businessId);

            // Check the user is an admin of the business
            checkUserIsAdminOfBusiness(user, business);

            Integer inventoryItemId = requestDTO.getInventoryItemId();
            //Check if inventory item exists in businesses inventory items
            Optional<InventoryItem> retrievedItemOptions = inventoryItemRepository.findById(inventoryItemId);
            if (retrievedItemOptions.isEmpty()) {
                BadRequestException exception = new BadRequestException(String.format(
                        "No inventory item with id %d exists in business with id %d.",
                        inventoryItemId, businessId));
                logger.warn(exception.getMessage());
                throw exception;
            }
            InventoryItem item = retrievedItemOptions.get();


            Integer quantity = requestDTO.getQuantity();
            List<SaleListing> listings = saleListingRepository.findAllByBusinessIdAndInventoryItemId(businessId, inventoryItemId);

            //Calculates the quantity used of this Inventory item in other sales listings, if any
            Integer quantityUsed = 0;
            for(SaleListing listing: listings) {
                quantityUsed += listing.getQuantity();
            }
            //Check if there is enough of the inventory item
            if (quantity > (item.getQuantity() - quantityUsed)) {
                NotEnoughOfInventoryItemException exception = new NotEnoughOfInventoryItemException(inventoryItemId, item.getQuantity() - quantityUsed, quantityUsed);
                logger.warn(exception.getMessage());
                throw exception;
            }

            //Price
            Double price = requestDTO.getPrice();

            //More Info
            String moreInfo = requestDTO.getMoreInfo();

            //Closes
            //Closes string should be in the format: "yyyy-mm-ddThh:mm:ss.sssZ", e.g: "2021-05-29T04:34:55.931Z"
            String closesDateString = requestDTO.getCloses();

            LocalDateTime closesDateTime = getClosesDateTime(closesDateString, item);


            SaleListing saleListing = new SaleListing(business, item, price, moreInfo, closesDateTime, quantity);
            saleListingRepository.save(saleListing);

        } catch (BusinessNotFoundException | ForbiddenAdministratorActionException | NotEnoughOfInventoryItemException |
                BadRequestException |
                InvalidQuantityException | InvalidPriceException |
                InvalidDateException exception) {
            throw exception;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding sales listing: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
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
        Specification<SaleListing> spec = null;
        List<SaleListing> listings;
        long totalCount;
        String searchQuery = dto.getSearchQuery().toLowerCase(); // Convert search query to all lowercase.
        String[] conjunctions = searchQuery.split(" or (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by OR

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

        Sort sort = null;

        switch (dto.getSortBy()) {
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

        if (sort != null) {
            Pageable pageable = PageRequest.of(dto.getPageNumber(), 10, sort);
            Page<SaleListing> page = saleListingRepository.findAll(spec, pageable);
            totalCount = page.getTotalElements();
            listings = page.getContent();
        } else {
            Pageable pageable = PageRequest.of(dto.getPageNumber(), 10);
            Page<SaleListing> page = saleListingRepository.findAll(spec, pageable);
            totalCount = page.getTotalElements();
            listings = page.getContent();
        }

        logger.info("Retrieved {} Sales Listings, showing {}", totalCount, listings.size());

        return Arrays.asList(listings.stream().map(GetSaleListingDTO::new).collect(Collectors.toList()), totalCount);
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
}
