package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Image class for storing individual images.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Image {
    private Integer id;
    private String filename;
    private String thumbnailFilename;

    public Image(String filename, String thumbnailFilename) {
        this.filename = filename;
        this.thumbnailFilename = thumbnailFilename;
    }

    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "image_id")
    public Integer getId() {
        return this.id;
    }
}
