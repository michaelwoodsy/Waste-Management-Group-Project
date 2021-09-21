import "@jest/globals"
import BusinessProfile from "@/components/business/BusinessProfile"
const VueTestUtils = require('@vue/test-utils')
import {Business} from "@/Api";
jest.mock('@/Api')

let wrapper

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
        Business.getFeaturedListings.mockReturnValue({data: [{}]})
        wrapper = VueTestUtils.shallowMount(BusinessProfile, {
            stubs: ['router-link', 'router-view', "login-required", "admin-required"],
            computed: {isLoggedIn() { return true }},
            propsData: {
                business: business
            }
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

    test('Featured listings are shown, if there are some', async () => {
        jest.spyOn(wrapper.vm, 'getFeaturedListings').mockImplementation(() => {})
        await wrapper.setData({featuredListings: [{id: 1}]})
        const saleListingEl = wrapper.findComponent({name: 'sale-listing'})
        expect(saleListingEl.exists()).toBeTruthy()
    })

    test('Check the getFeaturedListings method sends a request to the backend',  async () => {
        await wrapper.vm.getFeaturedListings()
        expect(Business.getFeaturedListings).toBeCalledWith(expect.any(Number))
    })

})

