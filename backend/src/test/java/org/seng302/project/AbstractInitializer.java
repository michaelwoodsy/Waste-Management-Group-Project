package org.seng302.project;

import lombok.Data;
import org.seng302.project.repository_layer.model.Address;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Product;
import org.seng302.project.repository_layer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
public abstract class AbstractInitializer {

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    private User testUser;
    private User testSystemAdmin;
    private User testUserBusinessAdmin;
    private Business testBusiness;
    private Product testProduct;
    private MockMultipartFile testFile;

    public void initialise() {
        this.initialiseTestUser();
        this.initialiseTestSystemAdmin();
        this.initialiseTestUserBusinessAdmin();
        this.initialiseTestBusiness();
        this.initialiseTestProduct();
        this.initialiseTestFile();
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

    public void initialiseTestFile() {
        testFile = new MockMultipartFile(
                "file",
                "file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "file".getBytes());
    }

}
