import Marketplace from "@/components/Marketplace";
import {shallowMount} from "@vue/test-utils";

let wrapper

describe("Clicking on the marketplace tab links", () => {

    beforeEach(async () => {
        wrapper = await shallowMount(Marketplace, {
            computed: {
                isLoggedIn() {
                    return true
                },
                actingAsUser() {
                    return true
                }
            }
        })
        wrapper.vm.$data.error = ""
        wrapper.vm.$data.isLoggedIn = true
    });


    /**
     * Sanity Check
     */
    test("Is Vue instance", () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    /**
     * Checks that when the for sale tab is clicked,
     * checks the changePage method runs and changes the active page variable to 'for sale'
     */
    test("Clicking for sale tab link changes content to for sale content", async () => {
        let wantedElement = wrapper.find("#for-sale-link")
        wantedElement.trigger("click")
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.tabSelected).toStrictEqual("ForSale");
    });

    /**
     * Checks that when the wanted tab is clicked,
     * checks the changePage method runs and changes the active page variable to 'wanted'
     */
    test("Clicking wanted tab link changes content to wanted content", async () => {
        let wantedElement = wrapper.find("#wanted-link")
        wantedElement.trigger("click")
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.tabSelected).toStrictEqual("Wanted");
    });

    /**
     * Checks that when the exchange tab is clicked,
     * checks the changePage method runs and changes the active page variable to 'exchange'
     */
    test("Clicking exchange tab link changes content to exchange content", async () => {
        let wantedElement = wrapper.find("#exchange-link")
        wantedElement.trigger("click")
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.tabSelected).toStrictEqual("Exchange");
    });
})

describe('Pagination, ordering and deletion tests', () => {
    const cards = [
        {
            "id": 500,
            "creator": {
                "id": 100,
                "firstName": "John",
                "lastName": "Smith",
                "homeAddress": {
                    "streetNumber": "3/24",
                    "streetName": "Ilam Road",
                    "city": "Auckland",
                    "region": "Canterbury",
                    "country": "New Zealand",
                    "postcode": "90210"
                },
            },
            "section": "ForSale",
            "created": "2021-04-15T05:10:00Z",
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
        {
            "id": 501,
            "creator": {
                "id": 101,
                "firstName": "John",
                "lastName": "Smith",
                "homeAddress": {
                    "streetNumber": "3/24",
                    "streetName": "Ilam Road",
                    "city": "Dunedin",
                    "region": "Canterbury",
                    "country": "New Zealand",
                    "postcode": "90210"
                },
            },
            "section": "ForSale",
            "created": "2021-05-15T05:12:00Z",
            "displayPeriodEnd": "2021-07-29T05:10:00Z",
            "title": "2005 Honda Fit",
            "description": "Teal, Good ol car. Perfect Condition. As is, where is. Will swap for Lamborghini, nothing else.",
            "keywords": [
                {
                    "id": 600,
                    "name": "Vehicle",
                    "created": "2021-07-15T05:10:00Z"
                }
            ]
        },
        {
            "id": 502,
            "creator": {
                "id": 101,
                "firstName": "John",
                "lastName": "Smith",
                "homeAddress": {
                    "streetNumber": "3/24",
                    "streetName": "Ilam Road",
                    "city": "Christchurch",
                    "region": "Canterbury",
                    "country": "New Zealand",
                    "postcode": "90210"
                },
            },
            "section": "ForSale",
            "created": "2021-03-10T05:10:00Z",
            "displayPeriodEnd": "2021-07-29T05:10:00Z",
            "title": "Bag of chips",
            "description": "Just a good ol bag of chips, nothing special, will trade for a pebble",
            "keywords": [
                {
                    "id": 601,
                    "name": "Food",
                    "created": "2021-07-15T05:10:00Z"
                }
            ]
        }
    ]
    const orderedCards = Marketplace.computed.orderedCards // Ordered computed property.

    beforeEach(async () => {
        wrapper = await shallowMount(Marketplace, {
            computed: {
                isLoggedIn() {
                    return true
                },
                actingAsUser() {
                    return true
                }
            }
        })
        wrapper.vm.$data.error = ""
        wrapper.vm.$data.isLoggedIn = true
        wrapper.vm.$data.cards = cards
    });

    test("orderedCards computed value by created-asc", () => {
        const call = {cards, order: 'created-asc', ...Marketplace.methods}

        // Check ordering by created date ascending
        expect(orderedCards.call(call)[0].id)
            .toBe(501)
        expect(orderedCards.call(call)[1].id)
            .toBe(500)
        expect(orderedCards.call(call)[2].id)
            .toBe(502)
    })

    test("orderedCards computed value by created-desc", () => {
        // Mocks the 'this' state of the component
        const call = {cards, order: 'created-desc', ...Marketplace.methods}

        // Check ordering by created date descending
        expect(orderedCards.call(call)[0].id)
            .toBe(502)
        expect(orderedCards.call(call)[1].id)
            .toBe(500)
        expect(orderedCards.call(call)[2].id)
            .toBe(501)
    })

    test("orderedCards computed value by title", () => {
        // Mocks the 'this' state of the component
        const call = {cards, order: 'title', ...Marketplace.methods}

        // Check ordering by title
        expect(orderedCards.call(call)[0].id)
            .toBe(500)
        expect(orderedCards.call(call)[1].id)
            .toBe(501)
        expect(orderedCards.call(call)[2].id)
            .toBe(502)
    })

    test("orderedCards computed value by location", () => {
        // Mocks the 'this' state of the component
        const call = {cards, order: 'location', ...Marketplace.methods}

        // Check ordering by location
        expect(orderedCards.call(call)[0].id)
            .toBe(500)
        expect(orderedCards.call(call)[1].id)
            .toBe(501)
        expect(orderedCards.call(call)[2].id)
            .toBe(502)
    })

    test("deleteCard method deletes the card", () => {
        // Expect the card to be there at first
        expect(wrapper.vm.$data.cards.find((a) => a.id === 502)).toBeDefined()

        // Delete card 502
        wrapper.vm.deleteCard(502);

        // Check the card is no longer there
        expect(wrapper.vm.$data.cards.find((a) => a.id === 502)).toBeUndefined()
    })

})

