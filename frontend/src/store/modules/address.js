export default {
    debug: true,

    /**
     * Formats address of user or business by using their address object
     * @param address object that stores the users home address or businesses address
     * @returns {string}
     */
    formatAddress(address) {
        let addressString = ''
        if (address.city !== null && address.city !== "") addressString += `${address.city}, `
        if (address.region !== null && address.region !== "") addressString += `${address.region}, `
        if (address.country !== null && address.country !== "") addressString += `${address.country}`
        if (address.postcode !== null && address.postcode !== "") addressString += `, ${address.postcode}`
        return addressString
    },

    /**
     * Formats address of user or business by using their address object, and includes the street
     * @param address object that stores the users home address or businesses address
     * @returns {string}
     */
    formatAddressWithStreet(address) {
        let addressString = ''
        if (address.streetNumber !== null && address.streetNumber !== "" &&
            address.streetName !== null && address.streetName !== "") {
            addressString += `${address.streetNumber} ${address.streetName}, `
        }
        if (address.city !== null && address.city !== "") addressString += `${address.city}, `
        if (address.region !== null && address.region !== "") addressString += `${address.region}, `
        if (address.country !== null && address.country !== "") addressString += `${address.country}`
        if (address.postcode !== null && address.postcode !== "") addressString += `, ${address.postcode}`
        return addressString
    },
}
