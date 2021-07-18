/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import BrowseSaleListings from "@/components/sale-listing/BrowseSaleListings";

const VueTestUtils = require('@vue/test-utils')

let wrapper;
// Setup before each test
beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(BrowseSaleListings)
});

describe("Tests for the BrowseSaleListings component", () => {

    //Test that clicking on 'Product name' once sets checked to be true for 'Product name'
    test("Clicking 'Product name' sets Product name's checked to be true", async () => {
        await wrapper.find('#productName').trigger('click')
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.fieldOptions[0].checked).toBeTruthy()
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