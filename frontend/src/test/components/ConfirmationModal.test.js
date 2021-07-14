import '@jest/globals'
import ConfirmationModal from "@/components/ConfirmationModal";
import {shallowMount} from "@vue/test-utils";

let wrapper

describe('Tests for the ConfirmationModal Component', function () {

    beforeEach(() => {
        wrapper = shallowMount(ConfirmationModal)
    })

    test('Test that clicking the confirm button emits the confirm event', async () => {
        const button = wrapper.find("#confirmButton")
        await button.trigger('click')
        expect(wrapper.emitted('confirm')).toBeTruthy()
    })

});