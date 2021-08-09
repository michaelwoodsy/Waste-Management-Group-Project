import "@jest/globals";
import Notification from "@/components/Notification";
import {shallowMount} from "@vue/test-utils";
import {Keyword, User} from '@/Api'
import user from '@/store/modules/user'

jest.mock('@/Api')
jest.mock('@/store/modules/user')

User.deleteNotification.mockImplementation(jest.fn())
User.deleteAdminNotification.mockImplementation(jest.fn())
User.readNotification.mockImplementation(jest.fn())
User.readAdminNotification.mockImplementation(jest.fn())
Keyword.deleteKeyword.mockImplementation(jest.fn())
user.actingUserId.mockImplementation(jest.fn(() => {
    return 1
}))

let wrapper

describe('Jest tests for Notification component', () => {

    beforeEach(() => {
        wrapper = shallowMount(Notification, {
            propsData: {
                data: {
                    id: 1,
                    type: 'cardExpiry',
                    message: "This card has expired and has been deleted",
                    created: "2/07/2021 4:34pm",
                    card: "Looking for plums"
                }
            }
        })
    })

    afterEach(() => {
        jest.resetAllMocks()
        wrapper.destroy()
    })

    test('Clicking the close button emits the remove-notification event', async () => {
        const button = wrapper.find('#closeNotification')
        await button.trigger('click')
        expect(User.deleteNotification).toHaveBeenCalledTimes(1)
        expect(wrapper.emitted('remove-notification')).toBeTruthy()
    })

    test('Deleting a keyword calls the correct API functions and emits remove-notification event', async () => {
        wrapper = shallowMount(Notification, {
            propsData: {
                data: {
                    id: 1,
                    type: 'newKeyword',
                    message: "A new keyword has been created with name: Apple",
                    created: "2/07/2021 4:34pm",
                    keyword: {
                        id: 1,
                        name: 'Apple',
                        created: "2/07/2021 4:34pm"
                    }
                }
            }
        })

        await wrapper.vm.deleteKeyword()
        expect(User.deleteAdminNotification).toHaveBeenCalledTimes(1)
        expect(User.deleteNotification).toHaveBeenCalledTimes(0)
        expect(Keyword.deleteKeyword).toHaveBeenCalledTimes(1)
        expect(wrapper.emitted('remove-notification')).toBeTruthy()
    })

    test('readNotification method for regular notification calls correct api method and emits event', async () => {
        await wrapper.vm.readNotification()
        expect(User.readNotification).toHaveBeenCalledTimes(1)
        expect(User.readAdminNotification).toHaveBeenCalledTimes(0)
        expect(wrapper.emitted('read-notification')).toBeTruthy()
    })

    test('readNotification method for admin notification calls correct endpoint and emits event', async () => {
        wrapper = shallowMount(Notification, {
            propsData: {
                data: {
                    id: 1,
                    type: 'newKeyword',
                    message: "A new keyword has been created with name: Apple",
                    created: "2/07/2021 4:34pm",
                    keyword: {
                        id: 1,
                        name: 'Apple',
                        created: "2/07/2021 4:34pm"
                    }
                }
            }
        })

        await wrapper.vm.readNotification()
        expect(User.readNotification).toHaveBeenCalledTimes(0)
        expect(User.readAdminNotification).toHaveBeenCalledTimes(1)
        expect(wrapper.emitted('read-notification')).toBeTruthy()
    })

})