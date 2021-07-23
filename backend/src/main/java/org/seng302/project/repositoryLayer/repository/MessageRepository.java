package org.seng302.project.repositoryLayer.repository;

import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.Message;
import org.seng302.project.repositoryLayer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Repository for accessing and storing Message objects
 */
@RepositoryRestResource
public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findAllByReceiver(@Param("receiver") User receiver);

    List<Message> findAllByCard(@Param("card") Card card);
}
