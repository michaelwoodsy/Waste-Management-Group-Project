require('./setup.js')
import { shallowMount } from '@vue/test-utils';
import UserProfileLinks from '../components/UserProfileLinks';

let wrapper;

beforeEach(() => {
    wrapper = shallowMount(UserProfileLinks, {
        propsData: {},
        mocks: {},
        stubs: {},
        methods: {},
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('Component', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });
});