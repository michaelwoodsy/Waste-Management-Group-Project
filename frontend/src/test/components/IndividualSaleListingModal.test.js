/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import IndividualSaleListingModal from "@/components/sale-listing/IndividualSaleListingModal";
import {Business} from "@/Api"

const VueTestUtils = require('@vue/test-utils')

jest.mock('@/Api')

const listing = {
    id: 1,
    inventoryItem: {
        product: {
            images: {

            }
        }
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
            formatSeller() {
                return null
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

    test("Test the starListing method calls Business.starListing with false", () => {
        wrapper.vm.$data.stared = true
        wrapper.vm.starListing()
        expect(Business.starListing).toBeCalledWith(expect.any(Number), false)
    })

    test("Test the starListing method calls Business.starListing with true", () => {
        wrapper.vm.$data.stared = false
        wrapper.vm.starListing()
        expect(Business.starListing).toBeCalledWith(expect.any(Number), true)
    })


})