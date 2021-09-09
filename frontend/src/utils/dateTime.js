/**
 * Module for date and time arithmetic.
 */

/**
 * Takes a ISO8601 date string, and returns a formatted representation
 * of when the date was. Eg, a date 2 days ago will return "2d".
 *
 * @param dateString ISO8601 date string.
 * @return formatted date string.
 */
export function getTimeDiffStr(dateString) {
    // Get the current date, and parse the provided date
    const date = new Date(Date.parse(dateString))
    const now = new Date(Date.now());

    // Calculate the difference in the corresponding units
    const minutesSince = (now - date) / 60000;
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

/**
 * Takes a datetime in ISO format and formats it like dd/mm/yyy, hh:mm:ss PM
 *
 * @param dateTime the ISO datetime string
 * @returns {string} formatted datetime
 */
export function formatDateTime(dateTime) {
    let date = new Date(dateTime)
    let year = date.getFullYear()
    let month = date.getMonth() + 1
    let day = date.getDate()

    let hours = date.getHours()
    let minutes = date.getMinutes()

    if (day < 10) day = '0' + day;
    if (month < 10) month = '0' + month;
    if (hours < 10) hours = '0' + hours;
    if (minutes < 10) minutes = '0' + minutes;

    return `${day}/${month}/${year} ${hours}:${minutes}`
}

/**
 * Takes a Date object and formats it as YYYY-MM-DD
 *
 * @param date Date object to format
 * @returns {string} string representing date as YYYY-MM-DD
 */
export function formatDate(date) {
    let yyyy = String(date.getFullYear())
    let mm = String(date.getMonth() + 1).padStart(2, '0')
    let dd = String(date.getDate()).padStart(2, '0')
    return `${yyyy}-${mm}-${dd}`
}