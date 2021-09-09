import '@jest/globals'
import {shallowMount} from "@vue/test-utils";
import SalesReportPage from "@/components/sales-report/SalesReportPage";
import {Business} from "@/Api";
import product from "@/store/modules/product";

jest.mock('@/Api')
jest.mock('@/store/modules/product')

let wrapper

describe('Tests for the SalesReportPage component', () => {

    beforeEach(() => {
        wrapper = shallowMount(SalesReportPage, {
            propsData: {
                businessId: 1
            }
        })
    })

    test('Test that getCurrency method sets the currency of the component', async () => {
        Business.getBusinessData.mockResolvedValue({
            data: {
                address: {
                    country: 'New Zealand'
                }
            }
        })
        product.getCurrency.mockResolvedValue({
            symbol: '$',
            code: 'NZD'
        })
        wrapper.vm.$data.currency = null;
        await wrapper.vm.getCurrency()
        const currency = wrapper.vm.$data.currency
        expect(currency.symbol).toStrictEqual('$')
        expect(currency.code).toStrictEqual('NZD')
    })

})