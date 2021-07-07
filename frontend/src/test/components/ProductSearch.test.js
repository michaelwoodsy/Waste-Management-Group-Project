/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import ProductSearch from "@/components/product-catalogue/ProductSearch";

const VueTestUtils = require('@vue/test-utils')

let wrapper;
// Setup before each test
beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(ProductSearch)
});

describe("Tests for the ProductSearch component", () => {

    //Test that clicking on Id once sets checked to be true for Id
    test("Clicking Id sets Id's checked to be true", async () => {
        await wrapper.find('#Id').trigger('click')
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.fieldOptions[0].checked).toBeTruthy()
    })

    //Test that clicking on Name once sets checked to be false for Name
    test("Clicking Name sets Name's checked to be false", async () => {
        await wrapper.find('#Name').trigger('click')
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.fieldOptions[1].checked).toBeFalsy()
    })

    //Test that clicking on Description twice results in checked being false for Description
    test("Clicking Description twice sets Description's checked to be false", async () => {
        await wrapper.find('#Description').trigger('click')
        await wrapper.vm.$nextTick()
        await wrapper.find('#Description').trigger('click')
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.fieldOptions[2].checked).toBeFalsy()
    })
})