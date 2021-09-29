/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import RegisterBusinessPage from "@/components/business/RegisterBusinessPage";
import {shallowMount} from "@vue/test-utils";
import {Business} from "@/Api";

let wrapper;

jest.mock('@/Api')
Business.createNew.mockResolvedValue(() => {})

beforeEach(() => {
    wrapper = shallowMount(RegisterBusinessPage, {
        computed: {
            isLoggedIn() {
                return true;
            }
        }
    })
})

describe('RegisterBusinessPage component test', () => {


    test("Test a business can't be created with empty name", async() => {
        await wrapper.vm.validateBusinessName()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.businessName).toStrictEqual('Please enter a Business Name')
        //Error message is shown
        expect(wrapper.find('span.error-msg').exists()).toBeTruthy()
    })

    test("Test a business can't be created with empty type", async() => {
        await wrapper.vm.validateBusinessType()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.businessType).toStrictEqual('Please select a Business Type')
        //Error message is shown
        expect(wrapper.find('span.error-msg').exists()).toBeTruthy()
    })

})