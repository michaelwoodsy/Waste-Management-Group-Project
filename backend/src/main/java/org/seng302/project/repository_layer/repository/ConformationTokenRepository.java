package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.ConformationToken;
import org.seng302.project.repository_layer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for ConformationToken objects in the database.
 */
public interface ConformationTokenRepository extends JpaRepository<ConformationToken, Integer> {

    /**
     * Allows finding a ConformationToken by token.
     *
     * @param token token to search for.
     * @return optional ConformationToken that matches the token.
     */
    Optional<ConformationToken> findByToken(@Param("token") String token);


    /**
     * Allows finding a ConformationToken by User.
     *
     * @param user User to search for.
     * @return optional ConformationToken that matches the user.
     */
    Optional<ConformationToken> findByUser(@Param("user") User user);

}