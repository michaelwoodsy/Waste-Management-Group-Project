/**
 * Module for testing the ProfilePage.vue component
 */

import '@jest/globals'
import {shallowMount} from "@vue/test-utils";
import ProfilePage from "@/components/ProfilePage";
import {User} from '@/Api'

let wrapper
jest.mock('@/Api')

/**
 * Initial setup of tests
 */
beforeAll(() => {
    const userDataResponse = {
        data: {
            id: 100,
            firstName: 'John',
            lastName: 'Smith',
            middleName: '',
            nickname: '',
            bio: '',
            email: 'john.smith@gmail.com',
            dateOfBirth: '1995-06-20',
            phoneNumber: '',
            homeAddress: {
                streetNumber: '',
                streetName: '',
                city: '',
                region: '',
                country: 'New Zealand',
                postcode: ''
            },
            created: '2020-07-14T14:32:00Z',
            role: 'defaultGlobalApplicationAdmin',
            businessesAdministered: []
        }
    }

    User.getUserData.mockResolvedValue(userDataResponse)

    wrapper = shallowMount(ProfilePage, {
        data() {
            return {
                stateRole: 'defaultGlobalApplicationAdmin'
            }
        },
        computed: {
            userId() {
                return 1
            },
            isLoggedIn() {
                return true
            },
            isViewingSelf() {
                return true;
            },
            userRole: {
                get() {
                    return this.stateRole
                },
                set(val) {
                    this.stateRole = val
                }
            }

        },
        methods: {
            profile(response) {
                this.firstName = response.data.firstName
                this.middleName = response.data.middleName
                this.lastName = response.data.lastName
                this.nickName = response.data.nickname
                this.bio = response.data.bio
                this.email = response.data.email
                this.homeAddress = null
                this.dateJoined = response.data.created
                this.timeCalculator(Date.parse(this.dateJoined))
                this.dateJoined = this.dateJoined.substring(0, 10)
                this.businessesAdministered = response.data.businessesAdministered
                this.setPrimaryAdminList()
            }
        }
    })
})

/**
 * Tests the addUserAdmin method
 */
describe('testAddUserAdminMethod', () => {

    beforeEach(() => {
        wrapper.vm.$data.error = null
        wrapper.vm.userRole = 'defaultGlobalApplicationAdmin'
    })

    /**
     * Tests adding a user as admin when current user is DGAA
     */
    test('testAddUserAdminWithDGAA', () => {
        const response = {}
        User.makeAdmin.mockResolvedValue(response)
        wrapper.vm.addUserAdmin(2)
        expect(wrapper.vm.$data.error).toStrictEqual(null)
    })

    /**
     * Tests adding a user as admin when current user is DGAA by clicking button
     */
    test('testAddUserAdminWithDGAAWithButton', async () => {
        const response = {}
        User.makeAdmin.mockResolvedValue(response)
        let AddGAAButton = wrapper.find("#addGAAButton")
        AddGAAButton.trigger("click")
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.error).toStrictEqual(null)
    })

    /**
     * Tests adding a user as admin when current user is GAA
     */
    test('testAddUserAdminWithGAA', () => {
        const response = {}
        User.makeAdmin.mockResolvedValue(response)
        wrapper.vm.userRole = 'globalApplicationAdmin'
        wrapper.vm.addUserAdmin(2)
        expect(wrapper.vm.$data.error).toStrictEqual('You must be a global admin to grant admin rights to a user')
    })

    /**
     * Tests adding a user as admin when current user is GAA by clicking button
     */
    test('testAddUserAdminWithGAAWithButton', async () => {
        const response = {}
        User.makeAdmin.mockResolvedValue(response)
        wrapper.vm.userRole = 'globalApplicationAdmin'
        let AddGAAButton = wrapper.find("#addGAAButton")
        AddGAAButton.trigger("click")
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.error).toStrictEqual('You must be a global admin to grant admin rights to a user')
    })

    /**
     * Tests adding a user as admin when current user is not an admin
     */
    test('testAddUserAdminWithUser', () => {
        const response = {}
        User.makeAdmin.mockResolvedValue(response)
        wrapper.vm.userRole = 'user'
        wrapper.vm.addUserAdmin(2)
        expect(wrapper.vm.$data.error).toStrictEqual('You must be a global admin to grant admin rights to a user')
    })

    /**
     * Tests adding a user as admin when current user is not an admin by clicking button
     */
    test('testAddUserAdminWithUserWithButton', async () => {
        const response = {}
        User.makeAdmin.mockResolvedValue(response)
        wrapper.vm.userRole = 'user'
        let AddGAAButton = wrapper.find("#addGAAButton")
        AddGAAButton.trigger("click")
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.error).toStrictEqual('You must be a global admin to grant admin rights to a user')
    })

})

/**
 * Tests the removeUserAdmin method
 */
describe('testRemoveUserAdminMethod', () => {

    beforeEach(() => {
        wrapper.vm.$data.error = null
        wrapper.vm.userRole = 'defaultGlobalApplicationAdmin'
    })

    /**
     * Tests removing a user as admin when current user is DGAA
     */
    test('testRemoveUserAdminWithDGAA', () => {
        const response = {}
        User.makeAdmin.mockResolvedValue(response)
        wrapper.vm.removeUserAdmin(2)
        expect(wrapper.vm.$data.error).toStrictEqual(null)
    })

    /**
     * Tests removing a user as admin when current user is GAA
     */
    test('testRemoveUserAdminWithGAA', () => {
        const response = {}
        User.makeAdmin.mockResolvedValue(response)
        wrapper.vm.userRole = 'globalApplicationAdmin'
        wrapper.vm.removeUserAdmin(2)
        expect(wrapper.vm.$data.error).toStrictEqual('You must be a global admin to revoke admin rights from a user')
    })

    /**
     * Tests removing a user as admin when current user is not an admin
     */
    test('testRemoveUserAdminWithUser', () => {
        const response = {}
        User.makeAdmin.mockResolvedValue(response)
        wrapper.vm.userRole = 'user'
        wrapper.vm.removeUserAdmin(2)
        expect(wrapper.vm.$data.error).toStrictEqual('You must be a global admin to revoke admin rights from a user')
    })

})