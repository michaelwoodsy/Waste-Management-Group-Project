package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.repository_layer.model.enums.Tag;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class LikedSaleListing {

    private Integer id;
    private User user;
    private SaleListing listing;
    private boolean starred;
    private Tag tag;

    public LikedSaleListing(User user, SaleListing listing, boolean starred, Tag tag) {
        this.user = user;
        this.listing = listing;
        this.starred = starred;
        this.tag = tag;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
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
