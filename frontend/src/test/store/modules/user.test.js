/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import user from '@/store/modules/user'
import {getCookie, setCookie} from '@/utils/cookieJar'

// Mock the api module
jest.mock('@/Api', () => ({
    'User': {
        getUserData(id) {
            return new Promise((resolve) => {
                resolve({
                    data: {
                        "id": id,
                        "firstName": "John",
                        "lastName": "Smith",
                        "email": "johnsmith99@gmail.com",
                        "businessesAdministered": [
                            {
                                name: "Store",
                                type: "business",
                                id: 2,
                                primaryAdministratorId: 1
                            }
                        ]
                    }
                })
            })
        },

        createNew() {
            return new Promise((resolve) => {
                resolve({data: {userId: 1}})
            })
        },

        // eslint-disable-next-line no-unused-vars
        login(username, password) {
            return new Promise((resolve) => {
                resolve({data: {userId: 1}})
            })
        }
    },
}));


// Setup before each test
beforeEach(() => {
    // Reset the state
    user.state = {
        message: 'Hello!',
        loggedIn: false,
        userId: null,
        userData: {
            role: "user"
        },
        actingAs: {
            type: "user"
        }
    }
});

// Actual tests
describe('store.user', () => {

    // Logging in
    test('logging in', async () => {
        expect(user.state.loggedIn).toBeFalsy();
        await user.login("username", "pword")
        expect(user.state.loggedIn).toBeTruthy();
        expect(parseInt(getCookie('userId'))).toBe(1)
    });

    // Logging out
    test('logging out', async () => {
        user.state = {
            message: 'Hello!',
            loggedIn: true,
            userId: 1,
            userData: {},
            actingAs: null
        }
        expect(user.state.loggedIn).toBeTruthy();
        await user.logout()
        expect(user.state.loggedIn).toBeFalsy();
        expect(user.state.actingAs).toBe(null);
    });

    // check acting as a business sets the correct state
    test('store state set when setActingAs is run', async () => {
        // Test business
        const business = {
            name: "Store",
            type: "business",
            id: 2
        }

        // Try act as the business
        await user.login("username", "pword")
        user.setActingAs(business.id, business.name, business.type)

        // Checks the actor cookie is correctly set
        expect(user.state.actingAs.name).toBe(business.name)
        expect(user.state.actingAs.type).toBe(business.type)
        expect(user.state.actingAs.id).toBe(business.id)
    });

    // check acting as a business sets cookies
    test('actor cookie gets set when setActingAs is run', async () => {
        // Test business
        const business = {
            name: "Store",
            type: "business",
            id: 2
        }

        // Try act as the business
        await user.login("username", "pword")
        user.setActingAs(business.id, business.name, business.type)

        // Checks the actor cookie is correctly set
        const actor = JSON.parse(getCookie('actor'));
        expect(actor.name).toBe(business.name)
        expect(actor.type).toBe(business.type)
        expect(actor.id).toBe(business.id)
    });

    // Check registering
    test('registering logs the user in', async () => {
        expect(user.state.loggedIn).toBeFalsy();

        // Try to register
        await user.register("Tom", "Rizzi")

        // Checks the state is set correctly
        expect(user.state.userId).toBe(1)
        expect(user.state.loggedIn).toBeTruthy();
    });

    // Checking if user is logged in test
    test("testing checkLoggedIn function if the correct cookies are present", async () => {
        // Setup cookies
        const actor = JSON.stringify({id: 2, type: "business", name: "Shop 1"});
        setCookie("actor", actor, null);
        setCookie("userId", 1, null);

        // run checkLoggedIn function
        await user.checkLoggedIn();

        // Checks the state is set correctly
        expect(user.state.userId).toBe("1")
        expect(user.state.loggedIn).toBeTruthy();
        expect(user.state.actingAs.name).toBe("Shop 1")
        expect(user.state.actingAs.type).toBe("business")
    });


    //check that when we change acting as to business,
    // isPrimaryAdminOfBusiness returns true
    test("testing isPrimaryAdminOfBusiness method", async() => {
        // Test business
        const business = {
            name: "Store",
            type: "business",
            id: 2,
            primaryAdministratorId: 1
        }

        // Try act as the business
        await user.login("username", "pword")
        user.setActingAs(business.id, business.name, business.type)

        const isPrimaryAdmin = user.isPrimaryAdminOfBusiness()
        expect(isPrimaryAdmin).toBeTruthy();
    })

    //test isGAA method
    test("testing isGAA method", () => {
        user.state.userData.role = "globalApplicationAdmin"
        const isGAA = user.isGAA()
        expect(isGAA).toBeTruthy();
    })

    //test isDGAA method
    test("testing isDGAA method", () => {
        user.state.userData.role = "defaultGlobalApplicationAdmin"
        const isDGAA = user.isDGAA()
        expect(isDGAA).toBeTruthy();
    })

    //test canDoAdminAction method
    test("testing canDoAdminAction method", () => {
        user.state.userData.role = "globalApplicationAdmin"
        user.state.actingAs.type = "user"
        let canDoAdminAction = user.canDoAdminAction()
        expect(canDoAdminAction).toBeTruthy();

        user.state.actingAs.type = "business"
        canDoAdminAction = user.canDoAdminAction()
        expect(canDoAdminAction).toBeFalsy();

        user.state.userData.role = "defaultGlobalApplicationAdmin"
        user.state.actingAs.type = "user"
        canDoAdminAction = user.canDoAdminAction()
        expect(canDoAdminAction).toBeTruthy();

        user.state.actingAs.type = "business"
        canDoAdminAction = user.canDoAdminAction()
        expect(canDoAdminAction).toBeFalsy();
    })

})