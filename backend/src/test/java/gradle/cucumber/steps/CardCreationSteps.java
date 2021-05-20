package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class CardCreationSteps {

    private User testUser;
    private Integer testUserId;
    private Address testAddress;
    private MockMvc mockMvc;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final CardRepository cardRepository;

    @Autowired
    public CardCreationSteps(UserRepository userRepository,
                             BCryptPasswordEncoder passwordEncoder,
                             AddressRepository addressRepository,
                             CardRepository cardRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.cardRepository = cardRepository;
    }

    @BeforeEach
    @Autowired
    public void setUp(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        User testUser = new User("Admin", "Admin", "", "", "", dgaaEmail,
                "2000-05-21", "+64 3 555 0129", null, dgaaPassword);
        testAddress = new Address("", "", "", "", "New Zealand","");
        addressRepository.save(testAddress);
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        userRepository.save(testUser);
        testUserId = userRepository.save(testUser).getId();

    }


}
