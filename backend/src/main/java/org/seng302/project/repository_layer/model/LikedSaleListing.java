package org.seng302.project.repository_layer.model;

import lombok.Data;
import org.seng302.project.repository_layer.model.enums.Tag;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
public class LikedSaleListing {

    private Integer id;
    private User user;
    private SaleListing listing;
    private boolean starred;
    private Tag tag;

    @Id
    public Integer getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    @ManyToOne
    @JoinColumn(name = "listing_id")
    public SaleListing getListing() {
        return listing;
    }

}
