import '@jest/globals'
import {shallowMount} from "@vue/test-utils";
import SalesReportPage from "@/components/sales-report/SalesReportPage";
import {Business} from "@/Api";
import product from "@/store/modules/product";

jest.mock('@/Api')
jest.mock('@/store/modules/product')

let wrapper

const testReport = [
    {
        periodStart: '2021-05-01',
        periodEnd: '2021-05-30',
        totalPurchaseValue: 60.00,
        purchaseCount: 4,
        sales: [
            {
                dateSold: "2021-09-04",
                productId: "WATT-BEANS",
                productName: "Watties Baked Beans",
                quantity: 6,
                price: 5.50,
                currencyCountry: "New Zealand",
                currency: {}
            },
            {
                dateSold: "2021-09-05",
                productId: "KID-BEANS",
                productName: "Value Kidney Beans",
                quantity: 4,
                price: 3.50,
                currencyCountry: "Australia",
                currency: {}
            },
            {
                dateSold: "2021-09-05",
                productId: "KID-BEANS",
                productName: "Value Kidney Beans",
                quantity: 4,
                price: 3.50,
                currencyCountry: "Australia",
                currency: {}
            }
        ]
    }
]

describe('Tests for the SalesReportPage component', () => {

    beforeEach(() => {
        wrapper = shallowMount(SalesReportPage, {
            propsData: {
                businessId: 1
            }
        })
        Business.getSalesReport.mockResolvedValue({
            data: testReport
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

    test('Test that getSalesReport correctly sets report property', async () => {
        await wrapper.vm.getReport(1, {})
        expect(wrapper.vm.$data.report.length).toStrictEqual(testReport.length)
    })

})