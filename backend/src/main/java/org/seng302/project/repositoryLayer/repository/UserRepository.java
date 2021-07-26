package org.seng302.project.repositoryLayer.repository;

import org.seng302.project.repositoryLayer.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * User repository which acts as an interface to the database for manipulating users.
 */
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    List<User> findByEmail(@Param("email") String email);


    //Role can be one of [user, globalApplicationAdmin, defaultGlobalApplicationAdmin]
    List<User> findByRole(@Param("role") String role);

    /**
     * Used when searching for users
     * @param sort this is used to sort the search
     * @return list of users that match the sort
     */
    List<User> findAll(Sort sort);

}