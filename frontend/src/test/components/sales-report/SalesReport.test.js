import '@jest/globals'
import SalesReport from "@/components/sales-report/SalesReport";
import {shallowMount} from "@vue/test-utils";

let wrapper

const testReport = [
    {
        periodStart: '2021-06-01',
        periodEnd: '2021-06-30',
        totalPurchaseValue: 50.00,
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
                dateSold: "2021-09-04",
                productId: "WATT-BEANS",
                productName: "Watties Baked Beans",
                quantity: 6,
                price: 5.50,
                currencyCountry: "New Zealand",
                currency: {}
            }
        ]
    },
    {
        periodStart: '2021-07-01',
        periodEnd: '2021-07-31',
        totalPurchaseValue: 40.00,
        purchaseCount: 5,
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
        ]
    }
]

describe('Tests for the SalesReport component', () => {

    beforeEach(() => {
        wrapper =  shallowMount(SalesReport, {
            propsData: {
                data: testReport,
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

    test('totalSales computed returns correct value', () => {
        let expectedVal = 0
        for (const section of testReport) {
            expectedVal += section.purchaseCount
        }
        expect(wrapper.vm.totalSales).toStrictEqual(expectedVal)
    })

    test('totalValue computed returns correct value', () => {
        let expectedVal = 0
        for (const section of testReport) {
            expectedVal += section.totalPurchaseValue
        }
        expect(wrapper.vm.totalValue).toStrictEqual(expectedVal)
    })

    test('Tests the currencyWarning method produces expected currency warning text', () => {
        wrapper.vm.currencyWarning(["NZD", "AUD"], ["AUD", "GBP"],
            ["10/04/2021", "14/09/2021"])
        expect(wrapper.vm.$data.currencyWarningText).toStrictEqual(
            "Data up to 10/04/2021 is in NZD and data after 10/04/2021 is in AUD."
        + " Please convert manually.\n"
        + "Data up to 14/09/2021 is in AUD and data after 14/09/2021 is in GBP."
            + " Please convert manually.\n")
        expect(wrapper.vm.$data.showCurrencyWarning).toBeTruthy()
    })

})