package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Keyword repository which acts as an interface to the database for manipulating keywords.
 */
@RepositoryRestResource
public interface KeywordRepository extends JpaRepository<Keyword, Integer>, JpaSpecificationExecutor<Keyword> {

    /**
     * Allows finding a Keyword by name
     *
     * @param name keyword name
     * @return list of keywords with matching name
     */
    List<Keyword> findByName(@Param("name") String name);
}
