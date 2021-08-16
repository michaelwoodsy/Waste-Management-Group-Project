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

    test("Test that the password reset send email modal appears", async ()=> {
        wrapper.vm.$data.viewPasswordReset = true
        await wrapper.vm.$nextTick()
        expect(wrapper.find('#viewPasswordResetModal').exists()).toBeTruthy();

    })

    test("Test that the sent email modal appears", async ()=> {
        wrapper.vm.$data.emailSent = true
        await wrapper.vm.$nextTick()
        expect(wrapper.find('#viewEmailSentModal').exists()).toBeTruthy();

    })

    test("Test a valid email for password reset", ()=>{
        wrapper.vm.$data.email = "john.smith@gmail.com"
        wrapper.vm.checkEmail()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.email).toBeNull()
    })

    test("Test an invalid email for password reset", ()=>{
        wrapper.vm.$data.email = "blah"
        wrapper.vm.checkEmail()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.email).toEqual("Invalid email address")
    })

    test("Test an empty email for password reset", ()=>{
        wrapper.vm.$data.email = ""
        wrapper.vm.checkEmail()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.email).toEqual("Please enter an email address")
    })

    test("Test that the reset function works", ()=>{
        wrapper.vm.$data.email = "john.smith@gmail.com"
        wrapper.vm.$data.submitting = true
        wrapper.vm.$data.viewPasswordReset = true
        wrapper.vm.$data.msg.email = "Invalid email address"
        wrapper.vm.$data.error = "There was an error"
        wrapper.vm.resetPasswordResetModal()
        expect(wrapper.vm.$data.email).toBeNull()
        expect(wrapper.vm.$data.submitting).toBeFalsy()
        expect(wrapper.vm.$data.viewPasswordReset).toBeFalsy()
        expect(wrapper.vm.$data.msg.email).toBeNull()
        expect(wrapper.vm.$data.error).toBeNull()

    })

})