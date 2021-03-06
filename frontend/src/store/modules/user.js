import {User} from '@/Api'
import {deleteCookie, getCookie, setCookie} from "@/utils/cookieJar";
import {createRed as createAlertRed} from "@/utils/globalAlerts"

export default {
    debug: true,

    /** Store state **/
    state: {
        message: 'Hello!',
        loggedIn: false,
        userId: null,
        role: null,
        userData: {},
        actingAs: null
    },

    /**
     * Sets the user state as logged in, including the userId cookie.
     *
     * @param userId UserId of the user that is logged in
     * used so the userData is set before completely logging in or registering
     */
    async setLoggedIn(userId) {
        try {
            const res = await User.getUserData(userId)
            // Successfully got user data
            this.state.userData = res.data;
            this.state.role = res.data.role;
            this.state.userId = userId;
            this.state.loggedIn = true;

            // Set acting as if it's null
            if (this.state.actingAs == null) {
                let name = `${res.data.firstName} ${res.data.lastName}`
                this.setActingAs(res.data.id, name, 'user')
            }

            setCookie('userId', this.state.userId, null);
        } catch (err) {
            // Failed to get data, alert the user
            if (err.response.status === 401) {
                createAlertRed("Your session has expired! Try logging in again.")
            } else {
                createAlertRed("Error: " + err.response.data.message)
            }
            this.setLoggedOut()
        }
    },

    /**
     * Updates data for user, called when a new business is created, so the new business is shown in userData
     */
    async updateData() {
        if (this.state.userId !== null) {
            await this.setLoggedIn(this.state.userId)
        }
    },

    /**
     * Sets the current user as logged out.
     */
    setLoggedOut() {
        this.state.loggedIn = false;
        this.state.userId = null;
        this.state.role = null;
        this.state.userData = {};
        this.state.actingAs = null;
        deleteCookie('userId');
        deleteCookie('actor');
    },

    /**
     * Sends request to register a new account
     *
     * @param data User information to register account with
     */
    async register(data) {
        const res = await User.createNew(data)
        await this.setLoggedIn(res.data.userId)
    },

    /**
     * Logs the user in, and sets the corresponding loggedIn value if successful
     *
     * @param username Username to send to api
     * @param password Password to send to api
     */
    async login(username, password) {
        const res = await User.login(username, password)
        await this.setLoggedIn(res.data.userId)
    },

    /**
     * Logs out the user, and updates the according loggedIn value
     */
    logout() {
        this.setLoggedOut()
    },

    /**
     * Checks if the current user is logged in (based on cookies) and sets the app state accordingly
     */
    async checkLoggedIn() {
        // Ger userId from cookies
        const userId = getCookie('userId');

        // Check if the userId was null
        if (userId != null) {
            // Try set actor
            try {
                const actor = JSON.parse(getCookie('actor'));
                this.setActingAs(actor.id, actor.name, actor.type)
            } catch (err) {
                deleteCookie('actor')
            }
            // Set logged in
            await this.setLoggedIn(userId)
        }
    },

    /**
     * Sets the user to act as either a business or user
     * @param id The id of the person or business
     * @param name The name of the person or business
     * @param type The type, either "business" or "user"
     */
    setActingAs(id, name, type) {
        if (type !== "business" && type !== "user") {
            throw new Error('Type must be business or user')
        }
        this.state.actingAs = {
            name, id, type
        }
        setCookie('actor', JSON.stringify(this.state.actingAs), null)
    },

    /**
     * Returns whether a user is logged in or not
     *
     * @returns {boolean} true if user is logged in
     */
    isLoggedIn() {
        return this.state.loggedIn
    },

    /**
     * Returns the current actor
     *
     * @returns {object} the current actor
     */
    actor() {
        return this.state.actingAs
    },

    /**
     * Returns true if the user is acting as a user
     * @returns {boolean|*}
     */
    isActingAsUser() {
        return this.state.actingAs.type === "user"
    },

    /**
     * Returns true if the user is acting as a business
     * @returns {boolean|*}
     */
    isActingAsBusiness() {
        return this.state.actingAs.type === "business"
    },

    /**
     * Returns true if the user is primary admin of the business they are acting as
     * @returns {boolean|*}
     */
    isPrimaryAdminOfBusiness() {
        if (this.state.actingAs.type !== "business") return false

        //Looks through the users businessesAdministered, finds the business acting as and then checks to see if the current user is the primary admin of that business
        //Used to show the Add Administrator button on a users profile page
        for (const business of this.state.userData.businessesAdministered) {
            if (business.id === this.state.actingAs.id && business.primaryAdministratorId === this.state.userData.id) {
                return true
            }
        }
        return false
    },

    /**
     * Returns true if the user is a GAA
     * @returns {boolean|*}
     */
    isGAA() {
        return this.state.userData.role === "globalApplicationAdmin"
    },

    /**
     * Returns true if the user is a DGAA
     * @returns {boolean|*}
     */
    isDGAA() {
        return this.state.userData.role === "defaultGlobalApplicationAdmin"
    },

    /**
     * Returns true if the user can proform Admin Actions
     * If they are a DGAA or GAA and are acting as a user
     * @returns {boolean|*}
     */
    canDoAdminAction() {
        return (this.isDGAA() || this.isGAA()) && this.isActingAsUser()
    },

    /**
     * Returns the ID of the user that is currently acting, null if not acting as a user
     *
     * @returns {*} ID of user that is currently acting
     */
    actingUserId() {
        if (this.state.actingAs.type === 'user') {
            return this.state.actingAs.id
        } else {
            return null
        }
    },

    /**
     * Returns true if the provided userId is the same as the logged in user id, and is acting as them.
     * Usefull for checking if the
     * @param userId userId to compare to the current logged in user.
     * @returns {boolean} True if the two ID's are the same, and the user is acting as themself.
     */
    isUser(userId) {
        return parseInt(this.state.userId) === parseInt(userId) && this.isActingAsUser();
    },

    /**
     * Allows the user to create a new card for in the marketplace
     * @param data for the marketplace card
     */
    createCard(data) {
        // Return a promise for the api call
        return new Promise(((resolve, reject) => {
            User.createCard(data)
                .then((res) => {
                    resolve(res);
                })
                .catch((err) => {
                    reject(err);
                });
        }));
    }


}
