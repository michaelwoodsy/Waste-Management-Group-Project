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
        Business.getReviews.mockResolvedValue({
            data: {
                reviews: reviews,
                totalReviews: reviews.length
            }
        })
        wrapper = shallowMount(BusinessReviews, {
            propsData: {
                businessId: 1
            }
        })
    })

    afterEach(() => {
        jest.resetAllMocks()
    })

    test("Reviews are obtained when mounted", async () => {
        expect(Business.getReviews).toHaveBeenCalledTimes(1) // called when mounted
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

    test("Changing page makes request for new page", async () => {
        await wrapper.vm.changePage(2)
        await wrapper.vm.$nextTick()
        expect(Business.getReviews).toHaveBeenCalledTimes(2) // called once when mounted and once when page changed
        expect(wrapper.vm.$data.page).toStrictEqual(2)
    })

})