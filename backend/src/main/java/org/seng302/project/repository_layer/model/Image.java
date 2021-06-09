package org.seng302.project.repository_layer.model;


/**
 * A basic Image class that just has an id field.
 * Used for testing setting a primary image for a product
 */
public class Image {

    private Integer id;

    public Image(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
