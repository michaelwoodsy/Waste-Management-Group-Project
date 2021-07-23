import '@jest/globals'
import ConfirmationModal from "@/components/ConfirmationModal";
import {shallowMount} from "@vue/test-utils";

let wrapper

describe('Tests for the ConfirmationModal Component', function () {

    test('Test that clicking the confirm button emits the confirm event', async () => {
        wrapper = shallowMount(ConfirmationModal)
        const button = wrapper.find("#confirmButton")
        await button.trigger('click')
        expect(wrapper.emitted('confirm')).toBeTruthy()
        wrapper.destroy()
    })

});