/**
 * @jest-environment jsdom
 */

import {afterEach, beforeEach, describe, test} from "@jest/globals";
import AddressInputFields from '@/components/AddressInputFields';
const VueTestUtils = require('@vue/test-utils')
import axios from 'axios';

// Will become the fake vue instance
let wrapper;

// Mock the axios module
jest.mock('axios');

// Setup before each test
beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(AddressInputFields)
});

// Reset any test variables that might have been changed
afterEach(() => {
    wrapper.destroy()
})


describe('AddressInputFields Component Tests', () => {

    // Check the valid computed field
    test("testing 'valid' computed property", () => {
        // Provides a fake 'this' context
        const getFakeData = (country, streetName) => {
            return { msg: {country, streetName} }
        }

        // Get the valid computed property
        const valid = AddressInputFields.computed.valid

        // Run the computed with different data
        expect(valid.call(getFakeData(null, null))).toBeTruthy()
        expect(valid.call(getFakeData("Country is required", null))).toBeFalsy()
        expect(valid.call(getFakeData(null, "A street name is required"))).toBeFalsy()
        expect(valid.call(getFakeData(
            "Country is required",
            "A street name is required with a street number"))).toBeFalsy()
    })

    // Test the getPhotonAddress method
    test("testing getPhotonAddress returns response from photon api", async() => {
        const data = { fakeResponse: 1 }
        axios.get.mockImplementationOnce(() => Promise.resolve(data));
        await expect(wrapper.vm.getPhotonAddress('8 Rountree')).resolves.toEqual(data);
    })

    // Test the getPhotonAddress method cancels the previous request
    test("testing getPhotonAddress cancels the previous photon api request", async() => {
        // Setup mock data
        const mockCancelToken = jest.fn(() => {})
        wrapper.setData({cancelToken: mockCancelToken})

        // Setup mock axios method
        axios.get.mockImplementationOnce(() => Promise.resolve({}));

        // Run the method
        await wrapper.vm.getPhotonAddress('8 Rountree')

        // Expect the previous request to be canceled
        expect(mockCancelToken).toHaveBeenCalledTimes(1);
    })

    // Test the addressEntered method
    test("testing addressEntered method doesn't make a request with less than 3 characters", async() => {
        // Setup mock getPhotonAddress method
        const mockGetPhotonAddress = jest.spyOn(AddressInputFields.methods, 'getPhotonAddress')
        wrapper = VueTestUtils.shallowMount(AddressInputFields)

        // Set mock data
        wrapper.setData({fullAddress: '8'})

        // Run the method
        await wrapper.vm.addressEntered()

        // Expect the getPhotonAddress method to not be called
        expect(mockGetPhotonAddress).toHaveBeenCalledTimes(0);
    })

    test("testing addressEntered method makes request to photon and sets suggestions", async() => {
        const data = {
            data: {
                "features": [
                    {
                        "properties": {
                            "osm_id": 5264935713,
                            "country": "New Zealand / Aotearoa",
                            "city": "Christchurch",
                            "countrycode": "NZ",
                            "postcode": "8041",
                            "county": "Christchurch City",
                            "type": "house",
                            "osm_type": "N",
                            "osm_key": "place",
                            "housenumber": "9",
                            "street": "Rountree Street",
                            "district": "Upper Riccarton",
                            "osm_value": "house",
                            "state": "Canterbury"
                        }
                    }
                ]
            }
        }
        axios.get.mockImplementationOnce(() => Promise.resolve(data));

        // Set mock data
        wrapper.setData({fullAddress: '8 Rountree Street'})

        // Run the method
        await wrapper.vm.addressEntered()

        // Expect the suggestions array to now contain that address
        expect(wrapper.vm.$data.suggestions).toEqual([{
            id: 5264935713,
            streetNumber: "9",
            streetName: "Rountree Street",
            city: "Christchurch",
            region: "Canterbury",
            country: 'New Zealand / Aotearoa',
            postcode: "8041"
        }]);
    })

    // Test getAddressString
    test("testing getAddressString returns a command separated string", async() => {
        // Create some test data
        const locationFull = {
            id: 5264935713,
            streetNumber: "9",
            streetName: "Rountree Street",
            city: "Christchurch",
            region: "Canterbury",
            country: 'New Zealand / Aotearoa',
            postcode: "8041"
        }
        const locationMedium = {
            city: "Wanaka",
            country: "New Zealand"
        }
        const locationSmall = {
            country: "New Zealand"
        }

        // Expect the correct address string
        expect(wrapper.vm.getAddressString(locationFull)).toEqual(
            '9 Rountree Street, Christchurch 8041, Canterbury, New Zealand / Aotearoa');
        expect(wrapper.vm.getAddressString(locationMedium)).toEqual(
            'Wanaka, New Zealand');
        expect(wrapper.vm.getAddressString(locationSmall)).toEqual(
            'New Zealand');
    })

    // Test swapInputMode
    test("testing swapInputMode shows manual address input fields", async() => {
        await wrapper.vm.swapInputMode()

        // Expect manual input fields to exist
        expect(wrapper.find('#addressRegion').exists()).toBeTruthy();
        expect(wrapper.find('#addressCity').exists()).toBeTruthy();
        expect(wrapper.find('#addressStreet').exists()).toBeTruthy();
        expect(wrapper.find('#addressNumber').exists()).toBeTruthy();
        expect(wrapper.find('#addressCountry').exists()).toBeTruthy();
    })

    // Test the address search box is shown initially
    test("testing address search box is shown", async() => {
        expect(wrapper.find('#fullAddress').exists()).toBeTruthy();
    })
})