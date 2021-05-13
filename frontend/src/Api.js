/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

/**
 * Declare all available services here
 */
import axios from 'axios'

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 2000,
    withCredentials: true
});

export default {
    // (C)reate


    // (R)ead
    getAll: () => instance.get('students', {
        transformResponse: [function (data) {
            return data ? JSON.parse(data)._embedded.students : data;
        }]
    }),
    // (U)pdate
    updateForId: (id, firstName, lastName) => instance.put('students/' + id, {firstName, lastName}),
    // (D)elete
    removeForId: (id) => instance.delete('students/' + id)
}

export const User = {
    createNew: (firstName,
                lastName,
                middleName,
                nickname,
                bio,
                email,
                dateOfBirth,
                phoneNumber,
                homeAddress,
                password) => instance.post('users', {
        firstName,
        lastName,
        middleName,
        nickname,
        bio,
        email,
        dateOfBirth,
        phoneNumber,
        homeAddress,
        password
    }),

    login: (username, password) => instance.post('login', {username, password}),

    getUserData: (id) => instance.get(`users/${id}`, {}),

    getUsers: (searchTerm) => instance.get('users/search', {params: {'searchQuery': searchTerm}}),
};

export const Business = {
    /*
     * Creates a new business under a given user.
     */
    createNew: (primaryAdministratorId,
                name,
                description,
                address,
                businessType) => instance.post('businesses', {
        primaryAdministratorId,
        name,
        description,
        address,
        businessType
    }),

    /*
     * Retrieves the data for a given business
     */
    getBusinessData: (id) => instance.get(`businesses/${id}`, {}),

    /*
     *  Removes a user with id userId from administering the business with id businessId
     */
    removeAdministrator: (businessId, userId) => instance.put(`/businesses/${businessId}/removeAdministrator`, {userId}),

    /*
     *  Adds a user with id userId to administrators of the business with id businessId
     */
    addAdministrator: (businessId, userId) => instance.put(`/businesses/${businessId}/makeAdministrator`, {userId}),

    /*
     * Gets all the products in a business's catalogue
     */
    getProducts: (businessId) => instance.get(`businesses/${businessId}/products`, {}),

    /*
     * Sends an edit product request to the backend
     */
    editProduct: (businessId, productId, newProductData) => instance.put(
        `businesses/${businessId}/products/${productId}`,
        newProductData),

    /**
     * Creates a new item in the inventory
     */
    createItem: (businessId, data) => instance.post(`businesses/${businessId}/inventory`, data),

    /*
     * Gets all the items in a business's inventory
     */
    getInventory: (businessId) => instance.get(`businesses/${businessId}/inventory`, {}),

    /*
     * Sends an edit inventory item request to the backend
     */
    editItem: (businessId, inventoryItemId, newItemData) => instance.put(
        `businesses/${businessId}/inventory/${inventoryItemId}`,
        newItemData),

    /**
     * Creates a new product in the product catalogue
     */
    createProduct: (businessId, data) => instance.post(`businesses/${businessId}/products`, data),

    /**
     * Retrieves all the listings for a given business
     * @param businessId The ID of the business in the database
     */
    getListings: (businessId) => instance.get(`businesses/${businessId}/listings`, {}),

    /**
     * Creates a new listing for a given business
     * @param businessId The ID of the business in the database
     * @param data The listing data
     */
    createListing: (businessId, data) => instance.post(`businesses/${businessId}/listings`, data),

    /**
     * Creates a new Card for in the community marketplace
     * @param data
     */
    createCard: (data) => instance.post('cards', data),


};