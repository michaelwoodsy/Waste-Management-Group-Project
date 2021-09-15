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




    <div class="row">
      <div id="popularListingCarousel" class="carousel slide w-100" data-ride="carousel">
        <div class="carousel-inner">
          <div class="carousel-item active">
            <div class="row">
              <div v-for="listing in listingsList1" v-bind:key="listing.id" class="col">
                <PopularListing :data="listing" @update-data="updateData" style="padding-left: 5px; padding-right: 5px"></PopularListing>
              </div>
            </div>
          </div>
          <div class="carousel-item">
            <div class="row">
              <div v-for="listing in listingsList2" v-bind:key="listing.id" class="col">
                <PopularListing :data="listing" @update-data="updateData" style="padding-left: 5px; padding-right: 5px"></PopularListing>
              </div>
            </div>
          </div>
          <div class="carousel-item">
            <div class="row">
              <div v-for="listing in listingsList3" v-bind:key="listing.id" class="col">
                <PopularListing :data="listing" @update-data="updateData" style="padding-left: 5px; padding-right: 5px"></PopularListing>
              </div>
            </div>
          </div>

        </div>
        <a class="carousel-control-prev" data-slide="prev" href="#popularListingCarousel" role="button">
          <span aria-hidden="true" class="carousel-control-prev-icon"></span>
          <span class="sr-only">Previous</span>
        </a>
        <a class="carousel-control-next" data-slide="next" href="#popularListingCarousel" role="button">
          <span aria-hidden="true" class="carousel-control-next-icon"></span>
          <span class="sr-only">Next</span>
        </a>
      </div>
    </div>
  </div>
</template>

<script>
import {Landing} from "@/Api";
import userState from "@/store/modules/user"
import PopularListing from "../sale-listing/PopularListing";
import axios from "axios";

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
      listingsList1: [],
      listingsList2: [],
      listingsList3: [],
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
      try {
        let response = null
        if (country === undefined)  {
          response = await Landing.getPopularListings()
        } else {
          response = await Landing.getPopularListings(this.countryInput)
        }
        this.listings = []
        for (let listing of response.data) {
          listing.currency = await this.$root.$data.product.getCurrency(listing.business.address.country)
          this.listings.push(listing)
        }
      } catch (error) {
        console.error(error)
        this.error = error
      }
      this.listingsList1 = this.listings.slice(0,4)
      this.listingsList2 = this.listings.slice(4,8)
      this.listingsList3 = this.listings.slice(7)
      this.listingsList3.push(this.listings[0])
    },


    async updateData() {
      await this.user.updateData()
    },

    /**
     * Makes a request to the photon address api.
     *
     * @param text The text entered into the autofill-able text field
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
     * Method to run when the full address has been entered
     */
    async countryEntered() {
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

    async selectCountry(country) {
      this.countryInput = country
      await this.getPopularListings(country)
    }
  }
}
</script>

<style scoped>

.country-input {
  margin: 20px;
}

</style>