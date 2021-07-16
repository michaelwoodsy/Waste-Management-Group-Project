import "@jest/globals";
import MarketCard from '@/components/marketplace/MarketCard';
import Home from "@/components/Home";
import {shallowMount} from "@vue/test-utils";
import {User} from "@/Api";
import userState from "@/store/modules/user"

jest.mock('@/Api')
jest.mock('@/store/modules/user')

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
const notifications = [
    {
        "id": 0,
        "title": "Card Expiry",
        "message": "This card is about to expire",
        "created": "2/07/2021 4:34pm",
        "card": "Looking for plums"
    },
    {
        "id": 1,
        "title": "Card Expiry",
        "message": "This card is about to expire",
        "created": "1/07/2021 6:37pm",
        "card": "Apples for Oranges"
    }
]

const messages = [
    {
        "id": 1,
        "sender": {
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
            }},
        "receiver": {
            "id": 101,
            "firstName": "Jane",
            "lastName": "Doe",
            "homeAddress": {
                "streetNumber": "3/24",
                "streetName": "Ilam Road",
                "city": "Christchurch",
                "region": "Canterbury",
                "country": "New Zealand",
                "postcode": "90210"
            }},
        "text": "This is a test message",
        "card": {
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
    }
]

describe('Jest tests for the home component', () => {

    beforeEach(async () => {

        const getCardsResponse = {
            data: [...cards]
        }
        const getNotificationsResponse = {
            data: [...notifications]
        }

        const getMessagesResponse = {
            data: [...messages]
        }

        User.getCards.mockResolvedValue(getCardsResponse)
        User.getNotifications.mockResolvedValue(getNotificationsResponse)
        User.getMessages.mockResolvedValue(getMessagesResponse)

        userState.isLoggedIn.mockImplementation(jest.fn(() => {
            return true
        }))
        userState.actor.mockImplementation(jest.fn(() => {
            return {
                id: 1,
                name: 'Myrtle Tremontana',
                type: 'user'
            }
        }))
        userState.isActingAsUser.mockImplementation(jest.fn(() => {
            return true
        }))
        userState.isActingAsBusiness.mockImplementation(jest.fn(() => {
            return false
        }))
        userState.canDoAdminAction.mockImplementation(jest.fn(() => {
            return false
        }))

        wrapper = shallowMount(Home)
        await wrapper.vm.$nextTick()

    })

    afterEach(() => {
        jest.clearAllMocks()
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

    test('Can remove notifications from the notifications list',  async () => {
        expect(wrapper.vm.$data.notifications.length).toStrictEqual(2)
        wrapper.vm.removeNotification(1)
        expect(wrapper.vm.$data.notifications.length).toStrictEqual(1)
    })

    test('Show notification button toggles correctly', async () => {
        wrapper.vm.$data.notificationsShown = false
        const button = wrapper.find('#showNotificationsButton')
        await button.trigger('click')
        expect(wrapper.vm.$data.notificationsShown).toBeTruthy()
    })

    test('Being an admin results in also getting admin notifications', async () => {
        userState.canDoAdminAction.mockImplementationOnce(jest.fn(() => {
            return true
        }))
        wrapper = shallowMount(Home)
        await wrapper.vm.$nextTick()
        expect(User.getAdminNotifications).toHaveBeenCalledTimes(1)
    })

    test('Show messages button toggles correctly', async () => {
        wrapper.vm.$data.notificationsShown = true
        const button = wrapper.find('#showMessagesButton')
        await button.trigger('click')
        expect(wrapper.vm.$data.notificationsShown).toBeFalsy()
    })

    test('Can remove message from the messages list',  async () => {
        wrapper.vm.$data.messages = messages
        expect(wrapper.vm.$data.messages.length).toStrictEqual(1)
        wrapper.vm.removeMessage(1)
        expect(wrapper.vm.$data.messages.length).toStrictEqual(0)
    })

})