import '@jest/globals'
import {shallowMount} from "@vue/test-utils"
import RegisterPage from "@/components/user/RegisterPage"

let wrapper = shallowMount(RegisterPage, {
    computed: {
        isLoggedIn(){
            return false
        }
    },
    methods: {
        addUser() {

        }
    }
})

describe('validateFirstName method tests', () => {

    beforeEach(() => {
        wrapper.vm.$data.msg.firstName = null
        wrapper.vm.$data.valid = true
    })

    test("Test invalid first name", () => {
        wrapper.vm.$data.firstName = ''
        wrapper.vm.validateFirstName()
        expect(wrapper.vm.$data.msg.firstName).toStrictEqual('Please enter a first name')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test valid first name", () => {
        wrapper.vm.$data.firstName = 'John'
        wrapper.vm.validateFirstName()
        expect(wrapper.vm.$data.msg.firstName).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

})

describe('validateLastName method tests', () => {

    beforeEach(() => {
        wrapper.vm.$data.msg.lastName = null
        wrapper.vm.$data.valid = true
    })

    test("Test invalid last name", () => {
        wrapper.vm.$data.lastName = ''
        wrapper.vm.validateLastName()
        expect(wrapper.vm.$data.msg.lastName).toStrictEqual('Please enter a last name')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test valid last name", () => {
        wrapper.vm.$data.lastName = 'Smith'
        wrapper.vm.validateLastName()
        expect(wrapper.vm.$data.msg.lastName).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

})

describe('validateEmail method tests', () => {

    beforeEach(() => {
        wrapper.vm.$data.msg.email = null
        wrapper.vm.$data.valid = true
    })

    test("Test no email address", () => {
        wrapper.vm.$data.email = ''
        wrapper.vm.validateEmail()
        expect(wrapper.vm.$data.msg.email).toStrictEqual('Please enter an email address')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test invalid email address", () => {
        wrapper.vm.$data.email = 'john.com'
        wrapper.vm.validateEmail()
        expect(wrapper.vm.$data.msg.email).toStrictEqual('Invalid email address')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test("Test valid email address", () => {
        wrapper.vm.$data.email = 'john.smith@gmail.com'
        wrapper.vm.validateEmail()
        expect(wrapper.vm.$data.msg.email).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })


})

describe('validateDateOfBirth method tests', () => {

    beforeEach(() => {
        wrapper.vm.$data.msg.dateOfBirth = null
        wrapper.vm.$data.valid = true
    })

    test('Test no birth date', () => {
        wrapper.vm.$data.dateOfBirth = ''
        wrapper.vm.validateDateOfBirth()
        expect(wrapper.vm.$data.msg.dateOfBirth).toStrictEqual('Please enter a date of birth')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test future birth date', () => {
        wrapper.vm.$data.dateOfBirth = '2022-01-01'
        wrapper.vm.validateDateOfBirth()
        expect(wrapper.vm.$data.msg.dateOfBirth).toStrictEqual('Date of birth can not be in the future')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test unrealistic birth date', () => {
        wrapper.vm.$data.dateOfBirth = '1500-01-01'
        wrapper.vm.validateDateOfBirth()
        expect(wrapper.vm.$data.msg.dateOfBirth).toStrictEqual('Date of birth is unrealistic')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test valid birth date', () => {
        wrapper.vm.$data.dateOfBirth = '1995-01-01'
        wrapper.vm.validateDateOfBirth()
        expect(wrapper.vm.$data.msg.dateOfBirth).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

})

describe('validatePassword method tests', () => {

    beforeEach(() => {
        wrapper.vm.$data.msg.password = null
        wrapper.vm.$data.valid = true
    })

    test('Test no password is invalid', () => {
        wrapper.vm.$data.password = ''
        wrapper.vm.validatePassword()
        expect(wrapper.vm.$data.msg.password).toStrictEqual('Please enter a password')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test invalid password', () => {
        wrapper.vm.$data.password = 'hello'
        wrapper.vm.validatePassword()
        expect(wrapper.vm.$data.msg.password).toStrictEqual('Password does not meet the requirements')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test valid password', () => {
        wrapper.vm.$data.password = 'Password123'
        wrapper.vm.validatePassword()
        expect(wrapper.vm.$data.msg.password).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

})

describe('validatePhoneNumber method tests', () => {

    beforeEach(() => {
        wrapper.vm.$data.msg.phone = null
        wrapper.vm.$data.valid = true
    })

    test('Test no phone number', () => {
        wrapper.vm.$data.phone = ''
        wrapper.vm.validatePhoneNumber()
        expect(wrapper.vm.$data.msg.phone).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

    test('Test invalid phone number', () => {
        wrapper.vm.$data.phone = 'hello'
        wrapper.vm.validatePhoneNumber()
        expect(wrapper.vm.$data.msg.phone).toStrictEqual('Invalid phone number')
        expect(wrapper.vm.$data.valid).toBeFalsy()
    })

    test('Test valid phone number', () => {
        wrapper.vm.$data.phone = '+64 0800 823 637'
        wrapper.vm.validatePhoneNumber()
        expect(wrapper.vm.$data.msg.phone).toStrictEqual(null)
        expect(wrapper.vm.$data.valid).toBeTruthy()
    })

})

describe('submit button tests', () => {

    test("Clicking the submit button runs the validate input method", async () => {
        await wrapper.find('#createButton').trigger('click')
        expect(wrapper.vm.$data.submitClicked).toBeTruthy()
    })

})