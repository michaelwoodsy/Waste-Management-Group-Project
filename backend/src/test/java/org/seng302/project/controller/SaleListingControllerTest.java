package org.seng302.project.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration
public class SaleListingControllerTest {

    // Test users
    private User user;
    private String userEmail = "basicUser@gmail.com";
    private String userPassword = "123";
    private User owner;
    private String ownerEmail = "ownerUser@gmail.com";
    private String ownerPassword = "123";

    // Test business
    private Business business;

    // Test product
    private Product product;

    // Test inventory
    private InventoryItem inventoryItem;

    // Test address
    private Address testAddress1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private AddressRepository addressRepository;

    /**
     * Creates the user if it's not already created.
     * If it is already created, the user is returned.
     * @return User
     */
    private User createUser(User wantedUser) {
        if (userRepository.findByEmail(wantedUser.getEmail()).size() > 0) {
            // Already exists, return it
            return(userRepository.findByEmail(wantedUser.getEmail()).get(0));
        } else {
            // User doesn't exist, save it to repository
            wantedUser.setPassword(passwordEncoder.encode(wantedUser.getPassword()));
            userRepository.save(wantedUser);
            return wantedUser;
        }
    }

    @BeforeEach
    public void initialise() {
        inventoryItemRepository.deleteAll();

        // Create address
        testAddress1 = new Address();
        addressRepository.save(testAddress1);

        // Create the users
        user = createUser(new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", userEmail, "1999-04-27",
                "+64 3 555 0129", testAddress1, userPassword));
        owner = createUser(new User("Jane", "Smith", "Rose", "Jonny",
                "Likes long walks on the beach", ownerEmail, "1999-04-27",
                "+64 3 555 0120", testAddress1, ownerPassword));

        // Create the business
        business = new Business("Business", "A Business", testAddress1, "Retail",
                owner.getId());
        businessRepository.save(business);

        // Create a product
        product = new Product("p1", "Watties Beans", "beans in a can", "Watties", 2.00,
                business.getId());
        productRepository.save(product);
    }

    @AfterEach
    public void tearDown() {
        productRepository.delete(product);
        businessRepository.delete(business);
        userRepository.delete(user);
        userRepository.delete(owner);
        addressRepository.delete(testAddress1);
    }

    @Test
    public void sanityCheck() {
        assertNotNull("hi");
    }
}
