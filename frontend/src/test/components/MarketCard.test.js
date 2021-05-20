/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import MarketCard from '@/components/MarketCard';
import {mount} from "@vue/test-utils";
import {test} from "@jest/globals";
import '@/Api';
import {Marketplace} from '@/Api';

// Mock the api module
jest.mock("@/Api");

// Mock the dateTime module
jest.mock('@/utils/dateTime', () => ({
    'getTimeDiffStr' () {
        return "2d"
    }
}));

let wrapper;
const mockedProps = {
    cardData: {
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
    }
}


afterEach(() => {
    wrapper.destroy()
})

describe('Testing the MarketCard component', () => {

    beforeEach(() => {
        wrapper = mount(MarketCard, {
            propsData: mockedProps,
            computed: {
                isCardCreator() {
                    return false
                },
                canDeleteCard() {
                    return false
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
        const mockThis = (id) => ({"cardData": mockedProps.cardData, "$root": {"$data": {"user": {isUser (userId) {return userId === id}}}}})

        expect(isCardCreator.call(mockThis(100))).toBeTruthy()
        expect(isCardCreator.call(mockThis(2))).toBeFalsy()
    })

    // Test the deleteCard method
    test("Test the deleteCard method emits a 'cardDeleted' event", async () => {
        const mockDeleteCard = jest.fn(() => Promise.resolve({}));
        Marketplace.deleteCard.mockImplementationOnce(mockDeleteCard);
        await wrapper.vm.deleteCard()

        // Expect the component to emit the deleteCard event
        expect(wrapper.emitted().cardDeleted).toBeTruthy()
        // Expect the component to emit the correct ID
        expect(wrapper.emitted().cardDeleted[0]).toEqual([mockedProps.cardData.id])
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
})