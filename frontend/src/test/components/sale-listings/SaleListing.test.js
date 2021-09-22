import "@jest/globals";
import SaleListing from "@/components/sale-listing/SaleListing";
import {shallowMount} from '@vue/test-utils'
import product from "@/store/modules/product"
jest.mock('@/store/modules/product')

let wrapper
let listingData = {
    "currency": {
        "code": "AUD",
        "symbol": "$"
    },
    "id": 57,
        "inventoryItem": {
        "id": 101,
            "product": {
            "id": "WATT-420-BEANS",
                "name": "Watties Baked Beans - 420g can",
                "description": "Baked Beans as they should be.",
                "manufacturer": "Heinz Wattie's Limited",
                "recommendedRetailPrice": 2.2,
                "created": "2021-09-22T03:06:53.260Z",
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
            "manufactured": "2021-09-22",
            "sellBy": "2021-09-22",
            "bestBefore": "2021-09-22",
            "expires": "2021-09-22"
    },
    "quantity": 3,
    "price": 17.99,
    "moreInfo": "Seller may be willing to consider near offers",
    "featured": false,
    "created": "2021-07-14T11:44:00Z",
    "closes": "2021-07-21T23:59:00Z",
    "business": {"id": 100, address: {country: ""}}
}

describe("Tests for the SaleListing component", () => {

    beforeEach( async () => {
        product.addSaleListingCurrencies.mockResolvedValue([listingData])
        wrapper = shallowMount(SaleListing, {
            computed: {
                isAdminOfBusiness() {
                    return false
                },
            },
            propsData: {listingData}
        })
        await wrapper.vm.$nextTick()
    });

    test("Check the delete button doesn't exist when the user viewing is not a business admin", async () => {
        let removeButton = await wrapper.find("#removeButton")
        console.log(wrapper.vm.isAdminOfBusiness)
        console.log(removeButton)
        expect(removeButton.exists()).toBeFalsy()
    })

    test("Check the delete button exists when the user viewing is a business admin", async () => {
        product.addSaleListingCurrencies.mockResolvedValue([listingData])
        wrapper = await shallowMount(SaleListing, {
            computed: {
                isAdminOfBusiness() {
                    return true
                },
            },
            propsData: {listingData}
        })
        await wrapper.vm.$nextTick()
        await wrapper.vm.$nextTick()
        let removeButton = await wrapper.find("#removeButton")
        expect(removeButton.exists()).toBeTruthy()
    })

})