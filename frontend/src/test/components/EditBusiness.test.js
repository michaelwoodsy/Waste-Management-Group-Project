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
            businessId() {
                return 1
            },
            isLoggedIn() {
                return true
            },
            isAdminOf() {
                return true
            },
            isPrimaryAdmin() {
                return true
            }
        },
        methods: {
            async prefillFields() {

            }
        }
    })
})


describe('EditBusiness component test', () => {

    test("Test a business can't be given an empty name", async() => {
        await wrapper.vm.validateBusinessName()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.businessName).toStrictEqual('Please enter a Business Name')
    })

    test("Test a business can't be given an empty type", async() => {
        await wrapper.vm.validateBusinessType()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.businessType).toStrictEqual('Please select a Business Type')
    })

    test("Test a business can't be given no primary admin", async() => {
        await wrapper.vm.validatePrimaryAdmin()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.primaryAdmin).toStrictEqual('Please select a Primary Administrator')
    })


})