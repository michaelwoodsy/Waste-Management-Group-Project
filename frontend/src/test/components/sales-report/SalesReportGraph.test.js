import '@jest/globals'
import {shallowMount} from "@vue/test-utils";
import SalesReportGraph from "@/components/sales-report/SalesReportGraph";


let wrapper

const testReport = [
    {
        periodStart: '2021-06-01',
        periodEnd: '2021-06-30',
        totalPurchaseValue: 50.00,
        purchaseCount: 4,
        sales: []
    },
    {
        periodStart: '2021-07-01',
        periodEnd: '2021-07-01',
        totalPurchaseValue: 40.00,
        purchaseCount: 5,
        sales: []
    }
]

describe('Tests for the SalesReportGraph component', () => {

    beforeEach(() => {
        wrapper = shallowMount(SalesReportGraph, {
            propsData: {
                data: testReport
            },
            methods: {
                drawChart() {
                    return null
                }
            }
        })
    })

    test('formattedDate method returns correct string', () => {
        const formattedDate = wrapper.vm.formattedDate('2021-09-07')
        expect(formattedDate).toStrictEqual('Tue Sep 07 2021')
    })

    test('setGraphInfo method changes list of dates as expected', () => {
        wrapper.vm.setGraphInfo()
        expect(wrapper.vm.$data.dates[0]).toStrictEqual('Tue Jun 01 2021 - Wed Jun 30 2021')
        expect(wrapper.vm.$data.dates[1]).toStrictEqual('Thu Jul 01 2021')
    })

    test('setGraphInfo method changes list of values as expected', () => {
        wrapper.vm.setGraphInfo()
        expect(wrapper.vm.$data.totalValues).toStrictEqual([50.00, 40.00])

    })

    test('setGraphInfo method changes list of sale numbers as expected', () => {
        wrapper.vm.setGraphInfo()
        expect(wrapper.vm.$data.totalSales).toStrictEqual([4, 5])
    })

})