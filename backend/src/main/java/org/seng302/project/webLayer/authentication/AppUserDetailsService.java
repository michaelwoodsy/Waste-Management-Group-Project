package org.seng302.project.webLayer.authentication;

import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserDetailsService class which provides user details to authentication manager.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user from the user repository by email address.
     * Used to pass a user detail to the authentication manager.
     *
     * @param username email address to find user by.
     * @return User details to be passed to autghentication manager.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        List<User> userList = userRepository.findByEmail(username);
        return new AppUserDetails(userList.get(0));
    }

}
