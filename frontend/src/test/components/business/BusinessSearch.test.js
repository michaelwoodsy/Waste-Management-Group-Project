/**
 * @jest-environment jsdom
 */

import "@jest/globals";

import BusinessSearch from "@/components/business/BusinessSearch";


const VueTestUtils = require('@vue/test-utils')


let loggedIn = true
// Will become the fake vue instance
let wrapper

let computed = {
    isLoggedIn() {
        return loggedIn
    },
}

describe('Jest tests for the BusinessSearch component', () => {
    // Setup before each test
    beforeEach(() => {
        wrapper = VueTestUtils.shallowMount(BusinessSearch, {
            stubs: ['router-link', 'router-view', "login-required", "admin-required"],
            computed,
            mocks: {
                $route: {query: {}}
            }
        })
    });

    // Test that not selecting a business type sets businessType to empty string
    test('Not selecting a business type sets businessType to empty string',async () => {
        const options = wrapper.find('select').findAll('option')
        options.at(0).setSelected()
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.businessType).toEqual("")
    })

    // Test that selecting the "Any type" option for business type sets businessType to "Any type"
    test('Selecting empty business type option sets businessType to "Any type"',async () => {
        const options = wrapper.find('select').findAll('option')
        options.at(1).setSelected()
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.businessType).toEqual("Any type")
    })

    // Test that selecting Accommodation and Food Services for business type sets businessType to Accommodation and Food Services
    test('Selecting Accommodation and Food Services for business type sets ' +
        'businessType to Accommodation and Food Services',async () => {
        const options = wrapper.find('select').findAll('option')
        // Accommodation and Food Services is at index 2 because
        // index 0 is "Filter by business type"
        // index 1 is ""
        options.at(2).setSelected()
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.businessType).toEqual("Accommodation and Food Services")
    })
})

