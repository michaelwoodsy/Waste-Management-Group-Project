package org.seng302.project.service_layer.service;

import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.model.enums.Tag;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.repository_layer.specification.LikedSaleListingSpecification;
import org.seng302.project.repository_layer.specification.SaleListingSpecifications;
import org.seng302.project.service_layer.dto.sale_listings.GetSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.PostSaleListingDTO;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class SaleListingService {

    private static final Logger logger = LoggerFactory.getLogger(SaleListingService.class.getName());
    private static final String AND_SPACE_REGEX = "( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String QUOTE_REGEX = "^\".*\"$";

    private final UserService userService;
    private final BusinessService businessService;

    private final SaleListingRepository saleListingRepository;
    private final LikedSaleListingRepository likedSaleListingRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;
    private final UserNotificationRepository userNotificationRepository;

    @Autowired
    public SaleListingService(UserService userService,
                              BusinessService businessService,
                              SaleListingRepository saleListingRepository,
                              LikedSaleListingRepository likedSaleListingRepository,
                              SaleHistoryRepository saleHistoryRepository,
                              InventoryItemRepository inventoryItemRepository,
                              UserRepository userRepository,
                              UserNotificationRepository userNotificationRepository) {
        this.saleListingRepository = saleListingRepository;
        this.likedSaleListingRepository = likedSaleListingRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.userRepository = userRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.userService = userService;
        this.businessService = businessService;
    }

    /**
     * Helper method to convert a list of listings to a list of GetSaleListingDTOs, with the liked count attached
     * @param listings  listings to convert
     * @param user      currently logged-in user (used to check if they like a listing)
     * @return a list of GetSaleListingDTO Objects
     */
    List<GetSaleListingDTO> getListingDTOs(List<SaleListing> listings, User user) {
        List<GetSaleListingDTO> listingDTOs = new ArrayList<>();
        for (SaleListing listing : listings) {
            // Get like and star data for the listings
            Integer likes = likedSaleListingRepository.findAllByListing(listing).size();
            List<LikedSaleListing> likedSaleListings = likedSaleListingRepository.findByListingAndUser(listing, user);
            var userLikes = !likedSaleListings.isEmpty();

            // Create DTO and annotate with like and star data
            var dto = new GetSaleListingDTO(listing);
            dto.attachLikeData(likes, userLikes);
            listingDTOs.add(dto);
        }
        return listingDTOs;
    }

    /**
     * Gets a list of sale listings for a business.
     *
     * @param businessId Business to get the sale listings from.
     * @param appUser    The user that made the request.
     * @return List of sale listings.
     */
    public List<GetSaleListingDTO> getBusinessListings(Integer businessId, AppUserDetails appUser) {
        // Get the user that made the request
        var user = userService.getUserByEmail(appUser.getUsername());

        logger.info("User with id {} trying to get sale listings of business with id {}.", user.getId(), businessId);

        // To check the business exists
        businessService.checkBusiness(businessId);

        // Get the sale listings of the business
        List<SaleListing> listings = saleListingRepository.findAllByBusinessId(businessId);

        return getListingDTOs(listings, user);
    }

    /**
     * Converts date from string to LocalDateTime
     * and checks it's a valid closing date
     *
     * @param closesDateString the closing date in string format
     * @return the closing date in LocalDateTime format
     */
    private LocalDateTime getClosesDateTime(String closesDateString, InventoryItem item) {
        LocalDateTime closesDateTime;
        try {
            if (closesDateString != null && !closesDateString.equals("")) {
                //Closes string should be in the format: "yyyy-mm-ddThh:mm:ss.sssZ", e.g: "2021-05-29T04:34:55.931Z"
                closesDateTime = LocalDateTime.parse(closesDateString, DateTimeFormatter.ISO_DATE_TIME);

                //Check if closes date is in the past
                if ((LocalDateTime.now()).isAfter(closesDateTime)) {
                    var exception = new BadRequestException("Closing date must be in the future.");
                    logger.warn(exception.getMessage());
                    throw exception;
                }
            } else {
                var formatter = new SimpleDateFormat("yyyy-MM-dd");
                var expiryDate = formatter.parse(item.getExpires());
                closesDateTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
        } catch (DateTimeParseException | ParseException parseException) {
            var invalidDateException = new InvalidDateException();
            logger.warn(invalidDateException.getMessage());
            throw invalidDateException;
        }
        return closesDateTime;

    }


    /**
     * Adds a new sale listing to a business.
     *
     * @param requestDTO DTO containing fields for the new sale listing
     * @param businessId Business to get the sale listings from.
     * @param appUser    The user that made the request.
     */
    public void newBusinessListing(PostSaleListingDTO requestDTO, Integer businessId, AppUserDetails appUser) {
        // Get the user that made the request
        var user = userService.getUserByEmail(appUser.getUsername());

        logger.info("User with id {} trying to get sale listings of business with id {}.", user.getId(), businessId);

        // Get the business of the request
        var business = businessService.checkBusiness(businessId);

        // Check the user is an admin of the business
        businessService.checkUserCanDoBusinessAction(appUser, business);

        Integer inventoryItemId = requestDTO.getInventoryItemId();
        //Check if inventory item exists in businesses inventory items
        Optional<InventoryItem> retrievedItemOptions = inventoryItemRepository.findById(inventoryItemId);
        if (retrievedItemOptions.isEmpty()) {
            var exception = new BadRequestException(String.format(
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
        for (SaleListing listing : listings) {
            quantityUsed += listing.getQuantity();
        }
        //Check if there is enough of the inventory item
        if (quantity > (item.getQuantity() - quantityUsed)) {
            var exception = new BadRequestException(
                    String.format(
                            "You do not have enough of item with id %d for this listing (you have %d, with %d used in other sale listings).",
                            inventoryItemId, item.getQuantity() - quantityUsed, quantityUsed));
            logger.warn(exception.getMessage());
            throw exception;
        }

        Double price = requestDTO.getPrice();
        String moreInfo = requestDTO.getMoreInfo();
        var closesDateString = requestDTO.getCloses();
        LocalDateTime closesDateTime = getClosesDateTime(closesDateString, item);

        var saleListing = new SaleListing(business, item, price, moreInfo, closesDateTime, quantity);
        saleListingRepository.save(saleListing);
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
    public List<Object> searchSaleListings(SearchSaleListingsDTO dto, AppUserDetails appUser) {
        // Get the user that made the request
        var user = userService.getUserByEmail(appUser.getUsername());

        List<SaleListing> listings;
        long totalCount;
        String searchQuery = dto.getSearchQuery().toLowerCase(); // Convert search query to all lowercase.
        String[] conjunctions = searchQuery.split(" or (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by OR

        Specification<SaleListing> spec = buildListingSpec(dto, conjunctions);

        var sort = buildListingSort(dto.getSortBy());

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

        var listingDTOs = getListingDTOs(listings, user);

        return Arrays.asList(listingDTOs, totalCount);
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
            var formatter = new SimpleDateFormat("yyyy-MM-dd");
            if (dto.getClosingDateLower() != null) {
                var date = formatter.parse(dto.getClosingDateLower());
                closingDateLower = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            if (dto.getClosingDateUpper() != null) {
                var date = formatter.parse(dto.getClosingDateUpper());
                closingDateUpper = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            spec = spec.and(searchClosesInBetween(closingDateLower, closingDateUpper));
        } catch (ParseException parseException) {
            var invalidDateException = new InvalidDateException();
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
     * Retrieves the sale listing.
     * Throws a NotAcceptableException if the listing is not found
     * @param listingId the id of the listing to retrieve
     * @return the retrieved SaleListing
     */
    private SaleListing retrieveListing(Integer listingId) {
        Optional<SaleListing> listing = saleListingRepository.findById(listingId);

        if (listing.isEmpty()) {
            var message = String.format("No sale listing with ID %d exists", listingId);
            logger.warn(message);
            throw new NotAcceptableException(message);
        }

        return listing.get();
    }

    /**
     * Retrieves the LikedSaleListing.
     * Throws a BadRequestException if the user had not liked the listing
     * @param listing the listing the user has liked
     * @param loggedInUser the user that has liked the listing
     * @return the retrieved LikedSaleListing
     */
    private LikedSaleListing retrieveLikedSaleListing(SaleListing listing, User loggedInUser) {
        List<LikedSaleListing> result = likedSaleListingRepository.findByListingAndUser(listing, loggedInUser);

        if (result.isEmpty()) {
            var message = String.format("User with ID %d has not liked sale listing with ID %d", loggedInUser.getId(), listing.getId());
            logger.warn(message);
            throw new BadRequestException(message);
        }

        return result.get(0);
    }

    /**
     * Likes a sale listing if it is liked by a user
     *
     * @param listingId ID of the sale listing to like
     * @param user      User who is liking the sale listing
     */
    @Transactional
    public void likeSaleListing(Integer listingId, AppUserDetails user) {
        // Get the logged in user from the users email
        var loggedInUser = userService.getUserByEmail(user.getUsername());

        SaleListing listing = retrieveListing(listingId);

        //Check that the user hasn't already liked the sale listing
        if (likedSaleListingRepository.findByListingAndUser(listing, loggedInUser).isEmpty()) {
            //Make the new liked sale listing
            var likedSaleListing = new LikedSaleListing(loggedInUser, listing);
            //Save the liked sale listing
            loggedInUser.addLikedListing(likedSaleListing);
            //Add liked sale listing to the list of liked sale listings of user
            userRepository.save(loggedInUser);
        } else {
            var message = String.format("User with ID %d has not liked sale listing with ID %d", loggedInUser.getId(), listingId);
            logger.warn(message);
            throw new BadRequestException(message);
        }
    }

    /**
     * Unlikes a sale listing if it is liked by a user
     *
     * @param listingId ID of the sale listing to unlike
     * @param user      User who is unliking the sale listing
     */
    @Transactional
    public void unlikeSaleListing(Integer listingId, AppUserDetails user) {
        var loggedInUser = userService.getUserByEmail(user.getUsername());

        SaleListing listing = retrieveListing(listingId);

        var likedSaleListing = retrieveLikedSaleListing(listing, loggedInUser);
        loggedInUser.removeLikedListing(likedSaleListing);
        userRepository.save(loggedInUser);
    }

    /**
     * Service method to purchase a sales listing. This method:
     * updates the sellers inventory
     * removes the sales listing
     * records the sale in sales history
     *
     * @param listingId Sales Listing ID to purchase
     * @param appUser   User purchasing the sales listing
     */
    public void buySaleListing(Integer listingId, AppUserDetails appUser) {
        var buyer = userService.getUserByEmail(appUser.getUsername());

        SaleListing listing = retrieveListing(listingId);

        logger.info("User with ID: {} Request to buy Sale Listing with ID: {}", buyer.getId(), listing.getId());

        //Record the sale
        var sale = new Sale(listing);
        saleHistoryRepository.save(sale);

        //Update the inventory items quantity or remove it if its new quantity is 0
        updateInventoryItem(listing);
        //Send notifications to the users who liked the listing saying it was brought
        sendPurchaseNotifications(listing, buyer);

        //Remove the sales listing
        saleListingRepository.delete(listing);
    }

    /**
     * Helper method of the buySaleListing method that updates a sales listings inventory item after it has been purchased
     * lowers the quantity of the inventory item, and removes the inventory item if the quantity is at or below zero
     *
     * @param listing listing purchased
     */
    private void updateInventoryItem(SaleListing listing) {
        var inventoryItem = listing.getInventoryItem();
        inventoryItem.setQuantity(inventoryItem.getQuantity() - listing.getQuantity());
        inventoryItemRepository.save(inventoryItem);

        //Remove the inventory item if the quantity is 0
        if (inventoryItem.getQuantity() <= 0) {
            //Check if sale listings exist for current inventory item wanting to be deleted (there shouldn't be..)
            removeSaleListings(listing.getBusiness().getId(), inventoryItem);
            inventoryItemRepository.delete(inventoryItem);
        }
    }

    /**
     * Helper method for the updateInventoryItem method, this is sort of a contingency method that removes
     * sale listings for an inventory item that has a quantity of 0, which shouldn't happen.
     *
     * @param businessId    Id of the business with the inventory item
     * @param inventoryItem inventory item being removed
     */
    private void removeSaleListings(Integer businessId, InventoryItem inventoryItem) {
        var saleListings = saleListingRepository.findAllByBusinessIdAndInventoryItemId(
                businessId, inventoryItem.getId());
        for (var saleListing : saleListings) {
            List<LikedSaleListing> likes = likedSaleListingRepository.findAllByListing(saleListing);
            for (var like : likes) {
                var user = like.getUser();
                user.removeLikedListing(like);
                userRepository.save(user);
                likedSaleListingRepository.delete(like);
            }
            saleListingRepository.delete(saleListing);
        }
    }

    /**
     * Helper method of the buySaleListing method that sends a notification to the purchaser of the sale listing
     * and notifications to the users that liked the sale listing
     *
     * @param listing listing purchased
     * @param buyer   user who purchased the listing
     */
    private void sendPurchaseNotifications(SaleListing listing, User buyer) {
        //Send notification to buyer
        var purchaseNotification = new PurchaserNotification(buyer, listing, listing.getBusiness());
        userNotificationRepository.save(purchaseNotification);

        //Send notifications to interested users
        List<LikedSaleListing> likes = likedSaleListingRepository.findAllByListing(listing);
        for (var like : likes) {
            //Make sure not to send this notification to the buyer
            if (!like.getUser().getId().equals(buyer.getId())) {
                var interestedUserNotification = new InterestedUserNotification(like.getUser(), like.getListing());
                userNotificationRepository.save(interestedUserNotification);
            }
            var user = like.getUser();
            user.removeLikedListing(like);
            userRepository.save(user);
            likedSaleListingRepository.delete(like);
        }
    }

    /**
     * Tags a user's liked sale listing
     * @param listingId the id of the listing to tag
     * @param tagName the name of the tag for the listing
     * @param user the AppUserDetails of the user tagging the listing
     */
    public void tagSaleListing(Integer listingId,
                               String tagName,
                               AppUserDetails user) {
        logger.info("Request to tag a sale listing with ID: {}", listingId);
        if (!Tag.checkTag(tagName)) {
            var badRequestException = new BadRequestException(String.format("%s is not a valid tag.", tagName));
            logger.warn(badRequestException.getMessage());
            throw badRequestException;
        }

        var loggedInUser = userService.getUserByEmail(user.getUsername());
        SaleListing listing = retrieveListing(listingId);
        var likedSaleListing = retrieveLikedSaleListing(listing, loggedInUser);
        var tag = Tag.getTag(tagName);
        likedSaleListing.setTag(tag);
        likedSaleListingRepository.save(likedSaleListing);
    }

    /**
     * Stars user's liked sale listing
     * @param listingId the id of the listing to star
     * @param user the AppUserDetails of the user starring the listing
     */
    public void starSaleListing(Integer listingId,
                                boolean star,
                                AppUserDetails user){
        logger.info("Request to star a sale listing with ID: {}", listingId);
        var loggedInUser = userService.getUserByEmail(user.getUsername());
        SaleListing listing = retrieveListing(listingId);
        var likedSaleListing = retrieveLikedSaleListing(listing, loggedInUser);
        likedSaleListing.setStarred(star);
        likedSaleListingRepository.save(likedSaleListing);
    }

    /**
     * Retrieves the popular sale listings from the specified country,
     * if no country is specified then it retrieves the popular sale listings worldwide.
     * @param country country to get popular listings for.
     * @return List of the up to 10 most popular sale listings in GetSaleListingDTOs'.
     */
    public List<GetSaleListingDTO> getPopularListings(String country) {
        List<List<Object>> response;
        if (country == null) {
            response = likedSaleListingRepository.findPopular(PageRequest.of(0, 10));
        } else {
            response = likedSaleListingRepository.findPopularByCountry(country, PageRequest.of(0, 10));
        }
        List<GetSaleListingDTO> listings = new ArrayList<>();
        for (List<Object> listing: response) {
            //Making sure that the Objects are the right class
            if (listing.get(0).getClass() == SaleListing.class && listing.get(1).getClass() == Long.class) {
                GetSaleListingDTO dto = new GetSaleListingDTO((SaleListing) listing.get(0));
                Long numLikes = (Long) listing.get(1);
                dto.attachLikeData(numLikes.intValue(), false);
                listings.add(dto);
            }
        }
        return listings;
    }

    /**
     * Deletes sale listings that have expired.
     * Scheduled to run at midnight every day.
     */
    @Scheduled(cron = "@midnight")
    public void deleteExpiredSaleListings() {
        // create specification for getting LikedSaleListings of listings that are expired
        var now = LocalDateTime.now();
        Specification<LikedSaleListing> spec = LikedSaleListingSpecification.saleListingClosesBefore(now);

        // find expired saleListings and likedSaleListings
        List<SaleListing> expiredListings = saleListingRepository.findAllByClosesBefore(now);
        List<LikedSaleListing> expiredLikedListings = likedSaleListingRepository.findAll(spec);

        // delete all that are expired
        if (!expiredLikedListings.isEmpty()) {
            likedSaleListingRepository.deleteAll(expiredLikedListings);
        }
        saleListingRepository.deleteAll(expiredListings);

        // logging
        var logMessage = String.format("Deleted %d expired sales listings", expiredListings.size());
        logger.info(logMessage);
    }

    /**
     * Features a business' sale listing
     * @param listingId The ID of the Sale Listing you are trying to feature
     * @param businessId The Business ID who has the Sale Listing
     * @param featured The new value of the featured field. True or False
     * @param user The User who is trying to feature the Sale Listing
     */
    public void featureSaleListing(Integer listingId,
                                   Integer businessId,
                                   boolean featured,
                                   AppUserDetails user){
        Integer maxNumberFeature = 5;
        logger.info("Request to feature a sale listing with ID: {}", listingId);
        // Get the business of the request
        Business business = businessService.checkBusiness(businessId);

        // Check the user is an admin of the business
        businessService.checkUserCanDoBusinessAction(user, business);

        List<SaleListing> businessListings = saleListingRepository.findAllByBusinessId(businessId);
        Integer currentlyFeatured = 0;
        for (SaleListing businessListing : businessListings){
            if(currentlyFeatured.equals(maxNumberFeature) && featured){
                throw new BadRequestException("You already have the maximum number of possible featured sale listings");
            }
            if(businessListing.isFeatured()){
                currentlyFeatured++;
            }
        }
        SaleListing listing = retrieveListing(listingId);
        listing.setFeatured(featured);
        saleListingRepository.save(listing);

    }
}
