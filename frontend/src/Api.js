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
    /**
     * Creates a new user
     * @param data information for new user account
     * @returns {Promise<AxiosResponse<any>>} response containing users ID
     */
    createNew: (data) => instance.post('users', data),

    /**
     * Edits a user
     * @param id The ID of the user that is going to be edited
     * @param editUserJSON The edited user data
     * @returns {Promise<AxiosResponse<any>>}
     */
    editUser: (id, editUserJSON) => instance.put(`users/${id}`, editUserJSON),

    /**
     * Log a user in
     * @param username the username (email) of the account the user is trying to log in with
     * @param password the password of the account the user is trying to log in with
     * @returns {Promise<AxiosResponse<any>>} response containing the user ID of the user that is now logged in
     */
    login: (username, password) => instance.post('login', {username, password}),

    /**
     * Retrieves a user's data
     * @param id The ID of the user you are trying to get information about
     * @returns {Promise<AxiosResponse<any>>} response containing the user with its information
     */
    getUserData: (id) => instance.get(`users/${id}`, {}),

    /**
     * Searches for users according to a search term and sort term
     * @param searchTerm The query that is used to find particular users
     * @param pageNumber The page number used by the backend pagination
     * @param sortBy The sort criteria used by the backend pagination
     * @returns {Promise<AxiosResponse<any>>} response containing all the users that match the search criteria
     * in order given by the sortBy term, along with the total number of users found
     */
    getUsers: (searchTerm, pageNumber, sortBy) => instance.get('users/search', {
        params: {
            'searchQuery': searchTerm,
            'pageNumber': pageNumber, 'sortBy': sortBy
        }
    }),

    /**
     * Promotes a user account to the Global Application Admin role
     * @param id The ID of the user that will become a GAA
     * @returns {Promise<AxiosResponse<any>>}
     */
    makeAdmin: (id) => instance.put(`users/${id}/makeadmin`),

    /**
     * Revokes the Global Application Admin role from a user account
     * @param id The ID of the user that will no longer be a GAA
     * @returns {Promise<AxiosResponse<any>>}
     */
    revokeAdmin: (id) => instance.put(`users/${id}/revokeadmin`),

    /**
     * Creates a new Card for in the community marketplace
     * @param data
     */
    createCard: (data) => instance.post('cards', data),

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
     * Sets a message to read/unread
     *
     * @param userId user ID of whom which the message is for
     * @param messageId ID of the message
     * @param read boolean of whether the message is read or not
     * @returns {Promise<AxiosResponse<any>>} response with status code
     */
    readMessage: (userId, messageId, read) =>
        instance.patch(
            `users/${userId}/messages/${messageId}/read`,
            {read: read}
        ),

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
     * Sets a notification ot read/unread
     *
     * @param userId user ID of whom the notification is for
     * @param notificationId ID of the notification
     * @param read boolean of whether or not the notification is read
     * @returns {Promise<AxiosResponse<any>>} response with status code
     */
    readNotification: (userId, notificationId, read) =>
        instance.patch(
            `users/${userId}/notifications/${notificationId}/read`,
            {read: read}
        ),

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
     * Sets an admin notification to read/unread
     * @param notificationId ID of the notification
     * @param read boolean of whether or not the notification is read
     * @returns {Promise<AxiosResponse<any>>} response with status code
     */
    readAdminNotification: (notificationId, read) =>
        instance.patch(
            `notifications/${notificationId}/read`,
            {read: read}
        ),

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
    makePrimaryImage: (userId, imageId) => instance.put(`users/${userId}/images/${imageId}/makeprimary`),

    /**
     * Sends a request to validate a lost password token used when resetting your password
     * @param token reset password token from the user
     * @returns {Promise<AxiosResponse<any>>}
     */
    validateLostPasswordToken: (token) => instance.get(`lostpassword/validate?token=${token}`),

    /**
     * Sends a request to edit a lost password for the user that the token links to
     * @param token         reset password token from the user
     * @param password   new password for the user
     * @returns {Promise<AxiosResponse<any>>}
     */
    editLostPassword: (token, password) => instance.patch(`lostpassword/edit`, {token, password}),

    /**
     * Sends a request to send an email to the given email to reset the users password
     * @param email the user's email used to send a password reset email to
     * @returns {Promise<AxiosResponse<any>>}
     */
    sendPasswordResetEmail: (email) => instance.post('lostpassword/send', {email}),

    /**
     * Sends a request to tag a liked sale listing
     *
     * @param listingId ID of listing to tag
     * @param name Name of the tag
     * @returns {Promise<AxiosResponse<any>>} response from request
     */
    tagListing: (listingId, name) => instance.patch(`listings/${listingId}/tag`,
        {
            tag: name
        }
    ),

    /**
     * Sends a request to leave a review on a sale
     *
     * @param userId ID of user who purchased the sale
     * @param saleId ID of the sale to leave a review for
     * @param review object containing review rating and message
     */
    leaveReview: (userId, saleId, review) => instance.post(`users/${userId}/purchases/${saleId}/review`,
        review
    )

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
     * @param pageNumber Page number to retrive
     * @param sortBy sorting criteria
     * @returns {Promise<AxiosResponse<any>>} Response from request
     */
    getBusinesses: (searchTerm, businessSearchType, pageNumber, sortBy) => instance.get('businesses/search', {
        params: {
            'searchQuery': searchTerm,
            'businessType': businessSearchType,
            'pageNumber': pageNumber,
            'sortBy': sortBy
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
     * @param updateProductCurrency Boolean, updates the existing products with the new countries currency
     * @returns {Promise<AxiosResponse<any>>} response from request
     */
    editBusiness: (businessId, newData, updateProductCurrency) =>
        instance.put(`businesses/${businessId}`, newData, {
            params:
                {updateProductCurrency: updateProductCurrency === true}
        }),


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
        {
            params:
                {
                    'searchQuery': searchQuery,
                    'matchingId': matchingId,
                    'matchingName': matchingName,
                    'matchingDescription': matchingDescription,
                    'matchingManufacturer': matchingManufacturer
                }
        }),

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
     * @param businessId The ID of the business in the database
     * @param imageId The ID of the image for the product in the database
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    removeBusinessImage: (businessId, imageId) => instance.delete(`businesses/${businessId}/images/${imageId}`),

    /**
     * Sends a request to make a specific image the primary image of a specific business
     * @param businessId The ID of the business in the database
     * @param imageId The ID of the image for the product in the database
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    makePrimaryBusinessImage: (businessId, imageId) => instance.put(`businesses/${businessId}/images/${imageId}/makeprimary`),


    /**
     * requests to get sales listings matching supplied properties
     * @param params query parameters for browsing slae listings
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    searchSaleListings: (params) => instance.get(`listings`, {
        params
    }),

    /**
     * Sends a request to purchase a specific listing
     * @param listingId The ID of the listing to purchase
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    purchaseListing: (listingId) => instance.post(`/listings/${listingId}/buy`),

    /**
     * Sends a request to like a specific listing
     * @param listingId The ID of the listing to like
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    likeListing: (listingId) => instance.patch(`/listings/${listingId}/like`),

    /**
     * Sends a request to unlike a specific listing
     * @param listingId The ID of the listing to unlike
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    unlikeListing: (listingId) => instance.patch(`/listings/${listingId}/unlike`),

    /**
     * Sends a request to star or un-star a sale listing.
     * @param listingId Id of the sale listing to star.
     * @param value Boolean, sets the listing to starred if true, and un-starred if false.
     * @returns {Promise<AxiosResponse<any>>} Response from  the request
     */
    starListing: (listingId, value) => instance.patch(`/listings/${listingId}/star`, {star: value}),

    /**
     * Gets a sales report for a specififed business
     *
     * @param businessId ID of business to generate sales report for
     * @param params query parameters specifying date range and granularity
     * @returns {Promise<AxiosResponse<any>>} response containing sales report
     */
    getSalesReport: (businessId, params) =>
        instance.get(`/businesses/${businessId}/sales`, {
            params
        }),

    /**
     * Sends a request to feature/un-feature a listing
     * @param businessId Id of the business the listing belongs to
     * @param listingId Id of the listing to feature
     * @param featured The featured boolean, true for featured, false for not featured
     * @returns {Promise<AxiosResponse<any>>} Response from the request
     */
    featureListing: (businessId, listingId, featured) =>
        instance.patch(`/businesses/${businessId}/listings/${listingId}/feature`, {featured}),

    /**
     * Gets a business' featured listings from the backend.
     * @param businessId Id of the business.
     * @returns {Promise<AxiosResponse<any>>} List of sale listings.
     */
    getFeaturedListings: (businessId) => instance.get(`/businesses/${businessId}/featuredlistings`),
};

export const Card = {
    /**
     * Get all cards from the market place from a particular section (For Sale, Wanted, or Exchange)
     * @param section The particular section you want the cards for
     * @param page The page number to display results from
     * @param sortBy String representing sort field and direction
     */
    getCardsSection: (section, page, sortBy) =>
        instance.get(`cards`, {params: {section, page, sortBy}}),

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
    searchCards: (params) => instance.get(`cards/search${params}`, {}),

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

export const Statistics = {
    /**
     * Retrieves statistics about resale.
     */
    getStatistics: () => instance.get(`statistics`)
}

export const Landing ={
    /**
     * Retrieves the popular listings from the backend
     * @param country The parameter for popular listings in that country
     * @returns {Promise<AxiosResponse<any>>} response with popular sale listings
     */
    getPopularListings: (country) => instance.get("popularlistings", {params: {'country': country}})
}
