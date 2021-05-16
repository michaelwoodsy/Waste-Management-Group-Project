/**
 * Module for date and time arithmetic.
 */

/**
 * Takes a ISO8601 date string, and returns a formatted representation
 * of when the date was. Eg, a date 2 days ago will return "2d".
 * @param dateString ISO8601 date string.
 * @return formatted date string.
 */
export function getTimeDiffStr(dateString) {
    // Get the current date, and parse the provided date
    const date = new Date(Date.parse(dateString))
    const now = new Date(Date.now());

    // Calculate the difference in the corresponding units
    const minutesSince = (now - date)/60000;
    const hoursSince = minutesSince / 60;
    const daysSince = hoursSince / 24;
    const weeksSince = daysSince / 7;

    // Check which date string to use
    if (minutesSince < 1) {
        return 'now'
    } else if (minutesSince < (60)) {
        return `${Math.floor(minutesSince)}m`
    } else if (hoursSince < 24) {
        return `${Math.floor(hoursSince)}h`
    } else if (daysSince < 7) {
        return `${Math.floor(daysSince)}d`
    }

    return `${Math.floor(weeksSince)}w`
}