package org.seng302.project.model.types;

/**
 * Enumeration containing valid business types.
 */
public enum BusinessType {

    ACCOMMODATION_AND_FOOD("Accommodation and Food Services"),
    RETAIL_TRADE("Retail Trade"),
    CHARITABLE_ORG("Charitable organisation"),
    NON_PROFIT_ORG("Non-profit organisation");

    private final String type;

    BusinessType(String type) {
        this.type = type;
    }

    /**
     * Method which checks if a string matches a valid business type.
     *
     * @param type the type to check.
     * @return true if the type is valid, false otherwise.
     */
    public static boolean checkType(String type) {
        boolean validType = false;
        for (BusinessType businessType : BusinessType.values()) {
            if (businessType.type.equals(type)) {
                validType = true;
                break;
            }
        }
        return validType;
    }

}
