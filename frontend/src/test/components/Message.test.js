import '@jest/globals'
import {shallowMount} from "@vue/test-utils";
import {User} from "@/Api";
import Message from "@/components/marketplace/Message";

jest.mock('@/Api')
User.sendCardMessage.mockImplementation(jest.fn(() => {}))

let wrapper

const message = {
    id: 1,
    text: 'This is a test message',
    receiverId: 1,
    card: {
        title: 'Test Card',
        creator: {
            id: 1,
            firstName: 'John',
            lastName: 'Smith'
        }
    },
    sender: {
        id: 2
    }
}

describe('Tests for the Message component', () => {

    beforeEach(() => {
        wrapper = shallowMount(Message, {
            propsData: {
                message: message
            }
        })
    })

    test('Trying to reply to a message with no text result in error', async () => {
        wrapper.vm.$data.reply = null
        await wrapper.vm.sendReply()
        expect(wrapper.vm.replyError).toBeTruthy()
        expect(wrapper.vm.replySent).toBeFalsy()
    })

    test('Trying to reply to a message with valid reply results in success', async () => {
        wrapper.vm.$data.reply = 'This is a reply'
        await wrapper.vm.sendReply()
        expect(wrapper.vm.replyError).toBeFalsy()
        expect(wrapper.vm.replySent).toBeTruthy()
    })

    test('Clear reply sets the correct values', () => {
        wrapper.vm.$data.reply = 'Some text'
        wrapper.vm.$data.replyError = true
        wrapper.vm.$data.replySent = true
        wrapper.vm.clearReply()
        expect(wrapper.vm.$data.reply).toBeNull()
        expect(wrapper.vm.$data.replyError).toStrictEqual(false)
        expect(wrapper.vm.$data.replySent).toStrictEqual(false)
    })

})