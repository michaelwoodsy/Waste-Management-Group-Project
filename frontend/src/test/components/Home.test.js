import "@jest/globals";
import MarketCard from '@/components/marketplace/MarketCard';
import Home from "@/components/Home";
import {shallowMount} from "@vue/test-utils";
import {User} from "@/Api";

jest.mock('@/Api')

let wrapper;

const cards = [
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
let notifications = [
    { id: 0,
        title: "Card Expiry",
        message: "This card is about to expire",
        created: "2/07/2021 4:34pm",
        card: "Looking for plums"},
    { id: 1,
        title: "Card Expiry",
        message: "This card is about to expire",
        created: "1/07/2021 6:37pm",
        card: "Apples for Oranges"}
]

describe('Jest tests for the home component', () => {

    beforeEach(() => {

        const getCardsResponse = {
            data: [...cards]
        }

        User.getCards.mockResolvedValue(getCardsResponse)

        wrapper = shallowMount(Home, {
            computed: {
                isLoggedIn() {
                    return true
                },
                actor() {
                    return {
                        id: 1,
                        name: 'Myrtle Tremontana',
                        type: 'user'
                    }
                },
                isActingAsUser() {
                    return true
                }
            }
        })

    })

    test('Test deleteCard is called when card-deleted event is triggered', async () => {
        const marketCard = wrapper.findComponent(MarketCard)
        await marketCard.vm.$emit('card-deleted', 1)

        expect(wrapper.vm.$data.cards.length).toStrictEqual(0)
    })

    test('Test extendCard is called when card-extended event is triggered', async () => {
        console.log(wrapper.vm.$data.cards)
        const oldDate = new Date(wrapper.vm.$data.cards[0].displayPeriodEnd)
        const newDate = new Date(wrapper.vm.$data.cards[0].displayPeriodEnd)
        newDate.setDate(oldDate.getDate() + 14)
        const marketCard = wrapper.findComponent(MarketCard)
        await marketCard.vm.$emit('card-extended', 1, newDate.toDateString())

        expect(new Date(wrapper.vm.$data.cards[0].displayPeriodEnd) > oldDate).toBeTruthy()
    })

    test('Expired cards computed property', () => {
        const pastDate = new Date()
        pastDate.setDate(pastDate.getDate() - 7) // Set date to past
        wrapper.vm.$data.cards[0].displayPeriodEnd = pastDate.toDateString()

        expect(wrapper.vm.expiredCards.length).toStrictEqual(1)
        expect(wrapper.vm.activeCards.length).toStrictEqual(0)
    })

    test('Active cards computed property', () => {
        const futureDate = new Date()
        futureDate.setDate(futureDate.getDate() + 7) // Set date to past
        wrapper.vm.$data.cards[0].displayPeriodEnd = futureDate.toDateString()

        expect(wrapper.vm.expiredCards.length).toStrictEqual(0)
        expect(wrapper.vm.activeCards.length).toStrictEqual(1)
    })

    test('Can remove notifications from the notifications list',  () => {
        wrapper.vm.$data.notifications = notifications
        expect(wrapper.vm.$data.notifications.length).toStrictEqual(2)
        wrapper.vm.removeNotification(1)
        expect(wrapper.vm.$data.notifications.length).toStrictEqual(1)
    })

    test('calling formatDateTime retrieves correct dateTime format',  () => {
        const date = '2021-07-10T08:28:21.200Z'
        const formattedDate = wrapper.vm.formatDateTime(date)
        expect(formattedDate).toEqual('10/07/2021 20:28')
    })

})