import "@jest/globals";
import IndividualSaleListingModal from "@/components/sale-listing/IndividualSaleListingModal";

const VueTestUtils = require('@vue/test-utils')

jest.mock('@/Api')

const listing = {
    id: 1,
    inventoryItem: {
        product: {
            images: [],
            name: "Name"
        }
    },
    business: {
        id: 1,
        name: "Some Business"
    }
}

let wrapper;
// Setup before each test
beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(IndividualSaleListingModal, {
        propsData: {
            listing: listing
        },
        methods: {
            formatPrice() {
                return null
            },
            formatAddress() {
                return null
            }
        },
        computed: {
            isLoggedIn() {
                return true
            }
        }
    })
});


describe("Jest tests for the IndividualSaleListingModal", () => {

    test("Test that buyClicked is false when the page loads", () => {
        expect(wrapper.vm.$data.buyClicked).toBeFalsy()
    })

    test("Test that buyClicked is true (changing button appearance) when the buy button is clicked", () => {
        wrapper.find('#buyButton').trigger('click')
        expect(wrapper.vm.$data.buyClicked).toBeTruthy()
    })

})