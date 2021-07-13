import "@jest/globals";
import Notification from "@/components/Notification";
import {shallowMount} from "@vue/test-utils";
import {User} from '@/Api'

jest.mock('@/Api')

let wrapper

describe('Jest tests for Notification component', () => {

    beforeEach(() => {
        wrapper = shallowMount(Notification, {
            propsData: {
                data: {
                    id: 0,
                    type: 'cardExpiry',
                    message: "This card has expired and has been deleted",
                    created: "2/07/2021 4:34pm",
                    card: "Looking for plums"
                }
            },
            computed: {
                userId() {
                    return 1
                }
            }
        })
    })

    test('Clicking the close button emits the remove-notification event', async () => {
        User.deleteNotification.mockImplementation(() => {})

        const button = wrapper.find('#closeNotification')
        await button.trigger('click')
        expect(wrapper.emitted('remove-notification')).toBeTruthy()
    })

})