package org.seng302.project.repository_layer.model.enums;

/**
 * Enum for granularities of a sales report
 */
public enum ReportGranularity {

    YEAR("yearly"),
    MONTH("monthly"),
    WEEK("weekly"),
    DAY("daily"),
    ALL("all");

    private final String range;

    ReportGranularity(String range) {
        this.range = range;
    }

    /**
     * Returns the granularity enum for a date range.
     *
     * @param range granularity range.
     * @return granularity enum.
     */
    public static ReportGranularity getGranularity(String range) {
        for (ReportGranularity granularity : ReportGranularity.values()) {
            if (granularity.range.equals(range)) {
                return granularity;
            }
        }
        return null;
    }

    /**
     * Checks if a string matches one of the valid granularity ranges
     *
     * @param granularity string to check validity of
     * @return true if the string is a valid granularity
     */
    public static boolean checkGranularity(String granularity) {
        return getGranularity(granularity) != null;
    }
}
