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
        if (price) {
            return `${currency.symbol}${price} ${currency.code}`
        } else {
            return null;
        }
    }
}
