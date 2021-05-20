package org.seng302.project.util;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.seng302.project.MainApplicationRunner;
import org.seng302.project.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Class containing functions to populate test database with test data.
 * Only used for development.
 */
@Component
public class TestDataRunner {

    private static final Logger logger = LoggerFactory.getLogger(MainApplicationRunner.class.getName());
    private static final JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SaleListingRepository saleListingRepository;
    private final CardRepository cardRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public TestDataRunner(UserRepository userRepository, BusinessRepository businessRepository, AddressRepository addressRepository,
                          ProductRepository productRepository, InventoryItemRepository inventoryItemRepository,
                          SaleListingRepository saleListingRepository, CardRepository cardRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.addressRepository = addressRepository;
        this.saleListingRepository = saleListingRepository;
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Function that populates repositories with test data.
     *
     * @throws FileNotFoundException if the test_data.json cannot be found.
     * @throws ParseException if test_data.json cannot be correctly parsed.
     */
    public void insertTestData() throws FileNotFoundException, ParseException {
        JSONObject data = (JSONObject) parser.parse(new FileReader("./src/main/resources/test_data.json"));
        // Insert test user data.
        if (userRepository.count() == 0) {
            insertTestUsers((JSONArray) data.get("users"));
        }
        // Insert test business data.
        if (businessRepository.count() == 0) {
            insertTestBusinesses((JSONArray) data.get("businesses"));
        }
        // Insert test product data.
        if (productRepository.count() == 0) {
            insertTestProducts((JSONArray) data.get("products"));
        }
        // Insert test inventoryItem data.
        if (inventoryItemRepository.count() == 0) {
            insertTestInventoryItems((JSONArray) data.get("inventoryItems"));
        }
        // Insert test saleListing data.
        if (saleListingRepository.count() == 0) {
            insertTestSaleListings((JSONArray) data.get("saleListings"));
        }
        if (cardRepository.count() == 0) {
            insertTestCards((JSONArray) data.get("cards"));
        }
    }

    /**
     * Inserts test user data to the database.
     *
     * @param userData JSONArray containing user data.
     */
    public void insertTestUsers(JSONArray userData) {
        logger.info("Adding sample data to user repository");
        for (Object object : userData) {
            JSONObject jsonUser = (JSONObject) object;
            JSONObject jsonAddress = (JSONObject) jsonUser.get("homeAddress");
            Address address = new Address(
                    jsonAddress.getAsString("streetNumber"),
                    jsonAddress.getAsString("streetName"),
                    jsonAddress.getAsString("city"),
                    jsonAddress.getAsString("region"),
                    jsonAddress.getAsString("country"),
                    jsonAddress.getAsString("postcode")
            );
            addressRepository.save(address);
            User newUser = new User(
                    jsonUser.getAsString("firstName"),
                    jsonUser.getAsString("lastName"),
                    jsonUser.getAsString("middleName"),
                    jsonUser.getAsString("nickname"),
                    jsonUser.getAsString("bio"),
                    jsonUser.getAsString("email"),
                    jsonUser.getAsString("dateOfBirth"),
                    jsonUser.getAsString("phoneNumber"),
                    address,
                    (passwordEncoder.encode(jsonUser.getAsString("password")))
            );
            userRepository.save(newUser);
        }

        logger.info("Finished adding sample data to user repository");
        logger.info(String.format("Added %d entries to user repository", userRepository.count()));
    }

    /**
     * Inserts test business data to the database.
     *
     * @param businessData JSONArray containing business data.
     */
    public void insertTestBusinesses(JSONArray businessData) {
        logger.info("Adding sample data to business repository");
        for (Object object : businessData) {
            JSONObject jsonBusiness = (JSONObject) object;
            JSONObject jsonAddress = (JSONObject) jsonBusiness.get("address");
            Address address = new Address(
                    jsonAddress.getAsString("streetNumber"),
                    jsonAddress.getAsString("streetName"),
                    jsonAddress.getAsString("city"),
                    jsonAddress.getAsString("region"),
                    jsonAddress.getAsString("country"),
                    jsonAddress.getAsString("postcode")
            );
            addressRepository.save(address);
            Integer primaryAdminId = jsonBusiness.getAsNumber("primaryAdministratorId").intValue();
            Business testBusiness = new Business(
                    jsonBusiness.getAsString("name"),
                    jsonBusiness.getAsString("description"),
                    address,
                    jsonBusiness.getAsString("businessType"),
                    primaryAdminId
            );
            businessRepository.save(testBusiness);

            testBusiness.addAdministrator(userRepository.getOne(primaryAdminId));

            JSONArray admins = (JSONArray) jsonBusiness.get("admins");
            for (Object admin : admins) {
                Integer adminId = Integer.parseInt(admin.toString());
                testBusiness.addAdministrator(userRepository.getOne(adminId));
            }

            businessRepository.save(testBusiness);
        }

        logger.info("Finished adding sample data to business repository");
        logger.info(String.format("Added %d entries to business repository", businessRepository.count()));
    }

    /**
     * Inserts test product data to the database.
     *
     * @param productData JSONArray of product data.
     */
    public void insertTestProducts(JSONArray productData) {
        logger.info("Adding sample data to product repository");
        for (Object object : productData) {
            JSONObject jsonProduct = (JSONObject) object;
            Product testProduct = new Product(
                    jsonProduct.getAsString("id"),
                    jsonProduct.getAsString("name"),
                    jsonProduct.getAsString("description"),
                    jsonProduct.getAsString("manufacturer"),
                    jsonProduct.getAsNumber("recommendedRetailPrice") != null ?
                            jsonProduct.getAsNumber("recommendedRetailPrice").doubleValue() : null,
                    jsonProduct.getAsNumber("businessId").intValue()
            );
            productRepository.save(testProduct);
        }
        logger.info("Finished adding sample data to product repository");
        logger.info(String.format("Added %d entries to product repository", productRepository.count()));
    }

    /**
     * Inserts test inventory item data to the database.
     *
     * @param inventoryData JSONArray of inventory data.
     */
    public void insertTestInventoryItems(JSONArray inventoryData) {
        logger.info("Adding sample data to inventory item repository");
        for (Object object : inventoryData) {
            JSONObject jsonInventoryItem = (JSONObject) object;
            Optional<Product> testProductOptions = productRepository.findByIdAndBusinessId(
                    jsonInventoryItem.getAsString("productId"),
                    jsonInventoryItem.getAsNumber("businessId").intValue());
            if (testProductOptions.isPresent()) {
                Product testProduct = testProductOptions.get();
                InventoryItem testInventoryItem = new InventoryItem(
                        testProduct,
                        jsonInventoryItem.getAsNumber("quantity").intValue(),
                        jsonInventoryItem.getAsNumber("pricePerItem") != null ?
                                jsonInventoryItem.getAsNumber("pricePerItem").doubleValue() : null,
                        jsonInventoryItem.getAsNumber("totalPrice") != null ?
                                jsonInventoryItem.getAsNumber("totalPrice").doubleValue() : null,
                        jsonInventoryItem.getAsString("manufactured"),
                        jsonInventoryItem.getAsString("sellBy"),
                        jsonInventoryItem.getAsString("bestBefore"),
                        jsonInventoryItem.getAsString("expires")
                );
                inventoryItemRepository.save(testInventoryItem);
            }
        }
        logger.info("Finished adding sample data to inventory item repository");
        logger.info(String.format("Added %d entries to inventory item repository", inventoryItemRepository.count()));
    }

    /**
     * Inserts test sale listing data to the database.
     *
     * @param saleData JSONArray of sale listing data.
     */
    public void insertTestSaleListings(JSONArray saleData) {
        logger.info("Adding sample data to sale listing repository");
        for (Object object : saleData) {
            JSONObject jsonSaleListing = (JSONObject) object;
            Optional<InventoryItem> testItemOptions = inventoryItemRepository.findById(jsonSaleListing.getAsNumber("inventoryItemId").intValue());
            if (testItemOptions.isPresent()) {
                InventoryItem testItem = testItemOptions.get();
                SaleListing testListing = new SaleListing(
                        jsonSaleListing.getAsNumber("businessId").intValue(),
                        testItem,
                        jsonSaleListing.getAsNumber("price").doubleValue(),
                        jsonSaleListing.getAsString("moreInfo"),
                        LocalDateTime.parse(jsonSaleListing.getAsString("closes"), DateTimeFormatter.ISO_DATE_TIME),
                        jsonSaleListing.getAsNumber("quantity").intValue()
                );
                saleListingRepository.save(testListing);
            }

        }
        logger.info("Finished adding sample data to sale listing repository");
        logger.info(String.format("Added %d entries to sale listing repository", saleListingRepository.count()));
    }

    /**
     * Inserts test card data to the database.
     *
     * @param cardsData JSONArray of sale listing data.
     */
    public void insertTestCards(JSONArray cardsData) {
        logger.info("Adding sample data to card repository");

        for (Object object : cardsData) {
            JSONObject jsonCard = (JSONObject) object;
            Optional<User> testUserOptions = userRepository.findById(jsonCard.getAsNumber("creatorId").intValue());
            if (testUserOptions.isPresent()) {
                User testUser = testUserOptions.get();

                Card testCard = new Card(
                        testUser,
                        jsonCard.getAsString("section"),
                        jsonCard.getAsString("title"),
                        jsonCard.getAsString("description")
                );


                cardRepository.save(testCard);
            }

        }
        logger.info("Finished adding sample data to card repository");
        logger.info(String.format("Added %d entries to card repository", cardRepository.count()));
    }

}
