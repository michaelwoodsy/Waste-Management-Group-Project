import "@jest/globals"
import BusinessProfile from "@/components/business/BusinessProfile"
import SaleListing from "@/components/sale-listing/SaleListing";
const VueTestUtils = require('@vue/test-utils')

let wrapper

let computed = {
    isLoggedIn() {
        return true
    },
}

let business = {
    "id": 100,
    "primaryAdministratorId": 20,
    "name": "Lumbridge General Store",
    "description": "A one-stop shop for all your adventuring needs",
    "businessType": "Accommodation and Food Services",
    "created": "2020-07-14T14:52:00Z",
    "images": []
}

describe('Jest tests for the BusinessProfile component', () => {

    // Setup before each test
    beforeEach(() => {
        jest.spyOn(BusinessProfile.methods, 'getPrimaryImage').mockImplementation(() => {})
        jest.spyOn(BusinessProfile.methods, 'formatAddress').mockImplementation(() => {})
        wrapper = VueTestUtils.shallowMount(BusinessProfile, {
            stubs: ['router-link', 'router-view', "login-required", "admin-required", 'sale-listing'],
            computed,
            propsData: {
                business: business
            }
            // mocks: {
            //     $route: {query: {}}
            // }
        })

    });

    test('A message is shown if the business has no featured listings',async () => {
        jest.spyOn(wrapper.vm, 'getFeaturedListings').mockImplementation(() => {return []})

        // check the text is correct
        const textEl = wrapper.find('#featuredListingText')
        expect(textEl.text()).toContain("no featured listings")
    })

    test('A sale listings aren\'t shown if the business has no featured listings',async () => {
        jest.spyOn(wrapper.vm, 'getFeaturedListings').mockImplementation(() => {return []})
        const saleListingEl = wrapper.findComponent({name: 'sale-listing'})
        expect(saleListingEl.exists()).toBeFalsy()
    })

    // Not currently working :(
    test('Featured listings are shown, if there are some',async () => {
        jest.spyOn(wrapper.vm, 'getFeaturedListings').mockImplementation(() => {return [{id: 1}]})
        const saleListingEl = wrapper.findComponent(SaleListing)
        console.log(saleListingEl)
        expect(saleListingEl.exists()).toBeTruthy()
    })
})

