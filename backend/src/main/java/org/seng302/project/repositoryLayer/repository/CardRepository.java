package org.seng302.project.repositoryLayer.repository;

import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Card repository which acts as an interface to the database for manipulating cards.
 */
@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Integer>, JpaSpecificationExecutor<Card> {

    List<Card> findAllBySection(@Param("section") String section);

    /**
     * Finds all cards created by a user.
     * @param user The user to find all cards for.
     * @return List of cards created by the user.
     */
    List<Card> findAllByCreator(@Param("creator") User user);

    /**
     * Finds all cards that have that keyword.
     * @param keyword The keyword to look for.
     * @return List of cards that contain the keyword.
     */
    List<Card> findAllByKeywordsContaining(@Param("keywords") Keyword keyword);

    @Modifying
    @Transactional
    List<Card> deleteByDisplayPeriodEndBefore(LocalDateTime dayAgo);

}