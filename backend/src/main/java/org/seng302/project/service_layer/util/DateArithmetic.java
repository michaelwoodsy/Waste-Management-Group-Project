package org.seng302.project.service_layer.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.*;

/**
 * Class for performing date arithmetic.
 */
public final class DateArithmetic {

    /**
     * Calculates the difference between two dates in years.
     *
     * @see <a href="https://stackoverflow.com/questions/7906301/how-can-i-find-the-number-of-years-between-two-dates">
     * https://stackoverflow.com/questions/7906301/how-can-i-find-the-number-of-years-between-two-dates</a>.
     */
    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }
}
