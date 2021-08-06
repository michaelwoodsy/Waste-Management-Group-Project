package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Notification for the buyer of a sale listing.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@PrimaryKeyJoinColumn(name = "purchase_notification_id")
public class PurchaserNotification extends UserNotification {

    static final String MESSAGE_TEMPLATE = "You have bought %d %s from %s";
    // The price and currency country needed so the frontend can work out the currency
    private Double price;
    private String currencyCountry;
    private Address address;

    public PurchaserNotification(User user, SaleListing saleListing, Business business) {
        super( );
        var product = saleListing.getInventoryItem().getProduct();
        this.setType("purchase");
        this.setUser(user);
        this.setMessage(createMessage(saleListing, product, business));

        this.price = saleListing.getPrice();
        this.address = business.getAddress();
        // Either the currency country on the product or the business country
        this.currencyCountry = product.getCurrencyCountry();
        if (this.currencyCountry == null) {
            this.currencyCountry = address.getCountry();
        }
    }

    /**
     * Returns the message for the notification.
     * @param saleListing SaleListing that has been purchased.
     * @param product Product that was listed.
     * @param business Business that has sold the product.
     * @return Message to be returned with notification.
     */
    private String createMessage(SaleListing saleListing, Product product, Business business) {
        return String.format(MESSAGE_TEMPLATE, saleListing.getQuantity(), product.getName(), business.getName());
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    public Address getAddress() {
        return this.address;
    }

}
