<template>
  <div class="col text-center">
    <h2>Popular Listings</h2>


    <div class="row d-flex justify-content-center country-input">
      <div class="d-flex align-items-center" style="padding-right: 10px">
        Filter by Country:
      </div>
      <input id="countryInput" v-model="countryInput"
             class="form-control"
             maxlength="30"
             placeholder="Enter a Country"
             @keyup="countryEntered"
             @keyup.tab="dropdown"
             @click="dropdown"
             data-toggle="dropdown"
             autocomplete="on"
             type="text"
             style="width: 40%"
      >
      <!--    Address suggestions    -->
      <div class="dropdown-menu" id="dropdown">

        <!-- Error text -->
        <p class="text-danger left-padding dropdown-item mb-0 alert-danger" v-if="error">
          {{ error }}
        </p>

        <!-- Loading spinning wheel -->
        <p v-else-if="loading" class="dropdown-item left-padding"
        >Loading... <span class="spinner-border spinner-border-sm"></span></p>

        <!-- No Results text -->
        <p class="text-muted dropdown-item left-padding mb-0 disabled"
           v-else-if="suggestions.length === 0">
          No results found.
        </p>

        <!-- Location Results text -->
        <a class="dropdown-item pointer left-padding"
           v-bind:key="country"
           v-for="country in suggestions"
           v-else
           @click="selectCountry(country)"
        >
          {{ country }}
        </a>
      </div>
    </div>

    <div v-if="country !== ''" style="padding-bottom: 20px">
      <button class="btn btn-primary no-outline" type="button" @click="clearFilter">Clear Filter</button>
    </div>

    <div class="">
      <div v-if="listings.length === 0">
        <p class="text-center"><strong>There are no popular sale listings for {{ country }}.</strong></p>
      </div>
      <div id="popularListingCarousel" class="carousel slide w-auto" data-interval="false">
        <div class="carousel-inner">
          <!-- Carousel Slides -->
          <div class="carousel-item active" v-if="listingsList1">
            <div class="row justify-content-center">
                <div v-for="listing in listingsList1" v-bind:key="listing.id">
                  <PopularListing :data="listing" style="padding-right: 20px;padding-left: 20px" @update-data="updateData"></PopularListing>
                </div>
            </div>
          </div>
          <div class="carousel-item" v-if="listingsList2">
            <div class="row justify-content-center">
              <div v-for="listing in listingsList2" v-bind:key="listing.id">
                <PopularListing :data="listing" style="padding-right: 20px;padding-left: 20px" @update-data="updateData"></PopularListing>
              </div>
            </div>
          </div>
          <div class="carousel-item" v-if="listingsList3">
            <div class="row justify-content-center">
              <div v-for="listing in listingsList3" v-bind:key="listing.id">
                <PopularListing :data="listing" style="padding-right: 20px;padding-left: 20px" @update-data="updateData"></PopularListing>
              </div>
            </div>
          </div>
        </div>
        <!-- Carousel Buttons -->
        <div v-if="listingsList2">
          <a class="carousel-control-prev" data-slide="prev" href="#popularListingCarousel" role="button" @click="currentSlide--">
            <span aria-hidden="true" class="carousel-control-prev-icon"></span>
            <span class="sr-only">Previous</span>
          </a>
          <a class="carousel-control-next" data-slide="next" href="#popularListingCarousel" role="button" @click="currentSlide++">
            <span aria-hidden="true" class="carousel-control-next-icon"></span>
            <span class="sr-only">Next</span>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {Landing} from "@/Api";
import userState from "@/store/modules/user"
import PopularListing from "../sale-listing/PopularListing";
import axios from "axios";
import $ from "jquery";

export default {
  name: "PopularListings",
  components: {
    PopularListing
  },
  async mounted() {
    await this.getPopularListings()
  },
  data() {
    return {
      user: userState,
      listings: [],
      listingsList1: null,
      listingsList2: null,
      listingsList3: null,
      currentSlide: 1,
      country: "",
      error: "",
      countryInput: "",
      suggestions: [],
      cancelToken: null,
      loading: false,
      timedAddressRequest: null,
      typeWaitTime: 200,  // time to wait in ms after the user has typed before making api request
      msg: {
        countryInput: null
      }
    }
  },
  methods: {
    /**
     * Gets the popular listings from the backend, as well as setting each sale listing's currency
     */
    async getPopularListings(country){
      this.listings = []
      this.listingsList1 = null;
      this.listingsList2 = null;
      this.listingsList3 = null;
      let response = null
      if (country === undefined)  {
        response = await Landing.getPopularListings()
      } else {
        response = await Landing.getPopularListings(this.countryInput)
      }
      this.listings = await this.$root.$data.product.addSaleListingCurrencies(response.data)
      if(this.listings.length > 6){
        //7-9 listings
        console.log(this.currentSlide)
        this.listingsList1 = this.listings.slice(0,3)
        this.listingsList2 = this.listings.slice(3,6)
        this.listingsList3 = this.listings.slice(6)
      } else if(this.listings.length > 3){
        //4-6 liked listings
        this.listingsList1 = this.listings.slice(0,3)
        this.listingsList2 = this.listings.slice(3)
      }else {
        //1-3
        this.listingsList1 = this.listings.slice(0)
      }
    },

    async updateData() {
      await this.getPopularListings()
      $(".carousel").carousel(this.currentSlide-1);
    },

    /**
     * Makes a request to the photon address api.
     *
     * @param text The country entered into the autofill-able text field
     * @param limit Limit for the number of results to get from the request. Default 10.
     * @return Axios Promise
     */
    getPhotonCountry(text, limit=10) {
      // Cancel previous request if there is one
      if (this.cancelToken) {
        this.cancelToken("A new request was made")
      }

      // Set the url for the location api
      let url = `https://photon.komoot.io/api?q=${text}&limit=${limit}&osm_tag=:country`

      // Make and return request promise
      return axios.get(url, {
        cancelToken: new axios.CancelToken((c) => {
          this.cancelToken = c;
        })
      })
    },

    /**
     * Creates a request to photon api, updates the suggestions
     *
     * @param inputCountry String, current address to search for.
     * @returns {Promise<void>} Promise for this request.
     */
    async updateSuggestions(inputCountry) {
      try {
        this.loading = true
        let res = await this.getPhotonCountry(inputCountry)
        this.cancelToken = null

        // Reset relevant variables
        this.suggestions = []
        this.loading = false
        this.error = null

        // Iterate through countries
        for (const location of res.data.features) {
          const country = location.properties.country
          //Remove duplicates
          if (!this.suggestions.includes(country)) {
            this.suggestions.push(country)
          }
        }
      }
      catch(err) {
        // Check if the error is from the request canceling
        if (err.message !== "A new request was made") {
          this.error = err
          this.loading = false
        }
      }
    },

    /**
     * Method to run when a country has been entered, updates suggestions
     */
    async countryEntered() {

      //Opens dropdown when searching
      let dropDownEl = document.getElementById('dropdown')
      let dropDownShown = dropDownEl.classList.contains('show')
      // open the drop down if it's not shown already
      if (!dropDownShown) {
        let toggleBtn = document.getElementById('countryInput')
        toggleBtn.click();
      }

      if (!this.countryInput) {
        this.suggestions = []
        return
      }

      if (this.timedAddressRequest || this.cancelToken) {
        // A request was scheduled to run within 200ms, but hasn't yet so the user must still be typing.
        // Cancel the request.
        clearTimeout(this.timedAddressRequest)

        // Schedule another request to run later
        this.timedAddressRequest = setTimeout(async () => {
          this.timedAddressRequest = null
          await this.updateSuggestions(this.countryInput)
        }, this.typeWaitTime)

      } else {
        await this.updateSuggestions(this.countryInput)
      }
    },

    /**
     * Makes sure the suggestions dropdown is down.
     *
     * @param event Mouse or KeyboardEvent
     */
    dropdown(event) {
      // check if the drop down is already shown
      let dropDownEl = document.getElementById('dropdown')
      let dropDownShown = dropDownEl.classList.contains('show')

      // open the drop down if it's not shown already
      if (dropDownShown || (event instanceof KeyboardEvent && !dropDownShown)) {
        let toggleBtn = document.getElementById('countryInput')
        toggleBtn.click();
      }
    },

    /**
     * Sets filtered country and retrieves its popular listings
     */
    async selectCountry(country) {
      this.countryInput = country
      this.country = country
      this.suggestions = []
      //Resets the carousel to the first page
      $('.carousel').carousel(0)
      await this.getPopularListings(country)
    },

    /**
     * Clears the filtered country and searches popular listings worldwide
     */
    async clearFilter() {
      this.countryInput = ""
      this.country = ""
      //Resets the carousel to the first page
      $('.carousel').carousel(0)
      await this.getPopularListings()
    }
  }
}
</script>

<style scoped>

.country-input {
  margin: 20px;
}

</style>