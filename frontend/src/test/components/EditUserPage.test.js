/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import {shallowMount} from "@vue/test-utils";
import EditUserProfile from "@/components/user/EditUserProfile";

let wrapper;
let computed = {
    userId() {
        return 1
    },
    isLoggedIn() {
        return true
    },
    actingAsUser() {
      return true
    },
    canDoAdminAction () {
        return false
    },
    isEditingSelf() {
        return true
    }
}
let methods = {
    editUser() {
        //Do nothing
    },
    async validateAddress() {
        return this.homeAddress.country !== null && this.homeAddress.country !== ""
    }
}


// Setup before each test
beforeEach(() => {
    wrapper = shallowMount(EditUserProfile, {
        stubs: ['router-link', 'router-view', "login-required", "admin-required"],
        computed,
        methods,
        data() {
            return {
                firstName: "Rutger",
                lastName: "van Kruiningen",
                oldEmail: "test.email@gmail.com",
                email: "test.email@gmail.com",
                dateOfBirth: "2000-09-08",
                phoneNumber: "022 123 4567",
                newPassword: "NewSecurePassword123",
                currentPassword: "Password123",
                homeAddress: {
                    streetNumber: '',
                    streetName: '',
                    city: '',
                    region: '',
                    country: 'New Zealand',
                    postcode: '',
                },
                valid: true
            }
        }
    })
});

describe('EditUserProfile Validity Tests', () => {
    test("Tests that valid is true when valid data is supplied", async() => {

        await wrapper.vm.checkInputs()
        expect(wrapper.vm.$data.valid).toBeTruthy()

        expect(wrapper.find('span.error-msg').exists()).toBeFalsy()
    })

    test("Tests that valid is false when the first name is empty", async() => {
        wrapper.setData({
            firstName: '',
        })

        await wrapper.vm.validateFirstName()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.firstName).toStrictEqual('Please enter a first name')
    })

    test("Tests that valid is false when the last name is empty", async() => {
        wrapper.setData({
            lastName: '',
        })

        await wrapper.vm.validateLastName()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.lastName).toStrictEqual('Please enter a last name')
    })

    test("Tests that valid is false when the email is empty and invalid", async() => {
        wrapper.setData({
            email: '',
        })

        await wrapper.vm.validateEmail()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.email).toStrictEqual('Please enter an email address')

        wrapper.setData({
            email: 'test@gmail',
        })

        await wrapper.vm.validateEmail()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.email).toStrictEqual('Invalid email address')
    })

    test("Tests that valid is false when the date of birth is empty and invalid", async() => {
        wrapper.setData({
            dateOfBirth: '',
        })

        await wrapper.vm.validateDateOfBirth()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.dateOfBirth).toStrictEqual('Please enter a date of birth')

        wrapper.setData({
            dateOfBirth: '1600-01-01',
        })

        await wrapper.vm.validateDateOfBirth()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.dateOfBirth).toStrictEqual('Date of birth is unrealistic')

        wrapper.setData({
            dateOfBirth: '2050-01-01',
        })

        await wrapper.vm.validateDateOfBirth()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.dateOfBirth).toStrictEqual('Date of birth can not be in the future')
    })

    test("Tests that valid is true when the phone number is empty and false when invalid", async() => {
        wrapper.setData({
            phoneNumber: '',
        })

        await wrapper.vm.validatePhoneNumber()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.phoneNumber).toBeNull()

        wrapper.setData({
            phoneNumber: '022 1',
        })

        await wrapper.vm.validatePhoneNumber()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.phoneNumber).toStrictEqual('Invalid phone number')
    })

    test("Tests that when no new password is provided, valid is true", async() => {
        wrapper.setData({
            newPassword: null,
        })

        await wrapper.vm.validatePassword()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.newPassword).toBeNull()
        expect(wrapper.vm.$data.msg.currentPassword).toBeNull()
    })

    test("Tests that when a new password is provided, and is not valid, valid is set to false", async() => {
        wrapper.setData({
            newPassword: "password",
            currentPassword: "",
        })

        await wrapper.vm.validatePassword()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.newPassword).toStrictEqual('Password does not meet the requirements')
        expect(wrapper.vm.$data.msg.currentPassword).toStrictEqual('Your current password is required')
    })

})