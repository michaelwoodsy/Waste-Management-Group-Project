import Marketplace from "@/components/Marketplace";
import {shallowMount} from "@vue/test-utils";

let wrapper

describe("Clicking on the marketplace tab links", () => {

    beforeEach(async () => {
        wrapper = await shallowMount(Marketplace, {
            computed: {
                isLoggedIn() {
                    return true
                }
            }
        })
        wrapper.vm.$data.error = ""
        wrapper.vm.$data.isLoggedIn = true
    });


    /**
     * Sanity Check
     */
    test("Is Vue instance", () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    /**
     * Checks that when the for sale tab is clicked,
     * checks the changePage method runs and changes the active page variable to 'for sale'
     */
    test("Clicking for sale tab link changes content to for sale content", async () => {
        let wantedElement = wrapper.find("#for-sale-link")
        wantedElement.trigger("click")
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.tabSelected).toStrictEqual("ForSale");
    });

    /**
     * Checks that when the wanted tab is clicked,
     * checks the changePage method runs and changes the active page variable to 'wanted'
     */
    test("Clicking wanted tab link changes content to wanted content", async () => {
        let wantedElement = wrapper.find("#wanted-link")
        wantedElement.trigger("click")
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.tabSelected).toStrictEqual("Wanted");
    });

    /**
     * Checks that when the exchange tab is clicked,
     * checks the changePage method runs and changes the active page variable to 'exchange'
     */
    test("Clicking exchange tab link changes content to exchange content", async () => {
        let wantedElement = wrapper.find("#exchange-link")
        wantedElement.trigger("click")
        await wrapper.vm.$nextTick()
        expect(wrapper.vm.$data.tabSelected).toStrictEqual("Exchange");
    });
})


