<template>
  <div>

    <!--    Div for displaying full address input box    -->
    <div class="form-row mb-3 needs-validation" v-if="fullAddressMode">
      <label for="fullAddress" class="addressText"><b>Address</b><span class="required">*</span></label>
      <input id="fullAddress"
             v-model="fullAddress"
             :class="{
               'form-control': true,
               'w-100': true,
               'is-valid': locationSelected
             }"
             @keyup="addressEntered"
             maxlength="250"
             data-toggle="dropdown"
             placeholder="Search for your address"
             type="text"
      >

      <!--    Address suggestions    -->
      <div class="dropdown-menu" id="dropdown">

        <!-- No Results text -->
        <p class="text-muted dropdown-item left-padding mb-0"
           v-if="suggestions.length === 0 || this.fullAddress.length < 3"
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
               :class="{'form-control': true, 'is-invalid': msg.country && showErrors2}" maxlength="30"
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
      streetNumber: null,
      streetName: null,
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
      addressCountry: '',
      addressRegion: '',
      addressCity: ''
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
      handler() {
        this.validateAddress()
        this.emitAddress()
      },
      deep: true
    },
    /** Validate address when show errors is set **/
    showErrors() {
      this.validateAddress()
      this.emitAddress()
    }
  },
  methods: {
    /**
     * Function to run when the full address has been entered
     */
    addressEntered() {
      // Clear previously selected address
      this.locationSelected = null

      // Check more than 2 characters are entered
      if (this.fullAddress.length < 3) {
        this.suggestions = []
        return
      }

      // Get possible addresses from photon api
      this.getPhotonAddress(this.fullAddress)
          .then((res) => {
            this.suggestions = []
            res.data.features.forEach((location) => {
              const address = {
                id: location.properties.osm_id,
                streetNumber: location.properties.housenumber,
                streetName: location.properties.street,
                city: location.properties.district,
                region: location.properties.state,
                country: location.properties.country,
                postcode: location.properties.postcode
              }
              if (this.addressIsValid(address)) {
                this.suggestions.push(address)
              }
            })
          })
        .catch((err) => {
          // Check if the error is from the request canceling
          if (err.message !== "A new request was made") {
            throw err
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
        stringRep += location.city
      }

      // Add the postcode
      if (location.region) {
        stringRep += stringRep === '' ? '' : ', '
        stringRep += `${location.region} ${location.postcode}`
      }

      // Add the postcode
      if (location.country) {
        stringRep += stringRep === '' ? '' : ', '
        stringRep += `${location.country}`
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
     * @returns {boolean} True if the location is valid
     */
    addressIsValid(location) {
      return location.country
    },

    /** Changes input mode between manual and search. */
    swapInputMode() {
      if (this.fullAddressMode) {
        this.fullAddressMode = false
        this.fullAddress = null
        this.locationSelected = null
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
      this.$emit('setAddress', address)
      this.$emit('setAddressValid', this.valid)
    },

    /**
     * Validates the address variables
     * Checks if the variables are empty, if so displays a warning message
     */
    validateAddress() {
      // TODO: Check for blank when in search mode

      if (this.address.country === '') {
        this.msg['country'] = 'Please enter a country'
      } else {
        this.msg['country'] = null
      }
      if (this.address.streetNumber !== '' && this.address.streetName === '') {
        this.msg['streetName'] = 'Please enter a Street Name'
      } else {
        this.msg['streetName'] = ''
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
</style>