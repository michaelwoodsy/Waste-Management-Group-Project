/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import {shallowMount} from "@vue/test-utils";
import EditBusiness from "@/components/business/EditBusiness";
import {Business} from "@/Api";

let wrapper;

jest.mock('@/Api')

Business.getBusinessData.mockImplementation(jest.fn(() => {
    return {
        data: {
            name: 'Business Name',
            description: 'Some Business Description',
            address: {},
            businessType: 'Retail Trade',
            administrators: [],
            primaryAdministratorId: 1
        }
    }
}))

describe('EditBusiness component test', () => {

    beforeEach(() => {

        wrapper = shallowMount(EditBusiness, {
            data() {
                return {
                    user: {
                        isActingAsBusiness() {
                            return true
                        },
                        state: {actingAs: {name: 'Business Name'}}
                    },
                    businessName: 'Business Name',
                    businessType: 'Retail Trade',
                    primaryAdmin: {id: 1}
                }
            },
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
            }
        })
    })

    test("Test a business can't be given an empty name", async () => {
        wrapper.vm.$data.businessName = ''
        await wrapper.vm.validateBusinessName()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.businessName).toStrictEqual('Please enter a Business Name')
    })

    test("Test a business can't be given an empty type", async () => {
        wrapper.vm.$data.businessType = ''
        await wrapper.vm.validateBusinessType()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.businessType).toStrictEqual('Please select a Business Type')
    })

    test("Test a business can't be given no primary admin", async () => {
        wrapper.vm.$data.primaryAdmin = null
        await wrapper.vm.validatePrimaryAdmin()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.primaryAdmin).toStrictEqual('Please select a Primary Administrator')
    })

    test("Edit Business function updates acting as name and sets success flag", async () => {
        Business.editBusiness.mockImplementation(jest.fn())

        const newName = 'New Business Name'
        wrapper.vm.$data.businessName = newName
        await wrapper.vm.editBusiness()
        expect(wrapper.vm.$data.successfulEdit).toBeTruthy()
        expect(wrapper.vm.$data.user.state.actingAs.name).toStrictEqual(newName)
    })

    test("Reset page clears correct flags", async () => {
        wrapper.vm.$data.valid = false
        wrapper.vm.$data.addressIsValid = true
        wrapper.vm.$data.successfulEdit = true
        await wrapper.vm.resetPage()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.addressIsValid).toBeFalsy()
        expect(wrapper.vm.$data.successfulEdit).toBeFalsy()
    })


})