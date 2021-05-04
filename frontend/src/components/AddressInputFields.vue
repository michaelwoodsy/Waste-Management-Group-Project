<template>
  <div class="text-left">

    <!--    Div for displaying full address search input box    -->
    <div class="form-row mb-3 needs-validation" v-if="fullAddressMode">
      <label for="fullAddress" class="addressText">
        <b>Address</b><span class="required">*</span>

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
        <label for="addressNumber"><b>Street Number</b></label>
        <input id="addressNumber"
               v-model="address.streetNumber"
               class="form-control"
               maxlength="20"
               placeholder="Enter your Street Number" style="width:100%" type="text">
      </div>

      <div class="form-row mb-3">
        <!--    Home Address Street Name    -->
        <label for="addressStreet"><b>Street Name</b></label><br/>
        <input id="addressStreet" v-model="address.streetName" class="form-control" placeholder="Enter your Street Name"
               style="width:100%" type="text"><br>

        <!--    Error message for street name input   -->
        <span v-if="msg.streetName && showErrors" class="text-danger">{{ msg.streetName }}</span>
      </div>

      <div class="form-row mb-3">
        <!--    Home Address City    -->
        <label for="addressCity"><b>City or Town</b></label>
        <input id="addressCity" v-model="address.city" class="form-control" maxlength="50"
               placeholder="Enter your City" style="width:100%" type="search">

      </div>

      <div class="form-row mb-3">
        <!--    Home Address Region    -->
        <label for="addressRegion"><b>Region</b></label>
        <input id="addressRegion" v-model="address.region" class="form-control" maxlength="50"
               placeholder="Enter your Region" style="width:100%" type="search">
      </div>

      <div class="form-row mb-3">
        <!--    Home Address Country    -->
        <label for="addressCountry"><b>Country<span class="required">*</span></b></label>
        <input id="addressCountry" v-model="address.country"
               :class="{'form-control': true, 'is-invalid': msg.country && showErrors}" maxlength="30"
               placeholder="Enter your Country" required style="width:100%" type="search">
        <!--    Error message for the country input    -->
        <span class="invalid-feedback">{{ msg.country }}</span>

      </div>

      <div class="form-row mb-3">
        <!--    Home Address Post Code    -->
        <label for="addressPostCode"><b>Postcode</b></label>
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
      loading: false
    }
  },
  props: ['showErrors'],
  computed: {
    valid() {
      return !(this.msg.country || this.msg.streetName)
    }
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
    /** Validate address when show errors is set **/
    showErrors: {
      async handler() {
        await this.validateAddress()
        this.emitAddress()
      },
      deep: true
    }
  },
  methods: {
    /**
     * Function to run when the full address has been entered
     */
    async addressEntered() {
      // Clear previously selected address
      this.locationSelected = null

      // Check more than 2 characters are entered
      if (!this.fullAddress || this.fullAddress.length < 3) {
        this.suggestions = []
        return
      }

      // Get possible addresses from photon api
      this.loading = true
      await this.getPhotonAddress(this.fullAddress)
          .then(async (res) => {
            // Reset relevant variables
            this.suggestions = []
            this.loading = false
            this.error = null

            // Iterate through locations and create address objects
            for (const location of res.data.features) {
              const address = {
                id: location.properties.osm_id,
                streetNumber: location.properties.housenumber,
                streetName: location.properties.street,
                city: location.properties.city || location.properties.district || location.properties.county,
                region: location.properties.state,
                country: location.properties.country,
                postcode: location.properties.postcode
              }
              if (await this.addressIsValid(address)) {
                this.suggestions.push(address)
              }
            }
          })
        .catch((err) => {
          // Check if the error is from the request canceling
          if (err.message !== "A new request was made") {
            this.error = err
            this.loading = false
          }
        })
    },

    /**
     * Makes a request to the photon address api.
     * @param text The text entered into the autofillable text field
     * @param limit Limit for the number of results to get from the request. Default 10.
     * @return Axios Promise
     */
    getPhotonAddress(text, limit=5) {
      // Cancel previous request if there is one
      if (this.cancelToken) {
        this.cancelToken("A new request was made")
      }

      // Make and return request promise
      return axios.get(`https://photon.komoot.io/api?q=${text}&limit=${limit}&osm_tag=place`, {
            cancelToken: new axios.CancelToken((c) => {
              this.cancelToken = c;
            })
      })
    },

    /**
     * Generates a string representation of a location.
     * @param location Location object to be sent to backend.
     * @returns {string} Representation of the location.
     */
    getAddressString(location) {
      let stringRep = ''

      // Add the street number and name
      if (location.streetNumber && location.streetNumber) {
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
     * @param location Location object to be sent to api
     */
    selectLocation(location) {
      this.locationSelected = location
      this.fullAddress = this.getAddressString(location)
      this.emitAddress()
    },

    /**
     * Checks if a location object is valid
     * @param location Object to be sent to backend
     * @returns Object Messages object with any messages for things needing fixed
     */
    async getAddressFixes(location) {
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

    /** Checks if the country of the address is real using the rest api
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
      let toggleBtn = document.getElementById('fullAddress');
      toggleBtn.click();
    }
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

.fade-enter-active, .fade-leave-active {
  transition: opacity .5s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
}
</style>