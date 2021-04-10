import { User } from '../../Api'
import { getCookie, setCookie, deleteCookie } from "../../../utils/cookieJar";
import { createRed as createAlertRed } from "@/../utils/globalAlerts"

export default {
    debug: true,

    /** Store state **/
    state: {
        message: 'Hello!',
        loggedIn: false,
        userId: null,
        userData: {}
    },

    /**
     * Sets the user state as logged in, including the userId cookie.
     * @param userId UserId of the user that is logged in
     */
    setLoggedIn(userId) {
        User.getUserData(userId)
            .then((res) => {
                // Successfully got user data
                this.state.userData = res.data;
                this.state.userId = userId;
                this.state.loggedIn = true;
                setCookie('userId', this.state.userId, null);
            })
            .catch((err) => {
                // Failed to get data, alert the user
                createAlertRed(err.response.data)
                this.setLoggedOut()
            })
    },

    /**
     * Sets the current user as logged out.
     */
    setLoggedOut() {
        this.state.loggedIn = false;
        this.state.userId = null;
        this.state.userData = {};
        deleteCookie('userId');
    },

    register (firstName, lastName, middleName, nickname, bio, email, dateOfBirth, phoneNumber, homeAddress, password) {
        // Return a promise for the api call
        return new Promise((resolve, reject) => {
            User.createNew(firstName, lastName, middleName, nickname, bio, email, dateOfBirth, phoneNumber, homeAddress, password)
                .then((res) => {
                    // Set logged in then resolve the promise
                    this.setLoggedIn(res.data.userId);
                    resolve(res)
                })
                .catch((err) => {
                    // Set logged out then reject the promise
                    this.setLoggedOut();
                    reject(err)
                })
        })
    },

    /**
     * Logs the user in, and sets the corresponding loggedIn value if successful
     * @param username Username to send to api
     * @param password Password to send to api
     * @returns {Promise<unknown>} Axios response
     */
    login (username, password) {
        // Return a promise for the api call
        return new Promise((resolve, reject) => {
            User.login(username, password)
                .then((res) => {
                    // Set logged in then resolve the promise
                    this.setLoggedIn(res.data.userId);
                    resolve(res)
                })
                .catch((err) => {
                    // Set logged out then reject the promise
                    this.setLoggedOut();
                    reject(err)
                })
        })
    },

    /**
     * Logs out the user, and updates the according loggedIn value
     */
    logout () {
       this.setLoggedOut()
    },

    /**
     * Checks if the current user is logged in, sets the app state accordingly
     */
    checkLoggedIn() {
        // Ger userId from cookies
        const userId = getCookie('userId');

        // Check if the userId was null
        if (userId == null) {
            return
        } else {
            this.setLoggedIn(userId)
        }

        // Check if the logged in users session token is valid
        User.getUserData(userId)
            .then(() => this.setLoggedIn(userId))
            .catch(() => this.setLoggedOut())
    }
}
