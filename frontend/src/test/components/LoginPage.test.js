/**
 * @jest-environment jsdom
 */

import LoginPage from "@/components/LoginPage";

const VueTestUtils = require('@vue/test-utils')

let wrapper;

beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(LoginPage, {
        stubs: ['page-wrapper', 'alert', 'logout-required', 'router-link']
    })
})

describe("Tests for the login page", () => {

    test("Test an empty email address gives error message", () => {
        wrapper.vm.$data.username = ""
        wrapper.vm.checkUsername()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.username).toStrictEqual("Please enter an email address")

    })

    test("Test an empty password gives error message", () => {
        wrapper.vm.$data.password = ""
        wrapper.vm.checkPassword()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.password).toStrictEqual("Please enter a password")
    })

    test("Test a valid email", () => {
        wrapper.vm.$data.username = "john.smith@gmail.com"
        wrapper.vm.checkUsername()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.username).toBeNull()
    })

    test("Test a valid password", () => {
        wrapper.vm.$data.password = "Password123"
        wrapper.vm.checkPassword()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.password).toBeNull()
    })

    test("Test that the 'no more attempts' message is originally hidden", () => {
        expect(wrapper.find('#noMoreAttempts').exists()).toBeFalsy();
    })


    test("Test that the 'no more attempts' message is shown after 3 incorrect attempts", async() => {
        wrapper.vm.$data.loginCount = 3
        await wrapper.vm.$nextTick()
        expect(wrapper.find('#noMoreAttempts').exists()).toBeTruthy();
    })

})