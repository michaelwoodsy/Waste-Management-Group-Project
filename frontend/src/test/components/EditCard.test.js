import '@jest/globals'
import {shallowMount} from "@vue/test-utils"
import {Keyword} from "@/Api"
import EditCard from "@/components/marketplace/EditCard";

let wrapper

const keywords = [
    {
        'id': 1,
        'name': 'Apple'
    },
    {
        'id': 2,
        'name': 'Banana'
    },
    {
        'id': 3,
        'name': 'Chocolate'
    }
]

let currentKeywords = []

jest.mock('@/Api')

describe('validate Section method tests', () => {

    beforeEach(async () => {
        wrapper = await shallowMount(EditCard, {
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

    beforeEach(async() => {
        wrapper = await shallowMount(EditCard, {
            methods: {
                addCard() {
                    return null
                }
            }
        })

        wrapper.vm.$data.msg.title = null
        wrapper.vm.$data.valid = true
    })

    test("Test empty title", () => {
        wrapper.vm.$data.title = ''
        wrapper.vm.validateTitle()
        expect(wrapper.vm.$data.msg.title).toStrictEqual('Please enter a title')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test space title", () => {
        wrapper.vm.$data.title = ' '
        wrapper.vm.validateTitle()
        expect(wrapper.vm.$data.msg.title).toStrictEqual('Please enter a title')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test null title", () => {
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

    //We now expect an empty keyword list to be valid
    test("Test empty keyword list", () => {
        wrapper.vm.$data.keywords = []
        expect(wrapper.vm.$data.keywords).toStrictEqual([])
        expect(wrapper.vm.$data.msg.keywords).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

    test("Test keyword list with 1 item", () => {
        wrapper.vm.$data.keywords = ["test"]
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

describe('validate Save Changes button', () => {

    beforeEach(() => {
        wrapper.vm.$data.submit = false
    })

    test("Clicking the Save Changes button makes submit to be true", async () => {
        await wrapper.find('#saveButton').trigger('click')
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.submit).toBeTruthy()
    })

    test("Not clicking the Save Changes button makes create to be false", async () => {
        expect(wrapper.vm.$data.submit).toBeFalsy()
    })
})

describe('Tests for getKeywordIds method', () => {

    beforeEach(() => {
        currentKeywords = [...keywords]
        Keyword.createKeyword.mockImplementation(jest.fn((name) => {
            const id = currentKeywords.length + 1
            currentKeywords.push({
                id: id,
                name: name
            })
            return {
                data: {
                    keywordId: id
                }
            }
        }))
        Keyword.searchKeywords.mockImplementation(jest.fn((name) => {
            const result = []
            for (const keyword of currentKeywords) {
                if (keyword['name'].includes(name)) {
                    result.push(keyword)
                }
            }
            return {
                data: result
            }
        }))
    })

    test('Creating a card with current keywords results in their IDs being added', async () => {
        wrapper.vm.$data.keywords = ['Apple', 'Chocolate']
        const result = await wrapper.vm.getKeywordIds()
        expect(result.length).toStrictEqual(2)
        expect(result).toContain(1)
        expect(result).toContain(3)
    })

    test('Creating a card with a new keyword adds the new keyword to the keyword list', async () => {
        wrapper.vm.$data.keywords = ['Apple', 'Cherry']
        const result = await wrapper.vm.getKeywordIds()
        expect(currentKeywords.length).toStrictEqual(4)
        expect(result.length).toStrictEqual(2)
        expect(result).toContain(4)
    })

})
