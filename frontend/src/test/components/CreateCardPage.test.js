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

describe('validate Keywords method tests', () => {

    beforeEach(() => {
        wrapper.vm.$data.msg.keywordIds = null
        wrapper.vm.$data.keywordIds = [
            {name: 'food', id: 5},
            {name: 'vegetable', id: 6},
            {name: 'fruit', id: 7},
        ]
        wrapper.vm.$data.selectedKeywords = []
        wrapper.vm.$data.valid = true
    })

    test("Test no keywords", () => {
        wrapper.vm.validateKeywords()
        expect(wrapper.vm.$data.msg.keywordIds).toStrictEqual('Please select at least one keyword')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test invalid keywords", () => {
        wrapper.vm.$data.selectedKeywords.push(4)
        wrapper.vm.validateKeywords()
        expect(wrapper.vm.$data.msg.keywordIds).toStrictEqual('Please select a valid keyword')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test mixture of valid and invalid keywords", () => {
        wrapper.vm.$data.selectedKeywords.push(5)
        wrapper.vm.$data.selectedKeywords.push(4)
        wrapper.vm.validateKeywords()
        expect(wrapper.vm.$data.msg.keywordIds).toStrictEqual('Please select a valid keyword')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test valid keyword", () => {
        wrapper.vm.$data.selectedKeywords.push(5)
        wrapper.vm.validateKeywords()
        expect(wrapper.vm.$data.msg.keywordIds).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

    test("Test multiple valid keywords", () => {
        wrapper.vm.$data.selectedKeywords.push(5)
        wrapper.vm.$data.selectedKeywords.push(7)
        wrapper.vm.validateKeywords()
        expect(wrapper.vm.$data.msg.keywordIds).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })
})

describe('validate Cancel button', () => {

    beforeEach(() => {
        wrapper.vm.$data.cancel = false
    })

    test("Clicking the cancel button makes cancel to be true", async () => {
        await wrapper.find('#cancelButton').trigger('click')
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.cancel).toBeTruthy()
    })

    test("Not clicking the cancel button makes cancel to be false", async () => {
        expect(wrapper.vm.$data.cancel).toBeFalsy()
    })
})

describe('validate Create button', () => {

    beforeEach(() => {
        wrapper.vm.$data.submit = false
    })

    test("Clicking the create button makes submit to be true", async () => {
        await wrapper.find('#createButton').trigger('click')
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.submit).toBeTruthy()
    })

    test("Not clicking the create button makes create to be false", async () => {
        expect(wrapper.vm.$data.submit).toBeFalsy()
    })
})

