import '@jest/globals'
import {shallowMount} from "@vue/test-utils"
import CreateCardPage from "@/components/marketplace/CreateCardPage"

let wrapper

describe('validate Section method tests', () => {

    beforeEach(async () => {
        wrapper = await shallowMount(CreateCardPage, {
            methods: {
                addCard() {
                    return null
                }
            }
        })

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
        wrapper.vm.$data.section = 'ForSale'
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
        wrapper.vm.$data.msg.keywords = null
        wrapper.vm.$data.valid = true
        wrapper.vm.$data.keywordValue = '';
        wrapper.vm.$data.keywords = [];
    })

    test("Test empty keyword list", () => {
        wrapper.vm.$data.keywords = []
        wrapper.vm.validateKeywords()
        expect(wrapper.vm.$data.msg.keywords).toStrictEqual('Please enter one or more keywords')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test keyword list with 1 item", () => {
        wrapper.vm.$data.keywords = ["test"]
        wrapper.vm.validateKeywords()
        expect(wrapper.vm.$data.keywords).toStrictEqual(["test"])
        expect(wrapper.vm.$data.msg.keywords).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

    test("Test adding a valid keyword", () => {
        wrapper.vm.$data.keywordValue = "test"
        wrapper.vm.addKeyword()
        expect(wrapper.vm.$data.keywords).toStrictEqual(["test"])
    })

    test("Test adding an invalid keyword", () => {
        wrapper.vm.$data.keywordValue = " "
        wrapper.vm.addKeyword()
        expect(wrapper.vm.$data.keywords).toStrictEqual([])
    })

    test("Test adding an keyword which is already in the list", () => {
        wrapper.vm.$data.keywordValue = "test"
        wrapper.vm.addKeyword()
        wrapper.vm.addKeyword()
        expect(wrapper.vm.$data.keywords).toStrictEqual(["test"])
    })

    test("Removing a keyword", () => {
        wrapper.vm.$data.keywordValue = "test"
        wrapper.vm.addKeyword()
        wrapper.vm.removeKeyword(0)
        expect(wrapper.vm.$data.keywords).toStrictEqual([])
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

