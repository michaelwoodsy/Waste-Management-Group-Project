package org.seng302.project;

import lombok.Data;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.serviceLayer.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class AbstractInitializer {

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private ImageUtil imageUtil;

    private User testUser;
    private User testSystemAdmin;
    private User testUserBusinessAdmin;
    private Business testBusiness;
    private Product testProduct;
    private List<Image> testImages;
    private MockMultipartFile testFile;
    private MockMultipartFile testImageFile;

    public void initialise() {
        this.initialiseTestUser();
        this.initialiseTestSystemAdmin();
        this.initialiseTestUserBusinessAdmin();
        this.initialiseTestBusiness();
        this.initialiseTestProduct();
        this.initialiseTestImages();
        this.initialiseTestFiles();
    }

    public void initialiseTestUser() {
        Address address = new Address(null, null, null, null, "New Zealand", null);
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
        testSystemAdmin.setId(2);
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
        testUserBusinessAdmin.setId(3);
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

        File imagePath = new File("src/test/resources/asparagus.jpg");
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

}
