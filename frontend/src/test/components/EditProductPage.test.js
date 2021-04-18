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
    changesMade() { return false },
    cancelBtnClass() {
        return {
            "btn": true,
            "mr-1": true,
            "my-1": true,
            "btn-danger": this.changesMade,
            "btn-link": !this.changesMade,
            "float-left": true
        }
    }
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
    test("a message is shown when the user isn't an admin of the business", async() => {
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
})