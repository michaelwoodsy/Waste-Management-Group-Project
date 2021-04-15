import { User } from '@/Api'
import { getCookie, setCookie, deleteCookie } from "../../utils/cookieJar";
import { createRed as createAlertRed } from "@/./utils/globalAlerts"

export default {
    debug: true,

    /** Store state **/
    state: {
        message: 'Hello!',
        loggedIn: false,
        userId: null,
        userData: {},
        actingAs: null
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

                // Set acting as if its null
                if (this.state.actingAs == null) {
                    let name = `${res.data.firstName} ${res.data.lastName}`
                    this.setActingAs(res.data.id, name, 'user')
                }

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
        this.state.actingAs = null;
        deleteCookie('userId');
        deleteCookie('actor');
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
     * Checks if the current user is logged in (based on cookies) and sets the app state accordingly
     */
    checkLoggedIn() {
        // Ger userId from cookies
        const userId = getCookie('userId');

        // Check if the userId was null
        if (userId == null) {
            return
        } else {
            // Try set actor
            try {
                const actor = JSON.parse(getCookie('actor'));
                this.setActingAs(actor.id, actor.name, actor.type)
            }
            catch(err) {
                deleteCookie('actor')
            }

            // Set logged in
            this.setLoggedIn(userId)
        }

        // Check if the logged in users session token is valid
        User.getUserData(userId)
            .then(() => this.setLoggedIn(userId))
            .catch(() => this.setLoggedOut())
    },

    /**
     * Sets the user to act as either a business or user
     * @param id The id of the person or business
     * @param name The name of the person or business
     * @param type The type, either "business" or "user"
     */
    setActingAs (id, name, type) {
        if (type !== "business" && type !== "user") {
            throw new Error('Type must be business or user')
        }
        this.state.actingAs = {
            name, id, type
        }
        setCookie('actor', JSON.stringify(this.state.actingAs), null)
    },

    /**
     * Returns true if the user is acting as a business
     * @returns {boolean|*}
     */
    isActingAsBusiness () {
        return this.state.actingAs.type === "business"
    },

    /**
     * Returns true if the user is primary admin of the business they are acting as
     * @returns {boolean|*}
     */
    isPrimaryAdminOfBusiness () {
        if (this.state.actingAs.type !== "business") return false

        //Looks through the users businessesAdministered, finds the business acting as and then checks to see if the current user is the primary admin of that business
        //Used to show the Add Administrator button on a users profile page
        for (let i = 0; i < this.state.userData.businessesAdministered.length; i++) {
            let business = this.state.userData.businessesAdministered[i]
            if (business.id === this.state.actingAs.id && business.primaryAdministratorId === this.state.userData.id) {
                return true
            }
        }
        return false
    },
}
