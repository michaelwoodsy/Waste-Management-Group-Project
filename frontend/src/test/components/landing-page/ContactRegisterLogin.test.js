import ContactRegisterLogin from "@/components/landing-page/ContactRegisterLogin";

const VueTestUtils = require('@vue/test-utils')

let wrapper;

beforeEach( () => {
    wrapper = VueTestUtils.shallowMount(ContactRegisterLogin, {
        stubs: ['alert']
    })
})

describe( "Tests for the Contact Us Component", () => {

    test("Test that the contact us modal appears", async ()=> {
        wrapper.vm.$data.contactUsModal = true
        await wrapper.vm.$nextTick()
        expect(wrapper.find('#contactUsModal').exists()).toBeTruthy();

    })

    test("Test that the sent email modal appears", async ()=> {
        wrapper.vm.$data.emailSent = true
        await wrapper.vm.$nextTick()
        expect(wrapper.find('#viewMessageSentModal').exists()).toBeTruthy();

    })


    test("Test an empty email address for contact us modal", () => {
        wrapper.vm.$data.email = ""
        wrapper.vm.validateEmail()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.email).toStrictEqual("Please enter an email address")
    })

    test("Test an invalid email for contact us modal", ()=>{
        wrapper.vm.$data.email = "invalid.email"
        wrapper.vm.validateEmail()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.email).toEqual("Invalid email address")
    })

    test("Test a valid email for contact us modal", () => {
        wrapper.vm.$data.email = "myrtle.t@gmail.com"
        wrapper.vm.validateEmail()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.email).toBeNull()
    })

    test("Test an empty message for contact us modal", () => {
        wrapper.vm.$data.message = ""
        wrapper.vm.validateMessage()
        expect(wrapper.vm.$data.valid).toBeFalsy()
        expect(wrapper.vm.$data.msg.message).toStrictEqual("Please enter a message")
    })

    test("Test a valid message for contact us modal", () => {
        wrapper.vm.$data.message = "What does re:sale do?"
        wrapper.vm.validateMessage()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.message).toBeNull()
    })

    test("Test that the reset method works", ()=>{
        wrapper.vm.$data.email = "myrtle.t@gmail.com"
        wrapper.vm.$data.message = "What does re:sale do?"
        wrapper.vm.$data.submitting = true
        wrapper.vm.$data.valid = false
        wrapper.vm.$data.contactUsModal = true
        wrapper.vm.$data.msg.email = "Invalid email address"
        wrapper.vm.$data.msg.message = "Please enter a message"
        wrapper.vm.$data.msg.errorChecks = "Please fix the shown errors and try again"
        wrapper.vm.resetContactUsModal()
        expect(wrapper.vm.$data.email).toStrictEqual("")
        expect(wrapper.vm.$data.message).toStrictEqual("")
        expect(wrapper.vm.$data.submitting).toBeFalsy()
        expect(wrapper.vm.$data.contactUsModal).toBeFalsy()
        expect(wrapper.vm.$data.valid).toBeTruthy()
        expect(wrapper.vm.$data.msg.email).toBeNull()
        expect(wrapper.vm.$data.msg.message).toBeNull()
        expect(wrapper.vm.$data.msg.errorChecks).toBeNull()
    })

    test("Test that reset contact us modal method closes the contact us modal", async ()=> {
        wrapper.vm.$data.contactUsModal = true
        await wrapper.vm.$nextTick()
        wrapper.vm.resetContactUsModal()
        expect(wrapper.vm.$data.contactUsModal).toBeFalsy();
    })
})