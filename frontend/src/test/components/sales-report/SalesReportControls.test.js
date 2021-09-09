import '@jest/globals'
import SalesReportControls from "@/components/sales-report/SalesReportControls";
import {shallowMount} from "@vue/test-utils";

let wrapper

describe('Tests for the SalesReportControls component', () => {

    beforeEach(() => {
        wrapper = shallowMount(SalesReportControls)
    })

    test('Test validateDateRange, both dates set to null results in correct error', () => {
        wrapper.vm.$data.options.startDate = null
        wrapper.vm.$data.options.endDate = null
        wrapper.vm.validateDateRange()
        expect(wrapper.vm.$data.msg.dateRange).toStrictEqual('Please enter a date range')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test validateDateRange, one date set to null results in correct error', () => {
        const endDate = new Date()
        endDate.setDate(endDate.getDate() - 7)
        wrapper.vm.$data.options.startDate = null
        wrapper.vm.$data.options.endDate = endDate.toISOString().substring(0, 10)
        wrapper.vm.validateDateRange()
        expect(wrapper.vm.$data.msg.dateRange).toStrictEqual('Please enter a date range')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test validateDateRange, one date in future results in correct error', () => {
        const startDate = new Date()
        startDate.setDate(startDate.getDate() - 14)
        const endDate = new Date()
        endDate.setDate(endDate.getDate() + 7)
        wrapper.vm.$data.options.startDate = startDate.toISOString().substring(0, 10)
        wrapper.vm.$data.options.endDate = endDate.toISOString().substring(0, 10)
        wrapper.vm.validateDateRange()
        expect(wrapper.vm.$data.msg.dateRange).toStrictEqual('Date range must be in the past')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test validateDateRange, valid range results in success', () => {
        const startDate = new Date()
        startDate.setDate(startDate.getDate() - 14)
        const endDate = new Date()
        endDate.setDate(endDate.getDate() - 7)
        wrapper.vm.$data.options.startDate = startDate.toISOString().substring(0, 10)
        wrapper.vm.$data.options.endDate = endDate.toISOString().substring(0, 10)
        wrapper.vm.validateDateRange()
        expect(wrapper.vm.$data.msg.dateRange).toBeNull()
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

    test('Test validateInputs, valid range emits event', () => {
        const startDate = new Date()
        startDate.setDate(startDate.getDate() - 20)
        const endDate = new Date()
        endDate.setDate(endDate.getDate() - 10)
        wrapper.vm.$data.options.startDate = startDate.toISOString().substring(0, 10)
        wrapper.vm.$data.options.endDate = endDate.toISOString().substring(0, 10)
        wrapper.vm.validateInputs()
        expect(wrapper.emitted('generate-report')).toBeTruthy()
    })

    test('Test validateInputs, invalid range does not emit event', () => {
        const startDate = new Date()
        startDate.setDate(startDate.getDate() + 2)
        const endDate = new Date()
        endDate.setDate(endDate.getDate() + 5)
        wrapper.vm.$data.options.startDate = startDate.toISOString().substring(0, 10)
        wrapper.vm.$data.options.endDate = endDate.toISOString().substring(0, 10)
        wrapper.vm.validateInputs()
        expect(wrapper.emitted('generate-report')).toBeFalsy()
    })

})