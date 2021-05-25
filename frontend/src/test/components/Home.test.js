import "@jest/globals";
import MarketCard from '@/components/MarketCard';
import Home from "@/components/Home";
import {shallowMount} from "@vue/test-utils";
import '@/Api';

jest.mock('@/Api')

let wrapper;

beforeAll(() => {
    wrapper = shallowMount(Home, {
        data() {
            return {
                cards: [
                    {
                        "id": 1,
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
                ]
            }
        },
        computed: {
            isLoggedIn() {
                return true
            },
            actor() {
                return {}
            },
            isActingAsUser() {
                return true
            }
        }
    })
})

describe('Jest tests for the home component', () => {

    beforeEach(async () => {
        wrapper.vm.$data.cards = [
            {
                "id": 1,
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
        ]
        await wrapper.vm.$nextTick()
    })

    test('Test deleteCard is called when card-deleted event is triggered', async () => {
        const marketCard = wrapper.findComponent(MarketCard)
        await marketCard.vm.$emit('card-deleted', 1)

        expect(wrapper.vm.$data.cards.length).toStrictEqual(0)
    })

    test('Test extendCard is called when card-extended event is triggered', async () => {
        const oldDate = new Date(wrapper.vm.$data.cards[0].displayPeriodEnd)
        const newDate = new Date(wrapper.vm.$data.cards[0].displayPeriodEnd)
        newDate.setDate(oldDate.getDate() + 14)
        const marketCard = wrapper.findComponent(MarketCard)
        await marketCard.vm.$emit('card-extended', 1, newDate.toDateString())

        expect(new Date(wrapper.vm.$data.cards[0].displayPeriodEnd) > oldDate).toBeTruthy()
    })

})