/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import MarketCard from '@/components/MarketCard';
import {shallowMount, mount} from "@vue/test-utils";
import {test} from "@jest/globals";

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

beforeEach(() => {
    wrapper = mount(MarketCard, {
        propsData: mockedProps
    })
})

afterEach(() => {
    wrapper.destroy()
})

describe('Testing the MarketCard component', () => {

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
})