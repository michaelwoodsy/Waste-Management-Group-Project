import { Business } from '@/Api'


export default {
    debug: true,

    /** Store state **/
    state: {
        isAdminOf: false,
    },

    /**
     * Registers a business under a given user.
     * This user is the primary administrator.
     * @param primaryAdministrator UserId of the user that is logged in
     * @param businessName Name of the business
     * @param description Business description
     * @param businessAddress Business address
     * @param businessType Type of business (e.g. Accommodation or Food Services)
     * @returns {Promise<unknown>} Axios response
     */
    register (primaryAdministrator, businessName, description, businessAddress, businessType) {
        // Return a promise for the api call
        return new Promise((resolve, reject) => {
            Business.createNew(primaryAdministrator, businessName, description, businessAddress, businessType)
                .then((res) => {
                    resolve(res)
                })
                .catch((err) => {
                    reject(err)
                })
        })
    },
}
