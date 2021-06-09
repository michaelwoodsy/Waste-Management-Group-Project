package org.seng302.project.web_layer.controller;


import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.repository_layer.specification.UserSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * Controller with all our search functionality.
 */
@RestController
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class.getName());
    private final UserRepository userRepository;

    @Autowired
    public SearchController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Receives a request containing a search query to search users by name and retrieves a list
     * of users based on the query.
     * Regular expression for splitting search query taken from linked website.
     *
     * @param searchQuery user’s full name or one or more of their names/nickname.
     * @return 200 response with (potentially empty) list of users or 401 if not authenticated.
     * @see <a href="https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes">
     * https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes</a>
     */
    @GetMapping("/users/search")
    @ResponseStatus(HttpStatus.OK)
    public List<User> searchUsers(@RequestParam("searchQuery") String searchQuery) {

        logger.info(String.format("Request to search users with query: %s", searchQuery));

        try {
            if (searchQuery.equals("")) {
                List<User> users = userRepository.findAll();
                logger.info(String.format("Retrieved %d users", users.size()));
                return users;
            }

            Set<User> result = new LinkedHashSet<>();

            searchQuery = searchQuery.toLowerCase(); // Convert search query to all lowercase.
            String[] conjunctions = searchQuery.split(" or (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by OR

            for (String conjunction : conjunctions) {
                Specification<User> hasSpec = Specification.where(null);
                Specification<User> containsSpec = Specification.where(null);
                boolean searchContains = false;
                String[] names = conjunction.split("( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by AND

                for (String name : names) {
                    if (Pattern.matches("^\".*\"$", name)) {
                        name = name.replaceAll("\"", "");
                        hasSpec = hasSpec.and(UserSpecifications.hasName(name));
                    } else {
                        hasSpec = hasSpec.and(UserSpecifications.hasName(name));
                        containsSpec = containsSpec.and(UserSpecifications.containsName(name));
                        searchContains = true;
                    }
                }

                result.addAll(userRepository.findAll(hasSpec));
                if (searchContains) {
                    result.addAll(userRepository.findAll(containsSpec));
                }

                for (User currUser : result) {
                    //Do this so the return is not an infinite loop of businesses and users
                    for (Business business : currUser.getBusinessesAdministered()) {
                        business.setAdministrators(new ArrayList<>());
                    }
                }
            }

            logger.info(String.format("Retrieved %d users", result.size()));
            return new ArrayList<>(result);

        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while searching users: %s", exception.getMessage()));
            throw exception;
        }

    }

}