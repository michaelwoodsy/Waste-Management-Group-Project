import '@jest/globals'
import ReviewModal from "@/components/ReviewModal"
import {shallowMount} from "@vue/test-utils"
import {User} from "@/Api"
import userState from '@/store/modules/user'

jest.mock('@/Api')
jest.mock('@/store/modules/user')

let wrapper;

const sale = {
    id: 1,
    review: null
}

describe('Tests for the review component', () => {

    beforeEach(() => {
        jest.clearAllMocks()
        wrapper = shallowMount(ReviewModal, {
            propsData: {
                sale: sale
            }
        })
        userState.actor.mockResolvedValue({
            id: 1
        })
    })

    test('Trying to send a review with no rating selected results in error', async () => {
        await wrapper.vm.checkInputs()
        expect(wrapper.vm.$data.invalidRating).toBeTruthy()
    })

    test('Trying to send a review with a valid rating is successful and emits event', async () => {
        User.leaveReview.mockImplementation(jest.fn())

        wrapper.vm.$data.reviewForm.rating = 3
        await wrapper.vm.checkInputs()
        expect(wrapper.vm.$data.invalidRating).toBeFalsy()
        expect(User.leaveReview).toHaveBeenCalledTimes(1)
        expect(wrapper.emitted('update-data')).toBeTruthy()
    })

    test('An error from the API endpoint sets the error field correctly', async () => {
        User.leaveReview.mockImplementation(() => {
            throw new Error("This is an error")
        })

        wrapper.vm.$data.reviewForm.rating = 3
        await wrapper.vm.checkInputs()
        expect(User.leaveReview).toHaveBeenCalledTimes(1)
        expect(wrapper.emitted('update-data')).toBeFalsy()
        expect(wrapper.vm.$data.error).toStrictEqual("This is an error")
    })

    test('When review is null, the form section is displayed', () => {
        expect(wrapper.find('#reviewForm')).toBeTruthy()
    })

    test('When there is a review, the review section is displayed', () => {
        wrapper.setProps({
            sale: {
                id: 1,
                review: {
                    rating: 3,
                    message: 'Some Review'
                }
            }
        })
        expect(wrapper.find('#reviewSection')).toBeTruthy()
    })

})