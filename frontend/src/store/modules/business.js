import { Business } from '../../Api'


export default {
    debug: true,

    /**
     * Sets the user state as logged in, including the userId cookie.
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
