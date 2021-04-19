import axios from "axios";

export default {
    debug: true,
    //Currencies that have been retrieved in the past

    /**
     * Gets the correct currency from restcountries and returns it. correct currency is retrieved from the country field
     * @param country country string from the businesses address used to search for the country on restcountries
     * @returns json filled with the currencies code and symbol
     */
    async getCurrency(country) {
        const promise = await new Promise((resolve, reject) => {
            axios.get(`https://restcountries.eu/rest/v2/name/${country}`)
                .then((response) => {
                    if (response.status === 404) {
                        console.log(`No country found with name '${country}'`)
                    }
                    if (response.data.length === 1) {
                        resolve(response)
                    } else {
                        reject(response)
                    }
                })
                .catch((err) => {
                    reject(err)
                })
        })
        return {"code": promise.data[0].currencies[0].code,
            "symbol": promise.data[0].currencies[0].symbol}
    },

    /**
     * formats price with a symbol infront and the conutry code after, e.g '$30 NZD'
     * @param currency JSON containing a code, e.g 'NZD' and a symbol, e.g '$'
     * @param price price of the product
     * @returns formated string of price, e.g '$30 NZD'
     */
    formatPrice(currency, price) {
        return `${currency.symbol}${price} ${currency.code}`
    }
}
