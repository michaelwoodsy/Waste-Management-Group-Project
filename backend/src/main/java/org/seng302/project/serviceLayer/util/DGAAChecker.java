package org.seng302.project.serviceLayer.util;

import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Singleton class for checking & creating existence of DGAA
 */
@Component
public class DGAAChecker {

    private static final Logger logger = LoggerFactory.getLogger(DGAAChecker.class.getName());
    private final SpringEnvironment springEnvironment;
    private final BCryptPasswordEncoder passwordEncoder;
    private AddressRepository addressRepository;
    private UserRepository userRepository;

    /**
     * Private constructor for this singleton
     */
    @Autowired
    public DGAAChecker(AddressRepository addressRepository,
                       UserRepository userRepository,
                       SpringEnvironment springEnvironment,
                       BCryptPasswordEncoder passwordEncoder) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.springEnvironment = springEnvironment;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Method to set the address repository. Used for unit testing purposes only.
     *
     * @param addressRepository address repository which will be mocked.
     */
    public void setAddressRepository(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Method to set the user repository for the DGAA checker. Used for unit testing purposes only.
     *
     * @param userRepository user repository which will be mocked.
     */
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns true when a DGAA exists
     *
     * @return true if DGAA exists, false otherwise
     */
    public Boolean dgaaExists() {
        List<User> dgaaUsers = userRepository.findByRole("defaultGlobalApplicationAdmin");
        return dgaaUsers.size() != 0;
    }

    /**
     * Checks that a DGAA exists.
     * If no DGAA exists, one is created and an error log entry is made.
     */
    @Scheduled(fixedDelay = 600000, initialDelay = 600000)
    public void dgaaCheck() {
        logger.info("Running check for DGAA...");

        if (!dgaaExists()) {
            logger.error("No DGAA found. Creating new DGAA...");
            User dgaa = createDGAA();
            if (dgaa != null) {
                addressRepository.save(dgaa.getHomeAddress());
                userRepository.save(dgaa);
                logger.info("Successfully created DGAA");
            }
        } else {
            logger.info("Check for DGAA found DGAA");
        }
    }

    /**
     * Creates a new User object to be used as the DGAA.
     *
     * @return new default global application admin.
     */
    public User createDGAA() {
        String email = springEnvironment.DGAA_EMAIL;
        String password = springEnvironment.DGAA_PASSWORD;

        if (email == null || password == null) {
            logger.error("Error: DGAA variables are not present");
            logger.error("Aborting creation of DGAA");
            return null;
        }

        password = passwordEncoder.encode(password);
        Address address = new Address();
        User admin = new User("admin", "", "", "",
                null, email, null, null, address, password);
        admin.setRole("defaultGlobalApplicationAdmin");
        return admin;
    }
}
