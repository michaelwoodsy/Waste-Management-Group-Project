<template>
  <div class="text-left">

    <!--    Div for displaying full address search input box    -->
    <div class="form-row mb-3 needs-validation" v-if="fullAddressMode">
      <label for="fullAddress" class="addressText">
        <strong>Address</strong><span class="required">*</span>

        <!-- Loading spinning win -->
        <transition name="fade">
          <span class="pl-1 d-inline transition-time" v-if="loading">
            <span class="spinner-border text-secondary spinner-border-sm" role="status">
              <span class="sr-only">Loading...</span>
            </span>
          </span>
        </transition>
      </label>

      <!-- Input for full address -->
      <input id="fullAddress"
             v-model="fullAddress"
             :class="{
               'form-control': true,
               'w-100': true,
               'is-valid': locationSelected,
               'is-invalid': showErrors && !locationSelected
             }"
             @keyup="addressEntered"
             @keyup.tab="dropdown"
             @click="dropdown"
             maxlength="250"
             data-toggle="dropdown"
             placeholder="Search for your address"
             type="text"
      >

      <!--    Address suggestions    -->
      <div class="dropdown-menu overflow-auto" id="dropdown">

        <!-- Error text -->
        <p class="text-danger left-padding dropdown-item mb-0 alert-danger" v-if="error">
          {{ error }}
        </p>

        <!-- No Results text -->
        <p class="text-muted dropdown-item left-padding mb-0 disabled"
           v-else-if="suggestions.length === 0 || (this.fullAddress && this.fullAddress.length < 3)"
        >
          No results found.
        </p>

        <!-- Location Results text -->
        <a class="dropdown-item pointer left-padding"
           v-bind:key="location.id"
           v-for="location in suggestions"
           v-else
           @click="selectLocation(location)"
        >
          {{ getAddressString(location) }}
        </a>

        <!-- Enter address manual text -->
        <div class="dropdown-divider"/>
        <p class="dropdown-text-item text-muted left-padding mb-0 pr-3">
          <small>
            Can't find your address?
            <a class="text-primary pointer" @click="swapInputMode">Enter manually</a> instead.
          </small>
        </p>
      </div>

      <!-- Error Message -->
      <span v-if="fullAddress" class="invalid-feedback">An address must be selected</span>
      <span v-else class="invalid-feedback">An address is required</span>

    </div>

    <!--    Div for displaying individual fields for address    -->
    <div v-else>
      <div class="form-row">
        <h3>Address</h3>
      </div>

      <div class="form-row mb-3">
        <a @click="swapInputMode" class="text-primary pointer">Search for address instead</a>
      </div>

      <div class="form-row mb-3">
        <!--    Home Address Street Number    -->
        <label for="addressNumber"><strong>Street Number</strong></label>
        <input id="addressNumber"
               v-model="address.streetNumber"
               class="form-control"
               maxlength="20"
               placeholder="Enter your Street Number" style="width:100%" type="text">
      </div>

      <div class="form-row mb-3">
        <!--    Home Address Street Name    -->
        <label for="addressStreet"><strong>Street Name</strong></label><br/>
        <input id="addressStreet" v-model="address.streetName" class="form-control" placeholder="Enter your Street Name"
               style="width:100%" type="text"><br>

        <!--    Error message for street name input   -->
        <span v-if="msg.streetName && showErrors" class="text-danger">{{ msg.streetName }}</span>
      </div>

      <div class="form-row mb-3">
        <!--    Home Address City    -->
        <label for="addressCity"><strong>City or Town</strong></label>
        <input id="addressCity" v-model="address.city" class="form-control" maxlength="50"
               placeholder="Enter your City" style="width:100%" type="search">

      </div>

      <div class="form-row mb-3">
        <!--    Home Address Region    -->
        <label for="addressRegion"><strong>Region</strong></label>
        <input id="addressRegion" v-model="address.region" class="form-control" maxlength="50"
               placeholder="Enter your Region" style="width:100%" type="search">
      </div>

      <div class="form-row mb-3">
        <!--    Home Address Country    -->
        <label for="addressCountry"><strong>Country<span class="required">*</span></strong></label>
        <input id="addressCountry" v-model="address.country"
               :class="{'form-control': true, 'is-invalid': msg.country && showErrors}" maxlength="30"
               placeholder="Enter your Country" required style="width:100%" type="search">
        <!--    Error message for the country input    -->
        <span class="invalid-feedback">{{ msg.country }}</span>

      </div>

      <div class="form-row mb-3">
        <!--    Home Address Post Code    -->
        <label for="addressPostCode"><strong>Postcode</strong></label>
        <input id="addressPostCode" v-model="address.postcode" class="form-control"
               maxlength="30"
               placeholder="Enter your Postcode" style="width:100%" type="text">
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: "AddressInputFields",
  data () {
    return {
      fullAddress: null,
      fullAddressMode: true,
      cancelToken: null,
      suggestions: [],
      locationSelected: null,
      address: {  // Address object for manual fields
        streetNumber: '',
        streetName: '',
        city: '',
        region: '',
        country: '',
        postcode: '',
      },
      msg: {
        country: null,
        streetName: null
      },
      error: null,
      loading: false,
      latitude: null,
      longitude: null,
      timedAddressRequest: null,
      typeWaitTime: 200  // time to wait in ms after the user has typed before making api request
    }
  },
  props: ['showErrors'],
  computed: {
    valid() {
      return !(this.msg.country || this.msg.streetName)
    }
  },
  mounted() {
    this.estimateLocation()
  },
  watch: {
    /** Watcher for the manual address **/
    address: {
      async handler() {
        await this.validateAddress()
        this.emitAddress()
      },
      deep: true
    },
    locationSelected: {
      async handler() {
        await this.validateAddress()
        this.emitAddress()
      },
      deep: true
    },
    /** Validate address when show errors is set **/
    showErrors: {
      async handler() {
        await this.validateAddress()
        this.emitAddress()
      },
      deep: true
    },
  },
  methods: {
    /**
     * Method to run when the full address has been entered
     */
    async addressEntered() {
      // Clear previously selected address
      this.locationSelected = null

      // Check more than 2 characters are entered
      if (!this.fullAddress || this.fullAddress.length < 3) {
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
          await this.updateSuggestions(this.fullAddress)
        }, this.typeWaitTime)

      } else {
        await this.updateSuggestions(this.fullAddress)
      }
    },

    /**
     * Makes a request to the photon address api.
     *
     * @param text The text entered into the autofill-able text field
     * @param limit Limit for the number of results to get from the request. Default 10.
     * @return Axios Promise
     */
    getPhotonAddress(text, limit=5) {
      // Cancel previous request if there is one
      if (this.cancelToken) {
        this.cancelToken("A new request was made")
      }

      // Set the url for the location api
      let url = `https://photon.komoot.io/api?q=${text}&limit=${limit}&osm_tag=place`
      if (this.longitude) {
        url += `&lat=${this.latitude}&lon=${this.longitude}`
      }

      // Make and return request promise
      return axios.get(url, {
            cancelToken: new axios.CancelToken((c) => {
              this.cancelToken = c;
            })
      })
    },

    /**
     * Generates a string representation of a location.
     *
     * @param location Location object to be sent to backend.
     * @returns {string} Representation of the location.
     */
    getAddressString(location) {
      let stringRep = ''

      // Add the street number and name
      if (location.streetNumber && location.streetName) {
        stringRep = `${location.streetNumber} ${location.streetName}`
      }

      // Add the city
      if (location.city) {
        stringRep += stringRep === '' ? '' : ', '
        stringRep += location.city + (location.postcode ? ` ${location.postcode}` : '')
      }

      // Add the postcode
      if (location.region) {
        stringRep += stringRep === '' ? '' : ', '
        stringRep += location.region
      }

      // Add the postcode
      if (location.country) {
        stringRep += stringRep === '' ? '' : ', '
        stringRep += location.country
      }

      return stringRep
    },

    /**
     * Selects a location from the suggestions list.
     *
     * @param location Location object to be sent to api
     */
    selectLocation(location) {
      this.locationSelected = location
      this.fullAddress = this.getAddressString(location)
      this.emitAddress()
    },

    /**
     * Checks if a location object is valid
     *
     * @param location Object to be sent to backend
     * @returns Object Messages object with any messages for things needing fixed
     */
    async getAddressFixes(location) {
      //If using full address, only need to check that it has been filled
      if (this.locationSelected !== null && this.fullAddressMode) {
        return {}
      }

      let fixMessages = {}

      // Check country
      if (location.country === '') {
        fixMessages['country'] = 'Please enter a country'
      } else {
        fixMessages['country'] = null
      }

      // Check street number
      if (location.streetNumber !== '' && location.streetName === '') {
        fixMessages['streetName'] = 'Please enter a Street Name'
      } else {
        fixMessages['streetName'] = ''
      }

      return fixMessages
    },

    /**
     * Checks if a location object is valid
     *
     * @param location Object to be sent to backend
     * @returns {boolean} True if the location is valid
     */
    async addressIsValid(location) {
      const msg = await this.getAddressFixes(location)
      return !(msg.country || msg.streetName);
    },

    /** Changes input mode between manual and search. */
    swapInputMode() {
      if (this.fullAddressMode) {
        this.fullAddressMode = false
        this.fullAddress = null
        this.locationSelected = null
        this.suggestions = []
      } else {
        this.fullAddressMode = true
      }
    },

    /** Emits the current address and validity, sending it to the higher component **/
    emitAddress() {
      let address;
      if (this.fullAddressMode) {
        address = this.locationSelected
      } else {
        address = this.address
      }

      // Delete the ID if it exists
      if (address && address.id) {
        delete address.id
      }

      this.$emit('setAddress', address)
      this.$emit('setAddressValid', this.valid)
    },

    /**
     * Checks if the country of the address is real using the rest api
     * Only runs if the user is in manual address mode.
     * Returns a bool.
     */
    async checkAddressCountry() {
      // Get address to check
      let address;
      if (this.fullAddressMode) {
        address = this.locationSelected
      } else {
        address = this.address
      }

      // Check country code with currency api
      if (!this.fullAddressMode) {
        // Check if country is valid and has a currency (for products and inventory items price)
        try {
          await this.$root.$data.product.getCurrency(address.country)
          this.msg['country'] = null
        } catch (e) {
          this.msg['country'] = 'Please enter a valid Country'
        }
      }

      return !this.msg.country
    },

    /**
     * Validates the address variables
     * Checks if the variables are empty, if so displays a warning message
     */
    async validateAddress() {
      this.msg = await this.getAddressFixes(this.address)
    },

    /** Makes sure the suggestions dropdown is down **/
    dropdown() {
      // check if the drop down is already shown
      let dropDownEl = document.getElementById('dropdown')
      let dropDownShown = dropDownEl.classList.contains('show')

      // open the drop down if it's not shown already
      if (dropDownShown) {
        let toggleBtn = document.getElementById('fullAddress')
        toggleBtn.click();
      }
    },

    /**
     * Estimate the users latitude and longitude coordinates using there IP.
     * Sets this.latitude and this.longitude
     */
    async estimateLocation() {
      // get the users IP
      let ipRes = await axios.get('https://api.ipify.org/?format=json')
      let ip = ipRes.data.ip

      // get estimated location based on ip
      let locationRes = await axios.get(`https://freegeoip.app/json/${ip}`)
      this.latitude = locationRes.data.latitude
      this.longitude = locationRes.data.longitude

      // updated address suggestions
      await this.addressEntered();
    },

    /**
     * Maps a location json object from photon api, to the json address format required by the backend api
     *
     * @param location Location json object from photon api.
     * @returns {{country, streetName: string, streetNumber: string, city: (string), postcode, id: number, region}}
     */
    mapToAddress(location) {
      return {
        id: location.properties.osm_id,
        streetNumber: location.properties.housenumber,
        streetName: location.properties.street,
        city: location.properties.city || location.properties.district || location.properties.county,
        region: location.properties.state,
        country: location.properties.country,
        postcode: location.properties.postcode
      }
    },

    /**
     * Creates a request to photon api, parses results and updates the suggestion list accordingly
     *
     * @param inputAddress String, current address to search for.
     * @returns {Promise<void>} Promise for this request.
     */
    async updateSuggestions(inputAddress) {
      try {
        this.loading = true
        let res = await this.getPhotonAddress(inputAddress)
        this.cancelToken = null

        // Reset relevant variables
        this.suggestions = []
        this.loading = false
        this.error = null

        // Iterate through locations and create address objects
        for (const location of res.data.features) {
          const address = this.mapToAddress(location)
          if (await this.addressIsValid(address)) {
            this.suggestions.push(address)
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
  }
}
</script>

<style scoped>
.left-padding {
  padding-left: 12px;
}

.required {
  color: red;
  display: inline;
}

/* Needed for transition animation of loading wheel */
.fade-enter-active, .fade-leave-active {
  transition: opacity .5s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
}
</style>