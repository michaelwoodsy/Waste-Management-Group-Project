import '@jest/globals'
import Pagination from "@/components/Pagination";
import {shallowMount} from "@vue/test-utils";

let wrapper

describe('Tests for the ConfirmationModal Component', function () {

    beforeEach(() => {
        wrapper = shallowMount(Pagination, {
            propsData: {
                currentPage: 4,
                totalItems: 100,
                itemsPerPage: 10
            }
        })
    })

    test('Test that clicking a page number emits an update event', async () => {
        const button = wrapper.find(".page-link")
        window.scrollTo = jest.fn(); // Mock the window.scrollTo function so the test doesn't fail
        await button.trigger('click')
        expect(wrapper.emitted('update:currentPage')).toBeTruthy()
    })

});