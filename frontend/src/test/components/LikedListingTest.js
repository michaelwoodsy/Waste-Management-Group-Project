import "@jest/globals"
import LikedListing from "@/components/sale-listing/LikedListing";
import {Business} from "@/Api";

const VueTestUtils = require('@vue/test-utils')

jest.mock('@/Api')

const likedListing = {
    id: 1,
    user: {},
    listing: {
        id: 1,
        inventoryItem: {
            product: {
                images: [],
                name: "Name"
            }
        },
        business: {
            id: 1,
            name: "Some Business"
        }
    },
    starred: false,
    tag: "NONE"
}

let wrapper;

describe("Tests for the LikedSaleListingComponent", () => {

    // Setup before each test
    beforeEach(() => {
        wrapper = VueTestUtils.shallowMount(LikedListing, {
            propsData: {
                listing: likedListing
            },
            methods: {
                formatPrice() {
                    return null
                },
                formatAddress() {
                    return null
                }
            }
        })
    });

    test("Test the starListing method calls Business.starListing with false", async () => {
        wrapper.vm.$data.stared = true
        await wrapper.vm.starListing()
        expect(Business.starListing).toBeCalledWith(expect.any(Number), false)
    })

    test("Test the starListing method calls Business.starListing with true", async () => {
        wrapper.vm.$data.stared = false
        jest.spyOn(wrapper.vm, 'likeListing').mockReturnValue(() => Promise.resolve())
        await wrapper.vm.starListing()
        expect(Business.starListing).toBeCalledWith(expect.any(Number), true)
    })

    test("Test the starListing method calls likeListing method", async () => {
        wrapper.vm.$data.stared = false
        wrapper.vm.$data.liked = false
        jest.spyOn(wrapper.vm, 'likeListing').mockReturnValue(() => Promise.resolve())
        await wrapper.vm.starListing()
        expect(wrapper.vm.likeListing).toBeCalled()
    })

})
