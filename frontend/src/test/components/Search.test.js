/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import Search from "@/components/Search";

import UserSearch from "@/components/user/UserSearch";
import BusinessSearch from "@/components/business/BusinessSearch";

const VueTestUtils = require('@vue/test-utils')


let loggedIn = true
// Will become the fake vue instance
let wrapper;
let computed = {
    isLoggedIn() {
        return loggedIn
    },
}

// Setup before each test
beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(Search, {
        stubs: ['router-link', 'router-view', "login-required", "admin-required"],
        computed,
        mocks: {
            $route: {query: {}}
        }
    })
});

describe('Jest tests for the Search page', () => {

    // Test that selecting the 'Users' tab shows the UserSearch component
    test('Selecting Users tab shows UserSearch component', async () => {
        wrapper.vm.$data.tabSelected = "Users"
        await wrapper.vm.$nextTick()
        const userSearchComponent = wrapper.findComponent(UserSearch)
        expect(userSearchComponent.exists()).toBe(true)
    })

    // Test that selecting the 'Businesses' tab shows the BusinessSearch component
    test('Selecting Businesses tab shows BusinessSearch component', async () => {
        wrapper.vm.$data.tabSelected = "Businesses"
        await wrapper.vm.$nextTick();
        const businessSearchComponent = wrapper.findComponent(BusinessSearch)
        expect(businessSearchComponent.exists()).toBe(true)
    })


    // Test that selecting the 'Users' tab hides the BusinessSearch component
    test('Selecting Users tab hides BusinessSearch component', async () => {
        wrapper.vm.$data.tabSelected = "Users"
        await wrapper.vm.$nextTick()
        const businessSearchComponent = wrapper.findComponent(BusinessSearch)
        expect(businessSearchComponent.exists()).toBe(false)
    })

    // Test that selecting the 'Businesses' tab hides the UserSearch component
    test('Selecting Businesses tab hides UserSearch component', async () => {
        wrapper.vm.$data.tabSelected = "Businesses"
        await wrapper.vm.$nextTick();
        const userSearchComponent = wrapper.findComponent(UserSearch)
        expect(userSearchComponent.exists()).toBe(false)
    })
})