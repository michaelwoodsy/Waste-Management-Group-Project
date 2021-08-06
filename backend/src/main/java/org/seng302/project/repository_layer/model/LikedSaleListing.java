package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.repository_layer.model.enums.Tag;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class LikedSaleListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "listing_id")
    private SaleListing listing;
    private boolean starred;
    private Tag tag;

    public LikedSaleListing(User user, SaleListing listing) {
        this.user = user;
        this.listing = listing;
        this.starred = false;
        this.tag = Tag.NONE;
    }

}
