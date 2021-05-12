import "@jest/globals";
import UserProfileLinks from '@/components/UserProfileLinks';

const VueTestUtils = require('@vue/test-utils')

let wrapper;

// Setup before each test
beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(UserProfileLinks, {
        stubs: ['router-link', 'router-view'],
        computed: {
            userProfileRoute() {
                return `users/1`;
            },
            actor() {
                return "Tom"
            },
            actorName() {
                return "Tom"
            },
            userAccounts() {
                return [{"firstName": "Tom", "lastName": "Rizzi", "id": 1}]
            },
            businessAccounts() {
                // Returns a list of fake business accounts for the time being
                return []
            }
        }
    })
});

afterEach(() => {
    wrapper.destroy();
});


// Actual tests
describe('UserProfileLinks', () => {

    // Sanity check
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    // Check the current actor name is displayed
    test('displayed actor name is correct', () => {
        expect(wrapper.find('div.nav-link').text()).toBe('Tom')
    });

    // Check clicking on a different actor will update the actor
    test('clicking an actor updates the user store', () => {
        window.open = jest.fn(); // global function mock to stop actual from executing
        wrapper.vm.actAsUser = jest.fn();
        const btn = '.dropdown-item';
        wrapper.find(btn).trigger('click');
        expect(wrapper.vm.actAsUser).toBeCalled();
    });

    // Check clicking logout calls logout method
    test('clicking logout updates the user store', () => {
        window.open = jest.fn(); // global function mock to stop actual from executing
        wrapper.vm.logOut = jest.fn();
        // Find the logout button
        wrapper.findAll('.dropdown-item').filter(node => node.text().match(/Logout/)).at(0).trigger("click")
        expect(wrapper.vm.logOut).toBeCalled();
    });
})

