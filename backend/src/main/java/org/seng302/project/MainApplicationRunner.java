/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package org.seng302.project;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.seng302.project.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Optional;

/**
 * This spring component runs at application startup to do some initialisation
 * work.
 */
@Component
public class MainApplicationRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MainApplicationRunner.class.getName());
    private static final JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * This constructor is implicitly called by Spring (purpose of the @Autowired
     * annotation). Injected constructors can be supplied with instances of other
     * classes (i.e. dependency injection)
     *
     * @param userRepository the user repository to persist example data.
     */
    @Autowired
    public MainApplicationRunner(UserRepository userRepository, BusinessRepository businessRepository, AddressRepository addressRepository,
                                 ProductRepository productRepository, InventoryItemRepository inventoryItemRepository,
                                 BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * By overriding the run method, we tell Spring to run this code at startup. See
     * https://dzone.com/articles/spring-boot-applicationrunner-and-commandlinerunne
     */
    @Override
    public void run(ApplicationArguments args) throws FileNotFoundException, ParseException {
        logger.info("Startup application with {}", args);
        if (Constants.TEST_DATA) {
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
                    jsonInventoryItem.getAsString("productId"),3);
            if (testProductOptions.isPresent()) {
                Product testProduct = testProductOptions.get();
            InventoryItem testInventoryItem = new InventoryItem(
                    testProduct,
                    jsonInventoryItem.getAsNumber("quantity").intValue(),
                    jsonInventoryItem.getAsNumber("pricePerItem") != null ?
                            jsonInventoryItem.getAsNumber("pricePerItem").doubleValue(): null,
                    jsonInventoryItem.getAsNumber("totalPrice")!= null ?
                            jsonInventoryItem.getAsNumber("totalPrice").doubleValue(): null,
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

}
