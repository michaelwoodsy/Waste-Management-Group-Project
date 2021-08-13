/**
 * @jest-environment jsdom
 */

import PasswordReset from "@/components/user/PasswordReset";

const VueTestUtils = require('@vue/test-utils')

let wrapper;


// Setup before each test
beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(PasswordReset, {
        stubs: ['page-wrapper', 'alert'],
        computed: {
            linkExpired() {
                return false
            },
            userEmail() {
                return "myrtle.t@gmail.com"
            },
            isLoggedIn() {
                return false
            }
        },
        methods: {
            changePassword() {

            }
        }
    })
});


describe('validatePassword method tests', () => {

    beforeEach(() => {
        wrapper.vm.$data.msg.password = null
        wrapper.vm.$data.valid = true
    })

    test('Test no password is invalid', () => {
        wrapper.vm.$data.password = ''
        wrapper.vm.validatePassword()
        expect(wrapper.vm.$data.msg.password).toStrictEqual('Please enter a password')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test password too short is invalid', () => {
        wrapper.vm.$data.password = 'hEll0'
        wrapper.vm.validatePassword()
        expect(wrapper.vm.$data.msg.password).toStrictEqual('New password does not meet the requirements')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test password without uppercase letters is invalid', () => {
        wrapper.vm.$data.password = 'password123'
        wrapper.vm.validatePassword()
        expect(wrapper.vm.$data.msg.password).toStrictEqual('New password does not meet the requirements')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test password without lowercase letters is invalid', () => {
        wrapper.vm.$data.password = 'PASSWORD123'
        wrapper.vm.validatePassword()
        expect(wrapper.vm.$data.msg.password).toStrictEqual('New password does not meet the requirements')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test password without numbers is invalid', () => {
        wrapper.vm.$data.password = 'PASSWORDpassword'
        wrapper.vm.validatePassword()
        expect(wrapper.vm.$data.msg.password).toStrictEqual('New password does not meet the requirements')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test valid password', () => {
        wrapper.vm.$data.password = 'Password123'
        wrapper.vm.validatePassword()
        expect(wrapper.vm.$data.msg.password).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })
})