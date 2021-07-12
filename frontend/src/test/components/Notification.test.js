import "@jest/globals";
import Notification from "@/components/Notification";
import {shallowMount} from "@vue/test-utils";

let wrapper

describe('Jest tests for Notification component', () => {

    beforeEach(() => {
        wrapper = shallowMount(Notification, {
            propsData: {
                data: {
                    id: 0,
                    title: "Card Expiry",
                    message: "This card is about to expire",
                    created: "2/07/2021 4:34pm",
                    card: "Looking for plums"
                }
            }
        })
    })

    test('calling formatDateTime retrieves correct dateTime format', () => {
        const date = '2021-07-10T08:28:21.200Z'
        const formattedDate = wrapper.vm.formatDateTime(date)
        expect(formattedDate).toEqual('10/07/2021 20:28')
    })

    test('Clicking the close button emits the remove-notification event', async () => {
        let button = wrapper.find('#closeNotification')
        await button.trigger('click')
        expect(wrapper.emitted('remove-notification')).toBeTruthy()
    })

})