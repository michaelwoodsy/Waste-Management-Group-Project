import {Business} from '@/Api'

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
     * @param name Name of the business
     * @param description Business description
     * @param address Business address
     * @param businessType Type of business (e.g. Accommodation or Food Services)
     * @returns {Promise<unknown>} Axios response
     */
    register(primaryAdministrator, name, description, address, businessType) {
        // Return a promise for the api call
        return new Promise((resolve, reject) => {
            Business.createNew(primaryAdministrator, name, description, address, businessType)
                .then((res) => {
                    resolve(res)
                })
                .catch((err) => {
                    reject(err)
                })
        })
    },

    createProduct(businessId, data) {
        // Return a promise for the api call
        return new Promise(((resolve, reject) => {
            Business.createProduct(businessId, data)
                .then((res) => {
                    resolve(res);
                })
                .catch((err) => {
                    reject(err);
                });
        }));
    },

    /**
     * Creates a new item in the inventory
     */
    createItem(businessId, data) {
        //Return a promise for the api call
        return new Promise((resolve, reject) => {
            Business.createItem(businessId, data)
                .then((res) => {
                    resolve(res)
                })
                .catch((err) => {
                    reject(err)
                })
        })
    }

}
