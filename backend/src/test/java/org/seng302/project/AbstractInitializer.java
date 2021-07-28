package org.seng302.project;

import lombok.Data;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.web_layer.authentication.WebSecurityConfig;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
public abstract class AbstractInitializer {

    protected final BCryptPasswordEncoder passwordEncoder;

    private User testUser;
    private User testOtherUser;
    private User testSystemAdmin;
    private User testSystemDGAA;
    private User testUserBusinessAdmin;
    private Business testBusiness;
    private Product testProduct;
    private List<Card> testCards;
    private List<Keyword> testKeywords;
    private List<Image> testImages;
    private List<Message> testMessages;
    private MockMultipartFile testFile;
    private MockMultipartFile testImageFile;
    private Card testCard;
    private UserNotification testUserNotification;

    private List<SaleListing> saleListings;

    public AbstractInitializer() {
        WebSecurityConfig webSecurityConfig = new WebSecurityConfig();
        passwordEncoder = webSecurityConfig.passwordEncoder();
        this.initialise();
    }

    public void initialise() {
        this.initialiseTestUsers();
        this.initialiseTestSystemAdmin();
        this.initialiseTestSystemDGAA();
        this.initialiseTestCard();
        this.initialiseTestUserNotification();
        this.initialiseTestUserBusinessAdmin();
        this.initialiseTestBusiness();
        this.initialiseTestProduct();
        this.initialiseTestCards();
        this.initialiseTestImages();
        this.initialiseTestFiles();
        this.initialiseTestCard();
        this.initialiseTestUserNotification();
        this.initialiseTestMessages();
        this.initialiseTestSaleListings();
    }

    public void initialiseTestUsers() {
        Address address = new Address();
        address.setCountry("New Zealand");
        testUser = new User(
                "John",
                "Smith",
                "Hector",
                null,
                null,
                "john.smith@gmail.com",
                "1995-07-25",
                null,
                address,
                "password");
        testUser.setId(1);
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));

        Address anotherAddress = new Address();
        anotherAddress.setCountry("New Zealand");
        testOtherUser = new User(
                "Jenny",
                "Dove",
                "Amelia",
                null,
                null,
                "jenny.dove@icloud.com",
                "1996-06-30",
                null,
                anotherAddress,
                "password");
        testOtherUser.setId(2);
        testOtherUser.setPassword(passwordEncoder.encode(testOtherUser.getPassword()));
    }

    public void initialiseTestSystemAdmin() {
        Address address = new Address(null, null, null, null, "New Zealand", null);
        testSystemAdmin = new User(
                "System",
                "Admin",
                "",
                "",
                "I am a system admin",
                "admin@resale.com",
                "1999-07-28",
                "+64 123 4567",
                address,
                "Th1s1sMyApplication");
        testSystemAdmin.setId(3);
        testSystemAdmin.setRole("globalApplicationAdmin");
        testSystemAdmin.setPassword(passwordEncoder.encode(testSystemAdmin.getPassword()));
    }

    public void initialiseTestSystemDGAA() {
        Address address = new Address(null, null, null, null, "New Zealand", null);
        testSystemDGAA = new User(
                "System",
                "Admin",
                "",
                "",
                "I am a system admin",
                "admin@resale.com",
                "1999-07-28",
                "+64 123 4567",
                address,
                "Th1s1sMyApplication");
        testSystemDGAA.setId(4);
        testSystemDGAA.setRole("defaultGlobalApplicationAdmin");
        testSystemDGAA.setPassword(passwordEncoder.encode(testSystemAdmin.getPassword()));
    }

    public void initialiseTestCard() {
        testCard = new Card(
                testUser,
                "ForSale",
                "New Card",
                "This is a new Card",
                Collections.emptySet());
    }

    public void initialiseTestUserBusinessAdmin() {
        Address address = new Address(null, null, null, null, "New Zealand", null);
        testUserBusinessAdmin = new User(
                "Jane",
                "Doe",
                "Angela",
                null,
                null,
                "jane.doe@gmail.com",
                "1995-06-21",
                null,
                address,
                "password");
        testUserBusinessAdmin.setId(5);
        testUserBusinessAdmin.setPassword(passwordEncoder.encode(testUserBusinessAdmin.getPassword()));
    }

    public void initialiseTestBusiness() {
        Address address = new Address(null, null, null, null, "New Zealand", null);
        testBusiness = new Business(
                "Test Business",
                "A test business",
                address,
                "Retail Trade",
                5);
        testBusiness.setId(1);
        testBusiness.addAdministrator(testUserBusinessAdmin);
    }

    public void initialiseTestProduct() {
        testProduct = new Product(
                "TEST-PROD",
                "Test Product",
                "A test product",
                null,
                null,
                1);
    }

    public void initialiseTestCards() {
        testKeywords = new ArrayList<>();
        testCards = new ArrayList<>();

        var keyword1 = new Keyword("Apples");
        keyword1.setId(1);
        testKeywords.add(keyword1);

        var keyword2 = new Keyword("Bananas");
        keyword2.setId(2);
        testKeywords.add(keyword2);

        var card1 = new Card(testUser, "ForSale", "A card",
                "Some description", Set.of(testKeywords.get(0)));
        card1.setId(1);
        testCards.add(card1);

        var card2 = new Card(testUser, "ForSale", "Another card",
                "Some description", Set.of(testKeywords.get(1)));
        card2.setId(2);
        testCards.add(card2);
    }

    public void initialiseTestImages() {
        Image image1 = new Image("image1.jpg", "image1_thumbnail.jpg");
        image1.setId(1);
        Image image2 = new Image("image2.jpg", "image2_thumbnail.jpg");
        image2.setId(2);
        Image image3 = new Image("image3.jpg", "image3_thumbnail.jpg");
        image3.setId(3);
        testImages = new ArrayList<>();
        testImages.addAll(List.of(image1, image2, image3));
    }

    public void initialiseTestFiles() {
        testFile = new MockMultipartFile(
                "file",
                "file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "file".getBytes()
        );

        File imagePath = new File("src/test/resources/public/media/asparagus.jpg");
        byte[] imageContent;
        try {
            imageContent = Files.readAllBytes(imagePath.toPath());
        } catch (IOException exception) {
            imageContent = "image".getBytes();
        }

        testImageFile = new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                imageContent
        );
    }

    public void initialiseTestUserNotification() {
        testUserNotification = new UserNotification(
                "This is a notification message", testUser
        );
        testUserNotification.setId(1);
    }

    public void initialiseTestMessages() {
        Message message1 = new Message("Is this still available?", testUser, testCard, testOtherUser);
        message1.setId(1);
        Message message2 = new Message("Yes it is still available", testOtherUser, testCard, testUser);
        message2.setId(2);
        testMessages = new ArrayList<>();
        testMessages.addAll(List.of(message1, message2));
    }

    public void initialiseTestSaleListings() {
        saleListings = new ArrayList<>();
        Address address1 = new Address(null, null, "Rangiora", null, "Netherlands", null);
        Business business1 = new Business("First Business", null, address1, "Retail Trade", 1);


        Product product1 = new Product("TEST-1", "First Product", null, null, 5.00, business1.getId());
        InventoryItem inventoryItem1 = new InventoryItem(product1, 5, null, null, "2021-01-01", null, null, "2021-12-01");
        SaleListing saleListing1 = new SaleListing(business1, inventoryItem1, 10.00, null, LocalDateTime.parse("2021-08-25T00:00:00"), 5);
        saleListings.add(saleListing1);


        Product product2 = new Product("TEST-2", "Second Product", null, null, 5.00, business1.getId());
        InventoryItem inventoryItem2 = new InventoryItem(product2, 10, null, null, "2021-01-01", null, null, "2021-12-02");
        SaleListing saleListing2 = new SaleListing(business1, inventoryItem2, 15.00, null, LocalDateTime.parse("2021-10-25T00:00:00"), 10);
        saleListings.add(saleListing2);


        Address address2 = new Address(null, null, "Christchurch", null, "New Zealand", null);
        Business business2 = new Business("Second Business", null, address2, "Charitable Organisation", 1);


        Product product3 = new Product("TEST-3", "Third Product", null, null, 5.00, business2.getId());
        InventoryItem inventoryItem3 = new InventoryItem(product3, 5, null, null, "2021-01-01", null, null, "2021-12-03");
        SaleListing saleListing3 = new SaleListing(business2, inventoryItem3, 20.00, null, LocalDateTime.parse("2021-11-25T00:00:00"), 5);
        saleListings.add(saleListing3);


        Product product4 = new Product("TEST-4", "Fourth Product", null, null, 5.00, business2.getId());
        InventoryItem inventoryItem4 = new InventoryItem(product4, 5, null, null, "2021-01-01", null, null, "2021-12-04");
        SaleListing saleListing4 = new SaleListing(business2, inventoryItem4, 30.00, null, LocalDateTime.parse("2021-12-25T00:00:00"), 5);
        saleListings.add(saleListing4);

    }

}
