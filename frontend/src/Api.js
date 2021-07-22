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
    timeout: 20000,
    withCredentials: true
});

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

    editUser: (id, editUserJSON) => instance.put(`users/${id}`, editUserJSON),

    login: (username, password) => instance.post('login', {username, password}),

    getUserData: (id) => instance.get(`users/${id}`, {}),

    getUsers: (searchTerm) => instance.get('users/search', {params: {'searchQuery': searchTerm}}),

    makeAdmin: (id) => instance.put(`users/${id}/makeadmin`),

    revokeAdmin: (id) => instance.put(`users/${id}/revokeadmin`),

    /**
     * Creates a new Card for in the community marketplace
     * @param data
     */
    createCard: (data) => instance.post('cards', data),

    /**
     * Get all cards from the market place from a particular section (For Sale, Wanted, or Exchange)
     * @param section The particular section you want the cards for
     */
    getCardsSection: (section) => instance.get(`cards`, {params: {'section': section}}),

    /**
     * Gets a user's cards from the backend
     * @param userId User ID to get cards from
     * @returns {Promise<AxiosResponse<any>>} response containing user's cards
     */
    getCards: (userId) => instance.get(`users/${userId}/cards`),

    /**
     * Sends a message to a user regarding a card.
     *
     * @param userId ID of the user to send the message to.
     * @param cardId ID of the card the message is about.
     * @param text The contents of the message
     * @returns {Promise<AxiosResponse<any>>} response containing message ID.
     */
    sendCardMessage: (userId, cardId, text) => instance.post(
        `users/${userId}/cards/${cardId}/messages`,
        {
            text: text
        }
    ),

    /**
     * Gets a user's messages
     *
     * @param userId ID of the user of whom to get messages of
     * @returns {Promise<AxiosResponse<any>>} List of user's messages
     */
    getMessages: (userId) => instance.get(`users/${userId}/messages`),

    /**
     * Deletes a user's messages
     *
     * @param userId ID of the user of whom to delete message of
     * @param messageId ID of the message that is to be deleted
     * @returns {Promise<AxiosResponse<any>>} List of user's messages
     */
    deleteMessage: (userId, messageId) => instance.delete(`users/${userId}/messages/${messageId}`),

    /**
     * Gets a user's notifications from the backend
     * @param userId User ID to get notifications from
     * @returns {Promise<AxiosResponse<any>>} response containing user's notifications
     */
    getNotifications: (userId) => instance.get(`users/${userId}/notifications`),

    /**
     * Deletes a user's notification from the backend
     *
     * @param userId User ID of whom to delete notification for
     * @param notificationId ID of the notification to delete
     */
    deleteNotification: (userId, notificationId) =>
        instance.delete(`users/${userId}/notifications/${notificationId}`),

    /**
     * Gets all admin notifications from the backend
     *
     * @returns {Promise<AxiosResponse<any>>} response containing admin notifications
     */
    getAdminNotifications: () => instance.get(`notifications`),

    /**
     * Deletes and admin notification from the backend
     *
     * @param notificationId ID of the notification to delete
     * @returns {Promise<AxiosResponse<any>>} response with status code
     */
    deleteAdminNotification: (notificationId) =>
        instance.delete(`notifications/${notificationId}`),

    /**
     * Adds an image to a user
     * @param userId the id of the user to add an image for
     * @param file the image file being uploaded by the user
     */
    addImage: (userId, file) => instance.post(
        `users/${userId}/images`, file, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }),

    /**
     * Sends a request to delete a specific image for a specific user
     * @param userId The ID of the user in the database
     * @param imageId The ID of the image for the product in the database
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    removeImage: (userId, imageId) => instance.delete(`users/${userId}/images/${imageId}`),

    /**
     * Sends a request to make a specific image the primary image of a specific user
     * @param userId The ID of the user in the database
     * @param imageId The ID of the image for the product in the database
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    makePrimaryImage: (userId, imageId) => instance.put(`users/${userId}/images/${imageId}/makeprimary`)

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

    /**
     * Get back all businesses with a certain search term and business type
     * @param searchTerm Criteria to search businesses for, e.g: businesses full name or part of a name
     * @param businessSearchType Criteria to limit the search for only businesses with this type
     * @returns {Promise<AxiosResponse<any>>} Response from request
     */
    getBusinesses: (searchTerm, businessSearchType) => instance.get('businesses/search', {
        params: {
            'searchQuery': searchTerm,
            'businessType': businessSearchType
        }
    }),

    /*
     * Retrieves the data for a given business
     */
    getBusinessData: (id) => instance.get(`businesses/${id}`, {}),

    /**
     * Makes a request to update the details of a business
     *
     * @param businessId ID of the business to update
     * @param newData new details to update business with
     * @returns {Promise<AxiosResponse<any>>} response from request
     */
    editBusiness: (businessId, newData) => instance.put(`businesses/${businessId}`, newData),


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
     * Adds an image to a product
     */
    addProductImage: (businessId, productId, file) => instance.post(
        `businesses/${businessId}/products/${productId}/images`, file, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }),

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
     * Sends a request to delete a specific image for a specific product in the catalogue
     * @param businessId The ID of the business in the database
     * @param productId The ID of the product in the database
     * @param imageId The ID of the image for the product in the database
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    removeProductImage: (businessId, productId, imageId) => instance.delete(`businesses/${businessId}/products/${productId}/images/${imageId}`),

    /**
     * Sends a request to make a specific image the primary image of a specific product in the catalogue
     * @param businessId The ID of the business in the database
     * @param productId The ID of the product in the database
     * @param imageId The ID of the image for the product in the database
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    makePrimaryProductImage: (businessId, productId, imageId) => instance.put(`businesses/${businessId}/products/${productId}/images/${imageId}/makeprimary`),

    /**
     * Sends a request to search a business's product catalogue
     * @param businessId the id of the business to search the catalogue of
     * @param searchQuery the string to search by
     * @param matchingId Whether the Id field is being searched by
     * @param matchingName Whether the Name field is being searched by
     * @param matchingDescription Whether the Description field is being searched by
     * @param matchingManufacturer Whether the Manufacturer field is being searched by
     */
    searchProducts: (businessId, searchQuery, matchingId, matchingName, matchingDescription,
                     matchingManufacturer) => instance.get(
        `businesses/${businessId}/products/search`,
        {params:
                {
                    'searchQuery': searchQuery,
                    'matchingId': matchingId,
                    'matchingName': matchingName,
                    'matchingDescription': matchingDescription,
                    'matchingManufacturer': matchingManufacturer
                }}),

    /**
     * Adds an image to a business
     */
    addBusinessImage: (businessId, file) => instance.post(
        `businesses/${businessId}/images`, file, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }),

    /**
     * Sends a request to delete a specific image for a specific business
     * @param userId The ID of the business in the database
     * @param imageId The ID of the image for the product in the database
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    removeBusinessImage: (businessId, imageId) => instance.delete(`businesses/${businessId}/images/${imageId}`),

    /**
     * Sends a request to make a specific image the primary image of a specific business
     * @param userId The ID of the business in the database
     * @param imageId The ID of the image for the product in the database
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    makePrimaryBusinessImage: (businessId, imageId) => instance.put(`businesses/${businessId}/images/${imageId}/makeprimary`)
};

export const Card = {
    /**
     * Extends the display period of a card that is nearing expiry
     * @param cardId The ID of the card in the database
     */
    extendDisplay: (cardId) => instance.put(`cards/${cardId}/extenddisplayperiod`, {cardId}),

    /**
     * Retrieves all the data for a given card
     * @param cardId The ID of the card in the database
     */
    getCard: (cardId) => instance.get(`cards/${cardId}`, {}),

    /**
     * Searches for cards by keyword
     * @param params parameters to search by, the keywords, the section, and whether to match all keywords or only some
     * @returns {Promise<AxiosResponse<any>>} Response from request
     */
    searchCards: (params) => instance.get(`cards/search/${params}`, {}),

    /**
     * Edits a card
     * @param cardId the id of the card to be edited
     * @param newCardData the new data for the card
     */
    editCard: (cardId, newCardData) => instance.put(`cards/${cardId}`, newCardData),

    /**
     * Sends a marketplace card deletion request to the api.
     * @param cardId Id of the card to delete
     * @returns {Promise<AxiosResponse<any>>} Response from request
     */
    deleteCard: (cardId) => instance.delete(`cards/${cardId}`)
};

export const Keyword = {

    /**
     * Request to create a new keyword.
     *
     * @param name the new keyword to add.
     */
    createKeyword: (name) => instance.post('keywords', {name}),

    /**
     * Searches keywords for matches to a partial keyword
     * @param partialKeyword the string to search by
     */
    searchKeywords: (partialKeyword) =>
        instance.get(`keywords/search`, {params: {'searchQuery': partialKeyword}}),

    /**
     * Deletes a keyword by ID
     *
     * @param keywordId the ID of the keyword to delete
     * @returns {Promise<AxiosResponse<any>>} response with status code
     */
    deleteKeyword: (keywordId) =>
        instance.delete(`keywords/${keywordId}`)
}

export const Images = {
    /**
     * Retrieves the image at the path.
     * @param path Path to the image
     */
    getImageURL: (path) => SERVER_URL + path.slice(1)
}