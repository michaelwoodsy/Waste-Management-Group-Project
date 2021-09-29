import '@jest/globals'
import Review from "@/components/business/Review";
import {shallowMount} from "@vue/test-utils"
import {Images} from "@/Api";

jest.mock('@/Api')

let wrapper;

describe("Tests for the Review component", () => {

    beforeEach(() => {
        Images.getImageURL.mockImplementation(jest.fn(() => {
            return '/media/defaults/defaultProfile_thumbnail.jpg'
        }))
        wrapper = shallowMount(Review, {
            propsData: {
                review: {
                    reviewId: 1,
                    sale: {
                        productName: 'Some Product'
                    },
                    user: {
                        primaryImageId: 1,
                        images: [
                            {
                                id: 1,
                                thumbnailFilename: '/media/defaults/defaultProfile_thumbnail.jpg'
                            }
                        ]
                    },
                    rating: 3,
                    reviewMessage: 'Was great thanks',
                    reviewResponse: null,
                    created: '2021-09-27'
                }
            }
        })
    })

    test('Test that the image URL is set to correct URL', () => {
        expect(wrapper.vm.$data.profileImage).toStrictEqual('/media/defaults/defaultProfile_thumbnail.jpg')
    })

    test('Test that the image URL is set to correct URL when no primary image', () => {
        wrapper = shallowMount(Review, {
            propsData: {
                review: {
                    reviewId: 1,
                    sale: {
                        productName: 'Some Product'
                    },
                    user: {
                        primaryImageId: 2,
                        images: [
                            {
                                id: 1,
                                thumbnailFilename: '/media/defaults/defaultProfile_thumbnail.jpg'
                            }
                        ]
                    },
                    rating: 3,
                    reviewMessage: 'Was great thanks',
                    reviewResponse: null,
                    created: '2021-09-27'
                }
            }
        })
        expect(wrapper.vm.$data.profileImage).toStrictEqual('/media/defaults/defaultProfile_thumbnail.jpg')
    })

})