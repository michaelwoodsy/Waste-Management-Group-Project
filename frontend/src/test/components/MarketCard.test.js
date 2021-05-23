/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import MarketCard from '@/components/MarketCard';
import {mount} from "@vue/test-utils";
import {test} from "@jest/globals";
// import axios from 'axios';
//
// // Mock the axios module
// jest.mock('axios');

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
    test("Test the deleteCard method emits a 'cardDeleted' event", () => {
        wrapper.vm.deleteCard()

        // Expect the component to emit the deleteCard event
        expect(wrapper.emitted().cardDeleted).toBeTruthy()
        // Expect the component to emit the correct ID
        expect(wrapper.emitted().cardDeleted[0]).toEqual([mockedProps.cardData.id])
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
        const now =  new Date()
        const createdDate = new Date("2021-05-13T05:10:00Z")
        const twoWeeksAfter = new Date(createdDate.setDate(createdDate.getDate() + 14))
        expect(wrapper.vm.timeUntilExpiry().timeLeft).toEqual(twoWeeksAfter.getTime() - now.getTime())
    })
})