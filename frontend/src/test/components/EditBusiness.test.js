/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import {shallowMount} from "@vue/test-utils";
import EditBusiness from "@/components/business/EditBusiness";

let wrapper;

jest.mock('@/Api')

beforeEach(() => {

    wrapper = shallowMount(EditBusiness, {
        computed: {
            isLoggedIn() {
                return true
            },
            isAdminOf() {
                return true
            },
            isPrimaryAdmin() {
                return false
            }
        }
    })
})


describe('EditBusiness component test', () => {

    test("Test a business can't be given an empty name", async() => {
        await wrapper.vm.validateBusinessName()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.businessName).toStrictEqual('Please enter a Business Name')
        //Error message is shown
        expect(wrapper.find('span.error-msg').exists()).toBeTruthy()
    })

    test("Test a business can't be given an empty type", async() => {
        await wrapper.vm.validateBusinessType()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.businessType).toStrictEqual('Please select a Business Type')
        //Error message is shown
        expect(wrapper.find('span.error-msg').exists()).toBeTruthy()
    })


})