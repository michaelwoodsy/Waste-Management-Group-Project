/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import BusinessSearch from "@/components/business/BusinessSearch";
import {shallowMount} from "@vue/test-utils";

let wrapper;

beforeEach(() => {
    wrapper = shallowMount(BusinessSearch, {
        computed: {
            isLoggedIn() {
                return true;
            }
        }
    })
})

describe('Validate BusinessSearch method tests', () => {

    test("Test orderDirArrow down", () => {
        wrapper.vm.$data.orderDirection = true;
        expect(wrapper.vm.orderDirArrow).toStrictEqual('↓')
    })

    test("Test orderDirArrow up", () => {
        wrapper.vm.$data.orderDirection = false;
        expect(wrapper.vm.orderDirArrow).toStrictEqual('↑')
    })

})