import '@jest/globals'
import BusinessReviews from "@/components/business/BusinessReviews"
import {shallowMount} from "@vue/test-utils"
import {Business} from "@/Api"

jest.mock("@/Api")

let wrapper;

const reviews = [
    {
        reviewId: 1,
        rating: 3,
        reviewMessage: 'Was a nice product',
        reviewResponse: null,
        sale: {}
    }
]

describe("Tests for the BusinessReviews component", () => {

    beforeEach(() => {
        wrapper = shallowMount(BusinessReviews, {
            propsData: {
                businessId: 1
            }
        })
        Business.getReviews.mockResolvedValue({data: reviews})
    })

    test("Reviews are obtained when getReviews is called", async () => {
        await wrapper.vm.getReviews()
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.reviews.length).toStrictEqual(1)
    })

    test("Error is properly set when getting reviews fails", async () => {
        Business.getReviews.mockImplementation(jest.fn(() => {
            throw new Error("This is an error")
        }))
        await wrapper.vm.getReviews()
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.error).toStrictEqual("This is an error")
    })

})