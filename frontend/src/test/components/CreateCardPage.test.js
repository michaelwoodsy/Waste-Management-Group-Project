import '@jest/globals'
import {shallowMount} from "@vue/test-utils"
import CreateCardPage from "@/components/CreateCardPage"

let wrapper = shallowMount(CreateCardPage)

describe('validate Section method tests', () => {

    beforeEach(() => {
        wrapper.vm.$data.msg.section = null
        wrapper.vm.$data.valid = true
    })

    test("Test no section", () => {
        wrapper.vm.$data.section = ''
        wrapper.vm.validateSection()
        expect(wrapper.vm.$data.msg.section).toStrictEqual('Please select a section')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test no section", () => {
        wrapper.vm.$data.section = null
        wrapper.vm.validateSection()
        expect(wrapper.vm.$data.msg.section).toStrictEqual('Please select a section')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test invalid section", () => {
        wrapper.vm.$data.section = 'blah'
        wrapper.vm.validateSection()
        expect(wrapper.vm.$data.msg.section).toStrictEqual('Please select an appropriate section')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test valid section", () => {
        wrapper.vm.$data.section = 'For Sale'
        wrapper.vm.validateSection()
        expect(wrapper.vm.$data.msg.section).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

    test("Test valid section", () => {
        wrapper.vm.$data.section = 'Wanted'
        wrapper.vm.validateSection()
        expect(wrapper.vm.$data.msg.section).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

    test("Test valid section", () => {
        wrapper.vm.$data.section = 'Exchange'
        wrapper.vm.validateSection()
        expect(wrapper.vm.$data.msg.section).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })
})

describe('validate Title method tests', () => {

    beforeEach(() => {
        wrapper.vm.$data.msg.title = null
        wrapper.vm.$data.valid = true
    })

    test("Test no title", () => {
        wrapper.vm.$data.title = ''
        wrapper.vm.validateTitle()
        expect(wrapper.vm.$data.msg.title).toStrictEqual('Please enter a title')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test no title", () => {
        wrapper.vm.$data.title = null
        wrapper.vm.validateTitle()
        expect(wrapper.vm.$data.msg.title).toStrictEqual('Please enter a title')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test valid title", () => {
        wrapper.vm.$data.title = '1982 Lada Samara'
        wrapper.vm.validateTitle()
        expect(wrapper.vm.$data.msg.title).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })
})

