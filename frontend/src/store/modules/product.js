import axios from "axios";

export default {
    debug: true,

    /**
     * Gets the correct currency from restcountries and returns it. correct currency is retrieved from the country field
     * @param country country string from the businesses address used to search for the country on restcountries
     * @returns json filled with the currencies code and symbol
     */
    async getCurrency(country) {
        //This should always get a currency as the country with its currency being retrieved properly
        // is a requirement for creating a business
        const promise = await new Promise((resolve, reject) => {
            axios.get(`https://restcountries.eu/rest/v2/name/${country}`)
                .then((response) => {
                    if (response.status === 404) {
                        console.log(`No country found with name '${country}'`)
                        reject(promise)
                    }
                    if (response.data.length >= 1) {
                        resolve(response)
                    } else {
                        reject(response)
                    }
                })
                .catch((err) => {
                    //console.log(err.response.data)
                    reject(err)
                })
        })
        return {
            "code": promise.data[0].currencies[0].code,
            "symbol": promise.data[0].currencies[0].symbol
        }
    },

    /**
     * formats price with a symbol infront and the conutry code after, e.g '$30 NZD'
     * @param currency JSON containing a code, e.g 'NZD' and a symbol, e.g '$'
     * @param price price of the product
     * @returns formatted string of price, e.g '$30 NZD' or null if price does not exist.
     */
    formatPrice(currency, price) {
        if (price !== null) {
            return `${currency.symbol}${price.toFixed(2)} ${currency.code}`
        } else {
            return null;
        }
    },

    /**
     * Takes a list of products, and adds currency object to them.
     * @param products List of product objects.
     * @param businessCurrency The location of the business the product belongs to.
     * @returns {*[]} List of product objects with currency field added.
     */
    async addProductCurrencies(products, businessCurrency) {
        let currenciesToFind = {}

        // Iterate over all products and find the currency countries that need to be found
        for (let product of products) {
            let currency = null;
            if (product.currencyCountry) {
                currency = product.currencyCountry
                currenciesToFind[currency] = null
            }
        }

        // Iterate over the countries and find the currency
        for (let country of Object.keys(currenciesToFind)) {
            currenciesToFind[country] = await this.getCurrency(country)
        }

        // Add the found currencies to the objects
        for (let product of products) {
            if (product.currencyCountry) {
                product.currency = currenciesToFind[product.currencyCountry]
            } else {
                product.currency = businessCurrency
            }

        }

        return products
    },

    /**
     * Takes a list of inventory items, and adds currency object to them.
     * @param items List of inventory item objects.
     * @returns {*[]} List of product objects with currency field added.
     */
    async addInventoryItemCurrencies(items, businessCurrency) {
        let currenciesToFind = {}

        // Iterate over all products and find the currency countries that need to be found
        for (let item of items) {
            let currency = null;
            if (item.product.currencyCountry) {
                currency = item.product.currencyCountry
                currenciesToFind[currency] = null
            }
        }

        // Iterate over the countries and find the currency
        for (let country of Object.keys(currenciesToFind)) {
            currenciesToFind[country] = await this.getCurrency(country)
        }

        // Add the found currencies to the objects
        for (let item of items) {
            if (item.product.currencyCountry) {
                item.currency = currenciesToFind[item.product.currencyCountry]
            } else {
                item.currency = businessCurrency
            }

        }

        return items
    },

    /**
     * Takes a list of sale listings, and adds currency object to them.
     * @param listings List of sale listing objects.
     * @param businessCountry Country of the business (could be undefined if coming from the browse sales listings page)
     * @returns {*[]} List of product objects with currency field added.
     */
    async addSaleListingCurrencies(listings, businessCountry) {
        let currenciesToFind = {}

        // Iterate over all products and find the currency countries that need to be found
        for (let listing of listings) {
            let currency = null;
            if (listing.business.address === undefined) {
                listing.business.address = {}
                listing.business.address.country = businessCountry
            }
            if (listing.inventoryItem.product.currencyCountry) {
                currency = listing.inventoryItem.product.currencyCountry
            } else {
                currency = listing.business.address.country
            }
            if (!(currency in currenciesToFind)) {
                currenciesToFind[currency] = null
            }
        }

        // Iterate over the countries and find the currency
        for (let country of Object.keys(currenciesToFind)) {
            currenciesToFind[country] = await this.getCurrency(country)
        }

        // Add the found currencies to the objects
        for (let listing of listings) {
            listing.currency = currenciesToFind[listing.inventoryItem.product.currencyCountry || listing.business.address.country]
        }

        return listings
    }
}
