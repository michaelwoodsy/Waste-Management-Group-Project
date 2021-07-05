/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import MarketCard from '@/components/marketplace/MarketCard';
import {mount} from "@vue/test-utils";
import '@/Api';
import {Card, Marketplace, User} from '@/Api';

// Mock the api module
jest.mock("@/Api");

// Mock the dateTime module
jest.mock('@/utils/dateTime', () => ({
    'getTimeDiffStr'() {
        return "2d"
    }
}));

let wrapper;
const mockedGetResponse = {
    data: {
        "id": 500,
        "creator": {
            "id": 100,
            "firstName": "John",
            "lastName": "Smith",
            "homeAddress": {
                "streetNumber": "3/24",
                "streetName": "Ilam Road",
                "city": "Christchurch",
                "region": "Canterbury",
                "country": "New Zealand",
                "postcode": "90210"
            }
        },
        "section": "ForSale",
        "created": "2021-05-13T05:10:00Z",
        "displayPeriodEnd": "2021-07-29T05:10:00Z",
        "title": "1982 Lada Samara",
        "description": "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
        "keywords": [
            {
                "id": 600,
                "name": "Vehicle",
                "created": "2021-07-15T05:10:00Z"
            }
        ]
    },
    statusText: '200: yay'
}

Card.getCard.mockResolvedValue(mockedGetResponse)

Card.extendDisplay.mockImplementation(() => {
    const date = new Date(mockedGetResponse.data.displayPeriodEnd)
    date.setDate(date.getDate() + 14)
    mockedGetResponse.data.displayPeriodEnd = date.toDateString()
})

afterEach(() => {
    wrapper.destroy()
})

describe('Testing the MarketCard component', () => {

    beforeEach(() => {
        wrapper = mount(MarketCard, {
            propsData: {
                cardData: mockedGetResponse.data
            },
            computed: {
                isCardCreator() {
                    return false
                },
                canDeleteCard() {
                    return false
                },
                userId() {
                    return 1
                }
            }
        })
    })

    // Test the cardCreatorName computed property
    test("Test the cardCreatorName computed", () => {
        expect(wrapper.vm.cardCreatorName).toBe("John Smith")
    })

    // Test the cardCreatorName computed property
    test("Test the location computed", () => {
        const location = MarketCard.computed.location
        // Function for mocking the user address
        const mockAddress = (address) => ({"cardData": {"creator": {"homeAddress": address}}})

        expect(location.call(mockAddress({city: "Christchurch", region: "Canterbury"})))
            .toBe("Christchurch")
        expect(location.call(mockAddress({city: "Auckland"})))
            .toBe("Auckland")
        expect(location.call(mockAddress({region: "Canterbury"})))
            .toBe("Canterbury")
        expect(location.call(mockAddress({country: "New Zealand"})))
            .toBe("New Zealand")
    })

    // Test the isCardOwner computed property
    test("Tests the isCardOwner property", () => {
        const isCardCreator = MarketCard.computed.isCardCreator
        // Function for mocking the 'this' state
        const mockThis = (id) => ({
            "cardData": mockedGetResponse.data, "$root": {
                "$data": {
                    "user": {
                        isUser(userId) {
                            return userId === id
                        }
                    }
                }
            }
        })

        expect(isCardCreator.call(mockThis(100))).toBeTruthy()
        expect(isCardCreator.call(mockThis(2))).toBeFalsy()
    })

    // Test the deleteCard method
    test("Test the deleteCard method emits a 'card-deleted' event", async () => {
        const mockDeleteCard = jest.fn(() => Promise.resolve({}));
        Marketplace.deleteCard.mockImplementationOnce(mockDeleteCard);
        await wrapper.vm.deleteCard()

        // Expect the component to emit the deleteCard event
        expect(wrapper.emitted('card-deleted')).toBeTruthy()
        // Expect the component to emit the correct ID
        expect(wrapper.emitted('card-deleted')[0]).toEqual([mockedGetResponse.data.id])
    })

    test('Test toggleDetails method switches showDetails from false to true', () => {
        wrapper.vm.$data.showDetails = false
        wrapper.vm.toggleDetails()
        expect(wrapper.vm.$data.showDetails).toBeTruthy()
    })

    test('Test toggleDetails method switches showDetails from true to false', () => {
        wrapper.vm.$data.showDetails = true
        wrapper.vm.toggleDetails()
        expect(wrapper.vm.$data.showDetails).toBeFalsy()
    })

    test('Test time until expiry computed method', () => {
        const now = new Date()
        const displayEnd = new Date("2021-07-29T05:10:00Z")
        expect(wrapper.vm.timeUntilExpiry().timeLeft).toEqual(displayEnd.getTime() - now.getTime())
    })

    // Test the deleteCard method
    test("Test the deleteCard method makes a call to the api", async () => {
        // Mock the delete api method
        const mockDeleteCard = jest.fn(() => Promise.resolve({}));
        Marketplace.deleteCard.mockImplementationOnce(mockDeleteCard);

        // Run the deleteCard method
        await wrapper.vm.deleteCard()

        // Expect the delete api method to be called
        expect(mockDeleteCard).toBeCalledTimes(1);
    })

    // Test the deleteCard method sets error flag on error
    test("Test the deleteCard method sets error flag on error", async () => {
        // Mock the api module call
        const mockDeleteCard = jest.fn(() => Promise.reject(new Error("Error")));
        Marketplace.deleteCard.mockImplementationOnce(mockDeleteCard);

        // Run the deleteCard method
        wrapper.vm.deleteCard()
        await wrapper.vm.$nextTick()

        // Expect the error value to be set
        expect(wrapper.vm.$data.error).toEqual("Error");
    })

    // Test the deleteCard method keeps keeps the modal open on error
    test("Test the delete modal closes when the request is successful", async () => {
        // Mock the api call
        const mockDeleteCard = jest.fn(() => Promise.resolve({}));
        Marketplace.deleteCard.mockImplementationOnce(mockDeleteCard);

        // Run the deleteCard method
        wrapper.vm.deleteCard()
        await wrapper.vm.$nextTick()

        // Expect the delete modal to be hidden
        expect(wrapper.find(".modal").classes().includes("show")).toBeFalsy();
    })

    test('Test the extend card function emits an extend card event', async () => {
        await wrapper.vm.extendCard()
        await wrapper.vm.$nextTick()

        expect(wrapper.emitted('card-extended')).toBeTruthy()
    })

    test("Send message method gives error message when text field is empty", async () => {
        wrapper.vm.$data.message = ""
        await wrapper.vm.sendMessage()
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.$data.messageError).toBeTruthy()
        expect(wrapper.vm.$data.messageSent).toBeFalsy()
    })

    test("Send message method succeeds when a valid message is given", async () => {
        User.sendCardMessage.mockImplementationOnce(jest.fn(() => {
            return {
                data: {
                    messageId: 1
                }
            }
        }))

        wrapper.vm.$data.message = "This is a valid message"
        await wrapper.vm.sendMessage()
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.$data.messageError).toBeFalsy()
        expect(wrapper.vm.$data.messageSent).toBeTruthy()
    })

})