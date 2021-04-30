<template>
  <div>
    <!--    Title for Address inputs    -->
    <div class="form-row mb-3">
      <span class="addressText"><b>Address</b></span>
    </div>


    <!--    Div for displaying full address input box    -->
    <div class="form-row mb-3" v-if="fullAddressMode">
      <label for="fullAddress"><b>Address</b></label>
      <input id="fullAddress" v-model="fullAddress" class="form-control w-100"
             @keyup="addressEntered"
             maxlength="250"
             placeholder="Address" type="text">

      <!--    Address suggestions    -->
      <div v-if="suggestions">
        <p v-bind:key="location.id" v-for="location in suggestions">
          {{location.streetNumber}} {{location.streetName}}, {{location.city}},
          {{location.region}} {{location.postcode}}, {{location.country}}
        </p>
      </div>
    </div>

    <!--    Div for displaying individual fields for address    -->
    <div v-else>
      <div class="form-row mb-3">
        <!--    Home Address Street Number    -->
        <label for="homeAddressNumber"><b>Street Number</b></label>
        <input id="homeAddressNumber"
               v-model="homeAddress.streetNumber"
               class="form-control"
               maxlength="20"
               placeholder="Enter your Street Number" style="width:100%" type="text">
      </div>

      <div class="form-row mb-3">
        <!--    Home Address Street Name    -->
        <label for="homeAddressStreet"><b>Street Name</b></label><br/>
        <input id="homeAddressStreet" v-model="homeAddress.streetName" class="form-control" placeholder="Enter your Street Name"
               style="width:100%" type="text"><br>

        <!--    Error message for street name input   -->
        <span v-if="msg.streetName" class="error-msg">{{ msg.streetName }}</span>
      </div>

      <div class="form-row mb-3">
        <!--    Home Address City    -->
        <label for="homeAddressCity"><b>City or Town</b></label>
        <input id="homeAddressCity" v-model="addressCity" class="form-control" maxlength="50"
               placeholder="Enter your City" style="width:100%" type="search">

        <!--    Autofill City/Town    -->
        <div v-for="city in cities" v-bind:key="city" style="width:100%; text-align: left">
          <a class="address-output" @click="changeCity(city)">{{ city }}</a>
        </div>
      </div>

      <div class="form-row mb-3">
        <!--    Home Address Region    -->
        <label for="homeAddressRegion"><b>Region</b></label>
        <input id="homeAddressRegion" v-model="addressRegion" class="form-control" maxlength="50"
               placeholder="Enter your Region" style="width:100%" type="search">

        <!--    Autofill region    -->
        <div v-for="region in regions" v-bind:key="region" style="width:100%; text-align: left">
          <a class="address-output" @click="changeRegion(region)">{{ region }}</a>
        </div>
      </div>

      <div class="form-row mb-3">
        <!--    Home Address Country    -->
        <label for="homeAddressCountry"><b>Country<span class="required">*</span></b></label>
        <input id="homeAddressCountry" v-model="addressCountry"
               :class="{'form-control': true, 'is-invalid': msg.country}" maxlength="30"
               placeholder="Enter your Country" required style="width:100%" type="search">
        <!--    Error message for the country input    -->
        <span class="invalid-feedback">{{ msg.country }}</span>

        <!--    Autofill country    -->
        <div v-for="country in countries" v-bind:key="country" style="width:100%; text-align: left">
          <a class="address-output" @click="changeCountry(country)">{{ country }}</a>
        </div>
      </div>

      <div class="form-row mb-3">
        <!--    Home Address Post Code    -->
        <label for="homeAddressPostCode"><b>Postcode</b></label>
        <input id="homeAddressPostCode" v-model="homeAddress.postcode" class="form-control"
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
      suggestions: []
    }
  },
  methods: {
    /**
     * Function to run when the address has been entered
     */
    addressEntered() {
      // Check more than 2 characters are entered
      if (this.fullAddress.length < 3) {
        this.suggestions = []
        return
      }

      this.getPhotonAddress(this.fullAddress)
          .then((res) => {
            this.suggestions = []
            res.data.features.forEach((location) => {
              if (location.properties.type === "house") {
                this.suggestions.push({
                  id: location.properties.osm_id,
                  streetNumber: location.properties.housenumber,
                  streetName: location.properties.street,
                  city: location.properties.district,
                  region: location.properties.state,
                  country: location.properties.country,
                  postcode: location.properties.postcode
                })
              }
            })
          })
    },

    /**
     * Makes a request to the photon address api.
     * @param text The text entered into the autofillable text field
     * @param limit Limit for the number of results to get from the request. Default 10.
     * @return Axios Promise
     */
    getPhotonAddress(text, limit=10) {
      // Cancel previous request if there is one
      if (this.cancelToken) {
        this.cancelToken("A new request was made")
      }

      // Make and return request promise
      return axios.get(`https://photon.komoot.io/api?q=${text}&limit=${limit}`, {
            cancelToken: new axios.CancelToken((c) => {
              this.cancelToken = c;
            })
      })
    }
  }
}
</script>

<style scoped>

</style>