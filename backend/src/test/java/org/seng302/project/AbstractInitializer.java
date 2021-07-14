package org.seng302.project;

import lombok.Data;
import org.seng302.project.repositoryLayer.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
public abstract class AbstractInitializer {

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    private User testUser;
    private User testOtherUser;
    private User testSystemAdmin;
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

    public void initialise() {
        this.initialiseTestUsers();
        this.initialiseTestSystemAdmin();
        this.initialiseTestUserBusinessAdmin();
        this.initialiseTestBusiness();
        this.initialiseTestProduct();
        this.initialiseTestCards();
        this.initialiseTestImages();
        this.initialiseTestFiles();
        this.initialiseTestCard();
        this.initialiseTestUserNotification();
        this.initialiseTestMessages();
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
                "1995/07/25",
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
                "1996/06/30",
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

    public void initialiseTestUserBusinessAdmin() {
        Address address = new Address(null, null, null, null, "New Zealand", null);
        testUserBusinessAdmin = new User(
                "Jane",
                "Doe",
                "Angela",
                null,
                null,
                "jane.doe@gmail.com",
                "1995/06/21",
                null,
                address,
                "password");
        testUserBusinessAdmin.setId(4);
        testUserBusinessAdmin.setPassword(passwordEncoder.encode(testUserBusinessAdmin.getPassword()));
    }

    public void initialiseTestBusiness() {
        Address address = new Address(null, null, null, null, "New Zealand", null);
        testBusiness = new Business(
                "Test Business",
                "A test business",
                address,
                "Retail Trade",
                3);
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

    public void initialiseTestCard() {
        testCard = new Card(
                testUser,
                "ForSale",
                "New Card",
                "This is a new Card",
                Collections.emptySet());
        testCard.setId(1);
    }

    public void initialiseTestUserNotification() {
        testUserNotification = new UserNotification(
                "This is a notification message", testUser
        );
        testUserNotification.setId(1);
    }

    public void initialiseTestMessages(){
        Message message1 = new Message("Is this still available?", testUser, testCard, testOtherUser);
        message1.setId(1);
        Message message2 = new Message("Yes it is still available", testOtherUser, testCard, testUser);
        message2.setId(2);
        testMessages = new ArrayList<>();
        testMessages.addAll(List.of(message1, message2));
    }

}
