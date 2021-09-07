package org.seng302.project.repository_layer.model.enums;

public enum ReportGranularity {

    YEAR("year"),
    MONTH("month"),
    FORTNIGHT("fortnight"),
    WEEK("week"),
    DAY("day"),
    HOUR("hour");

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
}
