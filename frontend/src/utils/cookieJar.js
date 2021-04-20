// Module provides methods for setting and getting cookies

/**
 * Gets a cookies with that name
 * @param name Name of the cookie to get
 * @returns {string|null} Value of the cookie
 */
export function getCookie(name) {
    let v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return v ? v[2] : null;
}

/**
 * Sets a cookie, if days is null cookie will be session
 * @param name Name for cookie
 * @param value Value for the cookie
 * @param days Number of days to keep the cookie
 */
export function setCookie(name, value, days) {
    let d = new Date;
    let dString = "0";
    if (days !== null) {
        d.setTime(d.getTime() + 24 * 60 * 60 * 1000 * days);
        dString = d.toGMTString()
    }
    document.cookie = name + "=" + value + ";path=/;expires=" + dString;
}

/**
 * Deletes a cookie
 * @param name Name of the cookie to delete
 */
export function deleteCookie(name) {
    setCookie(name, '', -1);
}