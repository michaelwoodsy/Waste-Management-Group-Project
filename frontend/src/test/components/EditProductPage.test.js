/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import EditProductPage from '@/components/product-catalogue/EditProductPage';
import Vue from 'vue';

const VueTestUtils = require('@vue/test-utils')


// Define business and product to mock
let businessId = 2
let productId = "WATT-420-BEANS"
let loggedIn = true
// Will become the fake vue instance
let wrapper;
let computed = {
    isLoggedIn() {
        return loggedIn
    },
    businessId() {
        return businessId
    },
    productId() {
        return productId
    },
    businessesAdministered() {
        return [{id: 2}]
    },
    isAdminOf() {
        return businessId === 2
    },
    changesMade() {
        return false
    },
    nameValid() {
        return true
    },
    priceValid() {
        return true
    },
    idValid() {
        return true
    }
}

// Mock the business api module, once implemented
jest.mock('@/Api', () => ({
    'Business': {
        // Mock fake product data
        getProducts() {
            return new Promise((resolve) => {
                resolve ({
                    data: [
                    {
                        "id": "WATT-420-BEANS",
                        "name": "Watties Baked Beans - 420g can",
                        "description": "Baked Beans as they should be.",
                        "manufacturer": "Heinz Wattie's Limited",
                        "recommendedRetailPrice": 2.2,
                        "created": "2021-04-16T23:08:48.584Z",
                        "images": [
                            {
                                "id": 1234,
                                "filename": "/media/images/23987192387509-123908794328.png",
                                "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                            }
                        ]
                    }

                ]})
            })
        }
    }
}));

// Setup before each test
beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(EditProductPage, {
        stubs: ['router-link', 'router-view', "login-required", "admin-required"],
        computed
    })
});

// Reset any test variables that might have been changed
afterEach(() => {
    businessId = 2
    productId = "WATT-420-BEANS"
    loggedIn = true
})

describe('EditProductPage Component Tests', () => {

    // Test the title is there
    test('displays title', () => {
        expect(wrapper.find('h1').text()).toBe('Edit Product')
    })

    // Test a login required message is shown when not logged in
    test("a message is shown when the user isn't logged in", () => {
        loggedIn = false;
        wrapper = VueTestUtils.shallowMount(EditProductPage, {
            stubs: ['router-link', 'router-view', "login-required", "admin-required"],
            computed
        })
        expect(wrapper.findComponent({name: 'login-required'}).exists()).toBeTruthy()
    })

    // Test an admin required message is shown when the user is not an admin of the business
    test("a message is shown when the user isn't an admin of the business", () => {
        businessId = 3;
        wrapper = VueTestUtils.shallowMount(EditProductPage, {
            stubs: ['router-link', 'router-view', "login-required", "admin-required"],
            computed
        })
        expect(wrapper.findComponent({name: 'admin-required'}).exists()).toBeTruthy()
    })

    // Check the input fields are prefilled
    test('input fields are prefilled with data', () => {
        expect(wrapper.find('#id').element.value.length).toBeGreaterThan(2)
        expect(wrapper.find('#name').element.value.length).toBeGreaterThan(2)
        expect(wrapper.find('#description').element.value.length).toBeGreaterThan(2)
        expect(wrapper.find('#rrp').element.value.length).toBeGreaterThan(2)
        expect(wrapper.find('#manufacturer').element.value.length).toBeGreaterThan(2)
    })

    // Check an error message is displayed if the product doesn't exist
    test("a message is shown when the product doesn't exist", async () => {
        productId = "Non existent";
        wrapper = VueTestUtils.shallowMount(EditProductPage, {
            stubs: ['router-link', 'router-view', "login-required", "admin-required", "alert"],
            computed
        })
        await Vue.nextTick() // Otherwise the loading ... message is displayed

        const alertComponent = wrapper.findComponent({name: 'alert'})
        expect(alertComponent.exists()).toBeTruthy()
        expect(alertComponent.text()).toContain("no product")
    })

    // Check the idValid computed field
    test("testing idField computed property", async () => {
        const fakeId = (id) => {
            return {newProduct: {id: id}}
        }
        expect(EditProductPage.computed.idValid.call(fakeId("Baked Beans"))).toBeFalsy()
        expect(EditProductPage.computed.idValid.call(fakeId(""))).toBeFalsy()
        expect(EditProductPage.computed.idValid.call(fakeId("Baked"))).toBeTruthy()
        expect(EditProductPage.computed.idValid.call(fakeId("Baked-beans123-"))).toBeTruthy()
    })

    // Check the nameValid computed field
    test("testing nameValid computed property", async () => {
        const fakeName = (name) => {
            return {newProduct: {name: name}}
        }
        expect(EditProductPage.computed.nameValid.call(fakeName("Myrtle's Motorcycles"))).toBeTruthy()
        expect(EditProductPage.computed.nameValid.call(fakeName(""))).toBeFalsy()
    })

    // Check the priceValid computed field
    test("testing priceValid computed property", async () => {
        const fakePrice = (price) => {
            return {newProduct: {recommendedRetailPrice: price}}
        }
        expect(EditProductPage.computed.priceValid.call(fakePrice(1.22))).toBeTruthy()
        expect(EditProductPage.computed.priceValid.call(fakePrice(1))).toBeTruthy()
        expect(EditProductPage.computed.priceValid.call(fakePrice(1.00))).toBeTruthy()
        expect(EditProductPage.computed.priceValid.call(fakePrice(1.555))).toBeFalsy()
        expect(EditProductPage.computed.priceValid.call(fakePrice(""))).toBeTruthy()
        expect(EditProductPage.computed.priceValid.call(fakePrice("a"))).toBeFalsy()
        expect(EditProductPage.computed.priceValid.call(fakePrice(" "))).toBeFalsy()
    })

    // Check a message is shown when a field isn't valid
    test("a message is shown when the input fields aren't valid", async () => {
        // Setup with idValid, nameValid, priceValid set to false
        wrapper = await VueTestUtils.shallowMount(EditProductPage, {
            stubs: ['router-link', 'router-view', "login-required", "admin-required"],
            computed: {...computed, idValid() {return false}, nameValid() {return false},
                priceValid() {return false}}
        })
        await wrapper.setData({nameBlur: true, priceBlur: true, idBlur: true}) // Set the elements as being blurred

        // Find the form group divs
        const foundFormGroupDivs = wrapper.findAll("div.form-group");

        // Find and test the Id input
        const idDiv = foundFormGroupDivs.filter(div => div.find('label').text() === 'ID*')
        const idInput = idDiv.at(0).find('input')
        // Expect the 'is-invalid' class is set on the input element, this will display the fixes message
        expect(idInput.classes().find(el => el === 'is-invalid')).toBeDefined()

        // Find and test the name input
        const nameDiv = foundFormGroupDivs.filter(div => div.find('label').text() === 'Name*')
        const nameInput = nameDiv.at(0).find('input')
        // Expect the 'is-invalid' class is set on the input element, this will display the fixes message
        expect(nameInput.classes().find(el => el === 'is-invalid')).toBeDefined()

        // Find and test the price input
        const priceDiv = foundFormGroupDivs.filter(div => div.find('label').text() === 'Recommended Retail Price')
        const priceInput = priceDiv.at(0).find('input')
        // Expect the 'is-invalid' class is set on the input element, this will display the fixes message
        expect(priceInput.classes().find(el => el === 'is-invalid')).toBeDefined()

    })
})

describe("Edit Product Images Test",  () => {

    // Test that we can remove an uploaded image
    test('Removing an image from the frontend list', () => {
        wrapper.vm.$data.images = [{
            url: "image.png",
            file: ""
            }, {
            url: "other_image.jpg",
            file: ""
        }]
        wrapper.vm.removeImage("image.png")
        expect(wrapper.vm.$data.images).toEqual([{
            url: "other_image.jpg",
            file: ""
        }])
    })
})