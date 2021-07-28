package org.seng302.project.repository_layer.model.types;

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

    /**
     * Gets the BusinessType enum object based on a String business type
     *
     * @param type the type to return the enum object of. e.g., "Retail Trade"
     * @return BusinessType that matches the string value e.g., "RETAIL_TRADE"
     */
    public static BusinessType getType(String type) {
        for (BusinessType businessType : BusinessType.values()) {
            if (businessType.type.equals(type)) {
                return businessType;
            }
        }
        return null;
    }

    /**
     * Returns true if a BusinessType enum object matches a string type
     *
     * @param type string value of the type to check e.g., "Retail Trade"
     * @return true if the enum object matches the string type.
     */
    public boolean matchesType(String type) {
        return this.type.equals(type);
    }

}
