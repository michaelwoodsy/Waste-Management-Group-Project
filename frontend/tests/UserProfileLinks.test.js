require('jsdom-global')()

const assert = require('assert')
const VueTestUtils = require('@vue/test-utils')
import UserProfileLinks from '@/components/UserProfileLinks';

let wrapper;

beforeEach(() => {
    wrapper = VueTestUtils.shallowMount(UserProfileLinks, {
        stubs: ['router-link', 'router-view'],
        computed: {
            userProfileRoute () {
                return `users/1`;
            },
            actor () {
                return "Tom"
            },
            actorName () {
                return "Tom"
            },
            userAccounts () {
                return []
            },
            businessAccounts () {
                // Returns a list of fake business accounts for the time being
                return [
                ]
            }
        }
    })
});

afterEach(() => {
    wrapper.destroy();
});

describe('UserProfileLinks', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Test', () => {
        assert.strictEqual('Tom\n' +
            '     My Profile Logout', wrapper.text())
    });
})

