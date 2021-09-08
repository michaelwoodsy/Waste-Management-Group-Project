import '@jest/globals'
import SalesReport from "@/components/sales-report/SalesReport";
import {shallowMount} from "@vue/test-utils";

let wrapper

describe('Tests for the SalesReport component', () => {

    beforeEach(() => {
        wrapper =  shallowMount(SalesReport, {
            propsData: {
                data: [],
                currency: {
                    symbol: '$',
                    code: 'NZD'
                }
            }
        })
    })

    test('formattedDate method returns correct string', () => {
        const formattedDate = wrapper.vm.formattedDate('2021-09-07')
        expect(formattedDate).toStrictEqual('Tue Sep 07 2021')
    })

    test('formattedValue method returns correct string', () => {
        const formattedValue = wrapper.vm.formattedValue(50.00)
        expect(formattedValue).toStrictEqual('$50.00 NZD')
    })

})