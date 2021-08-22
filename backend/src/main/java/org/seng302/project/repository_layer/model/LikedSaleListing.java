package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.repository_layer.model.enums.Tag;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "liked_sale_listing")
public class LikedSaleListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private SaleListing listing;

    @Column(name = "starred")
    private boolean starred;

    @Column(name = "tag")
    private Tag tag;

    public LikedSaleListing(User user, SaleListing listing) {
        this.user = user;
        this.listing = listing;
        this.starred = false;
        this.tag = Tag.NONE;
    }

}
