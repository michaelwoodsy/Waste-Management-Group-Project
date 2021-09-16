import "@jest/globals";
import {Statistics} from "@/Api";
import StatisticsPage from '@/components/landing-page/Statistics';
import {shallowMount} from "@vue/test-utils";

// Will become the fake vue instance
let wrapper;

jest.mock('@/Api')

const stats = {
    numAvailableListings: 1,
    totalNumSales: 2,
    totalUserCount: 3
}

describe('AddressInputFields Component Tests', () => {

    beforeEach(async () => {
        const response = {
            data: stats
        }
        Statistics.getStatistics.mockImplementation(() => Promise.resolve(response))

        wrapper = shallowMount(StatisticsPage, {})
        await wrapper.vm.$nextTick()
    })

    afterEach(() => {
        jest.clearAllMocks()
    })

    test("Test the correct fields are set with the correct stats", async () => {
        expect(wrapper.find('#userCountText').text()).toBe(stats.totalUserCount.toString())
        expect(wrapper.find('#availableListingsText').text()).toBe(stats.numAvailableListings.toString())
        expect(wrapper.find('#totalSalesText').text()).toBe(stats.totalNumSales.toString())
    })
})