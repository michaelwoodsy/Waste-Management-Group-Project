package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Image repository which acts as an interface to the database for manipulating images.
 */
@RepositoryRestResource
public interface ImageRepository extends JpaRepository<Image, Integer> {

}