/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import BrowseSaleListings from "@/components/sale-listing/BrowseSaleListings";

const VueTestUtils = require('@vue/test-utils')

let wrapper;
// Setup before each test
beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(BrowseSaleListings, {
        computed: {
            isLoggedIn() {
                return true
            },
        }
    })
});


describe("Tests for the BrowseSaleListings checkboxes", () => {

    //Test that clicking on 'Product name' once sets checked to be false for 'Product name'
    //This is because this is the default search option
    test("Clicking 'Product name' sets Product name's checked to be true", async () => {
        await wrapper.find('#productName').trigger('click')
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.fieldOptions[0].checked).toBeFalsy()
    })

    //Test that clicking on 'Seller name' twice results in checked being false for 'Seller name'
    test("Clicking 'Seller name' twice sets Seller name's checked to be false", async () => {
        await wrapper.find('#sellerName').trigger('click')
        await wrapper.vm.$nextTick()
        await wrapper.find('#sellerName').trigger('click')
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.fieldOptions[2].checked).toBeFalsy()
    })

})


describe("Tests for the BrowseSaleListings price range", () => {

    test("Entering a null price lower bound is ok", async () => {
        wrapper.vm.$data.priceLowerBound = null
        await wrapper.vm.validatePrices()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.priceLowerBound)
            .toBeNull()
    })

    test("Entering a null price upper bound is ok", async () => {
        wrapper.vm.$data.priceUpperBound = null
        await wrapper.vm.validatePrices()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.priceUpperBound)
            .toBeNull()
    })

    test("Entering a price lower bound with 1 decimal place is ok", async () => {
        wrapper.vm.$data.priceLowerBound = 2.5
        await wrapper.vm.validatePrices()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.priceLowerBound)
            .toBeNull()
    })

    test("Entering a price upper bound with 2 decimal places is ok", async () => {
        wrapper.vm.$data.priceUpperBound = 2.50
        await wrapper.vm.validatePrices()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.priceUpperBound)
            .toBeNull()
    })

    test("Entering a price lower bound with 5 decimal places gives error", async () => {
        wrapper.vm.$data.priceLowerBound = 2.56235
        await wrapper.vm.validatePrices()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.priceLowerBound)
            .toStrictEqual("Please enter a valid price for the price's lower bound")
    })

    test("Entering letters for the price upper bound gives error", async () => {
        wrapper.vm.$data.priceUpperBound = "number"
        await wrapper.vm.validatePrices()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.priceUpperBound)
            .toStrictEqual("Please enter a valid price for the price's upper bound")
    })

    test("Entering an upper bound that is less than the lower bound gives an error", async () => {
        wrapper.vm.$data.priceLowerBound = 7.50
        wrapper.vm.$data.priceUpperBound = 4.50
        await wrapper.vm.validatePrices()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.priceUpperBound)
            .toStrictEqual("The price's upper bound is less than the lower bound")
    })

})

describe("Tests for the BrowseSaleListings closing date range", () => {

    test("Entering a closing date lower bound in the past gives an error", async () => {
        const date = new Date()
        date.setDate(date.getDate()-2)
        wrapper.vm.$data.closingDateLowerBound = date
        await wrapper.vm.validateDates()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.closingDateLowerBound)
            .toStrictEqual("Please enter a date in the future for the closing date's lower bound")
    })

    test("Entering a closing date upper bound in the past gives an error", async () => {
        const date = new Date()
        date.setDate(date.getDate()-2)
        wrapper.vm.$data.closingDateUpperBound = date
        await wrapper.vm.validateDates()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.closingDateUpperBound)
            .toStrictEqual("Please enter a date in the future for the closing date's upper bound")
    })

    test("Entering a closing date upper bound earlier than the lower bound gives an error", async () => {
        const lowerBoundDate = new Date()
        lowerBoundDate.setDate(lowerBoundDate.getDate()+3)
        wrapper.vm.$data.closingDateLowerBound = lowerBoundDate

        const upperBoundDate = new Date()
        upperBoundDate.setDate(upperBoundDate.getDate()+2)
        wrapper.vm.$data.closingDateUpperBound = upperBoundDate

        await wrapper.vm.validateDates()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.closingDateUpperBound)
            .toStrictEqual("The closing date's upper bound is earlier than the lower bound")
    })

    test("Entering a valid closing date lower bound is ok", async () => {
        const date = new Date()
        date.setDate(date.getDate()+2)
        wrapper.vm.$data.closingDateLowerBound = date
        await wrapper.vm.validateDates()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.closingDateLowerBound)
            .toBeNull()
    })

    test("Entering a valid closing date upper bound is ok", async () => {
        const date = new Date()
        date.setDate(date.getDate()+2)
        wrapper.vm.$data.closingDateUpperBound = date
        await wrapper.vm.validateDates()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.closingDateUpperBound)
            .toBeNull()
    })

    test("Entering a null closing date lower bound is ok", async () => {
        wrapper.vm.$data.closingDateLowerBound = null
        await wrapper.vm.validateDates()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.closingDateLowerBound)
            .toBeNull()
    })

    test("Entering a null closing date upper bound is ok", async () => {
        wrapper.vm.$data.closingDateUpperBound = null
        await wrapper.vm.validateDates()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.closingDateUpperBound)
            .toBeNull()
    })
})

describe("tests the browse sale listing methods", () => {

    beforeEach(() => {
        wrapper.vm.$data.listings = [
            {
                "id": 57,
                "inventoryItem": {
                    "id": 101,
                    "product": {
                        "id": "WATT-420-BEANS",
                        "name": "Watties Baked Beans - 420g can",
                        "description": "Baked Beans as they should be.",
                        "manufacturer": "Heinz Wattie's Limited",
                        "recommendedRetailPrice": 2.2,
                        "created": "2021-08-04T05:36:43.910Z",
                        "images": [
                            {
                                "id": 1234,
                                "filename": "/media/images/23987192387509-123908794328.png",
                                "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                            }
                        ],
                        "currencyCountry": "string"
                    },
                    "quantity": 4,
                    "pricePerItem": 6.5,
                    "totalPrice": 21.99,
                    "manufactured": "2021-08-04",
                    "sellBy": "2021-08-04",
                    "bestBefore": "2021-08-04",
                    "expires": "2021-08-04"
                },
                "quantity": 3,
                "price": 17.99,
                "moreInfo": "Seller may be willing to consider near offers",
                "created": "2021-07-14T11:44:00Z",
                "closes": "2021-07-21T23:59:00Z",
                "business": {
                    "id": 100,
                    "administrators": [
                        {
                            "id": 100,
                            "firstName": "John",
                            "lastName": "Smith",
                            "middleName": "Hector",
                            "nickname": "Jonny",
                            "bio": "Likes long walks on the beach",
                            "email": "johnsmith99@gmail.com",
                            "dateOfBirth": "1999-04-27",
                            "phoneNumber": "+64 3 555 0129",
                            "homeAddress": {
                                "streetNumber": "3/24",
                                "streetName": "Ilam Road",
                                "suburb": "Upper Riccarton",
                                "city": "Christchurch",
                                "region": "Canterbury",
                                "country": "New Zealand",
                                "postcode": "90210"
                            },
                            "created": "2020-07-14T14:32:00Z",
                            "role": "user",
                            "businessesAdministered": [
                                "string"
                            ]
                        }
                    ],
                    "primaryAdministratorId": 20,
                    "name": "Lumbridge General Store",
                    "description": "A one-stop shop for all your adventuring needs",
                    "address": {
                        "streetNumber": "3/24",
                        "streetName": "Ilam Road",
                        "suburb": "Upper Riccarton",
                        "city": "Christchurch",
                        "region": "Canterbury",
                        "country": "New Zealand",
                        "postcode": "90210"
                    },
                    "businessType": "Accommodation and Food Services",
                    "created": "2020-07-14T14:52:00Z"
                }
            }
        ]
    })

    test("tests that the sale listing to be viewed is saved", async () => {
        const listing = wrapper.vm.$data.listings[0]
        wrapper.vm.viewListing(listing)
        expect(wrapper.vm.$data.viewListingModal).toBeTruthy()
        expect(wrapper.vm.$data.viewBusinessModal).toBeFalsy()
        expect(wrapper.vm.$data.listingToView).toStrictEqual(listing)
    })

    test("tests that the sale listing modal appears", async () => {
        const listing = wrapper.vm.$data.listings[0]
        wrapper.vm.viewListing(listing)
        expect(wrapper.find("#viewListingModal")).toBeTruthy()
    })

})

