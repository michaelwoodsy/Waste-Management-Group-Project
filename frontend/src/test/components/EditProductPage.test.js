/**
 * @jest-environment jsdom
 */

import {afterEach, beforeEach, describe, test} from "@jest/globals";
const VueTestUtils = require('@vue/test-utils')
import EditProductPage from '@/components/EditProductPage';
import Vue from 'vue';


// Define business and product to mock
let businessId = 2
let productId = "WATT-420-BEANS"
let loggedIn = true
// Will become the fake vue instance
let wrapper;
let computed = {
    isLoggedIn() { return loggedIn },
    businessId() { return businessId },
    productId() { return productId },
    businessesAdministered() { return [{id: 2}] },
    isAdminOfBusiness() { return businessId === 2 },
    changesMade() { return false }
}

// Mock the business api module, once implemented
jest.mock('@/Api', () => ({
    'Business': {}
}));

// Setup before each test
beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(EditProductPage, {
        stubs: ['router-link', 'router-view', "login-required", "admin-required"],
        computed
    })
});

// Reset any test variables that might have been changed
afterEach(() => {
    businessId = 2
    productId = "WATT-420-BEANS"
    loggedIn = true
})

describe('EditProductPage Component Tests', () => {

    // Test the title is there
    test('displays title', () => {
        expect(wrapper.find('h1').text()).toBe('Edit Product')
    })

    // Test a login required message is shown when not logged in
    test("a message is shown when the user isn't logged in", () => {
        loggedIn = false;
        wrapper = VueTestUtils.shallowMount(EditProductPage, {
            stubs: ['router-link', 'router-view', "login-required", "admin-required"],
            computed
        })
        expect(wrapper.findComponent({ name: 'login-required' }).exists()).toBeTruthy()
    })

    // Test an admin required message is shown when the user is not an admin of the business
    test("a message is shown when the user isn't an admin of the business", () => {
        businessId = 3;
        wrapper = VueTestUtils.shallowMount(EditProductPage, {
            stubs: ['router-link', 'router-view', "login-required", "admin-required"],
            computed
        })
        expect(wrapper.findComponent({ name: 'admin-required' }).exists()).toBeTruthy()
    })

    // Check the input fields are prefilled
    test('input fields are prefilled with data', () => {
        expect(wrapper.find('#id').element.value.length).toBeGreaterThan(2)
        expect(wrapper.find('#name').element.value.length).toBeGreaterThan(2)
        expect(wrapper.find('#description').element.value.length).toBeGreaterThan(2)
        expect(wrapper.find('#rrp').element.value.length).toBeGreaterThan(2)
        expect(wrapper.find('#manufacturer').element.value.length).toBeGreaterThan(2)
    })

    // Check an error message is displayed if the product doesn't exist
    test("a message is shown when the product doesn't exist", async() => {
        productId = "Non existant";
        wrapper = VueTestUtils.shallowMount(EditProductPage, {
            stubs: ['router-link', 'router-view', "login-required", "admin-required", "alert"],
            computed
        })
        await Vue.nextTick() // Otherwise the loading ... message is displayed

        const alertComponent = wrapper.findComponent({ name: 'alert' })
        expect(alertComponent.exists()).toBeTruthy()
        expect(alertComponent.text()).toContain("no product")
    })

    // Check the idValid computed field
    test("testing idField computed property", async() => {
        const fakeId = (id) => {return { newProduct: {id: id }}}
        expect(EditProductPage.computed.idValid.call(fakeId("Baked Beans"))).toBeFalsy()
        expect(EditProductPage.computed.idValid.call(fakeId(""))).toBeFalsy()
        expect(EditProductPage.computed.idValid.call(fakeId("Baked"))).toBeTruthy()
        expect(EditProductPage.computed.idValid.call(fakeId("Baked-beans123-"))).toBeTruthy()
    })

    // Check the nameValid computed field
    test("testing nameValid computed property", async() => {
        const fakeName = (name) => {return { newProduct: {name: name }}}
        expect(EditProductPage.computed.nameValid.call(fakeName("Myrtle's Motorcycles"))).toBeTruthy()
        expect(EditProductPage.computed.nameValid.call(fakeName(""))).toBeFalsy()
    })

    // Check the priceValid computed field
    test("testing priceValid computed property", async() => {
        const fakePrice = (price) => {return { newProduct: {recommendedRetailPrice: price }}}
        expect(EditProductPage.computed.priceValid.call(fakePrice(1.22))).toBeTruthy()
        expect(EditProductPage.computed.priceValid.call(fakePrice(1))).toBeTruthy()
        expect(EditProductPage.computed.priceValid.call(fakePrice(1.00))).toBeTruthy()
        expect(EditProductPage.computed.priceValid.call(fakePrice(1.555))).toBeFalsy()
        expect(EditProductPage.computed.priceValid.call(fakePrice(""))).toBeTruthy()
        expect(EditProductPage.computed.priceValid.call(fakePrice("a"))).toBeFalsy()
        expect(EditProductPage.computed.priceValid.call(fakePrice(" "))).toBeFalsy()
    })
})