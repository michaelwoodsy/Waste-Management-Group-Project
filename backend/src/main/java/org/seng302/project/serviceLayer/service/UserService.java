package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.repositoryLayer.specification.UserSpecifications;
import org.seng302.project.serviceLayer.dto.user.UserResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Provides logic for User objects
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class.getName());
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Searches for users based on a search query string.
     * Regular expression for splitting search query taken from linked website.
     *
     * @param searchQuery Query to search users by.
     * @return List of users matching the search query.
     * @see <a href="https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes">
     * https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes</a>
     */
    public List<UserResponseDTO> searchUsers(String searchQuery) {
        List<User> users = List.of();

        if (searchQuery.equals("")) {
            // Search query is for all results
            users = userRepository.findAll();

        } else {
            Set<User> result = new LinkedHashSet<>();

            searchQuery = searchQuery.toLowerCase(); // Convert search query to all lowercase.
            String[] conjunctions = searchQuery.split(" or (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by OR

            for (String conjunction : conjunctions) {
                Specification<User> hasSpec = Specification.where(null);
                Specification<User> containsSpec = Specification.where(null);
                boolean searchContains = false;
                String[] names = conjunction.split("( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by AND

                // Iterate over the names in the search and check if they are quoted
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

                // query the repository and add to results set
                result.addAll(userRepository.findAll(hasSpec));
                if (searchContains) {
                    result.addAll(userRepository.findAll(containsSpec));
                }

                // convert set to a list
                users = new ArrayList<>(result);
            }
        }

        logger.info(String.format("Retrieved %d users", users.size()));

        // Map user objects to UserResponseDTO objects and return
        return users.stream().map(UserResponseDTO::new).collect(Collectors.toList());
    }


}
