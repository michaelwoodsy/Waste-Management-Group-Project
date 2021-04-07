<template>
  <div>

    <login-required
        page="view a business's profile page"
        v-if="!isLoggedIn"
    />

    <div v-else>
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h2>{{ name }}</h2>
        </div>
      </div>


      <!-- Name of Business -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Name of Business: </p>
        </div>
        <div class="col-6">
          <p>{{ name }}</p>
        </div>
      </div>

      <!-- Description -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Description: </p>
        </div>
        <div class="col-6">
          <p>{{ description }}</p>
        </div>
      </div>

      <!-- Address-->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Address: </p>
        </div>
        <div class="col-6">
          <p>{{ address }}</p>
        </div>
      </div>

      <!-- Type -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Type: </p>
        </div>
        <div class="col-6">
          <p>{{ businessType }}</p>
        </div>
      </div>

      <!-- Date of Registration -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Date of Registration: </p>
        </div>
        <div class="col-6">
          <p>Registered since: {{dateJoined}} ({{dateSinceJoin}})</p>
        </div>
      </div>

      <!-- Administrators -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Administrators: </p>
        </div>
        <div class="col-6">
          <p>{{ administrators }}</p>
        </div>
      </div>
    </div>

  </div>
</template>

<script>

import {Business} from '@/Api';
import LoginRequired from "./LoginRequired";

export default {
  name: "BusinessProfilePage",
  props: {
    msg: String
  },
  mounted() {
    Business.getBusinessData(this.businessId).then((response) => this.profile(response))
  },
  computed: {
    /**
     * Gets the business ID
     * @returns {any}
     */
    businessId() {
      return this.$route.params.businessId;
    },
    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*}
     */
    isLoggedIn () {
      return this.$root.$data.user.state.loggedIn
    }
  },
  components: {
    LoginRequired
  },
  methods: {
    /**
     * Assigns the data from the response to the profile variables
     * @param response is the response from the server
     */
    profile(response) {
      this.name = response.data.name
      this.description = response.data.description
      this.businessType = response.data.businessType
      this.administrators = response.data.administrators
      console.log(`Response about business: ${JSON.stringify(response)}`);


      //Need to remove the street and number part of this address, just splice from the first ','
      if (response.data.address.indexOf(",") === -1) this.address = response.data.address
      else this.address = response.data.address.slice(response.data.address.indexOf(",")+2)

      //Uncomment the following statements and remove the two lines above when the home address is an object. Hopefully it works
      /*
      this.homeAddress = ''
      if (response.data.address.city !== '') this.address += `${response.data.address.city}, `
      if (response.data.address.region !== '') this.address += `${response.data.address.region}, `
      this.address += response.data.address.country
      if (response.data.address.postcode !== '') this.address += `, ${response.data.address.postcode}`
      */

      this.dateJoined = response.data.created
      this.timeCalculator(Date.parse(this.dateJoined))
      this.dateJoined = this.dateJoined.substring(0, 10)
    },

    /**
     * Calculates the time since the user joined in ms from the start of time (1970)
     * @param joined date when the user joined
     */
    timeCalculator(joined) {
      let dateNow = new Date();
      const milliYear = 31557600000
      //milliMonths = 30.4 days
      const milliMonth = 2629800000

      let text = ''
      dateNow = Date.parse(dateNow)
      //Calculate time since they have been a member in milliseconds
      let since = dateNow - joined
      let sinceYears = 0
      let sinceMonths = 0

      //Find how many years
      while (since >= milliYear) {
        sinceYears += 1
        since -= milliYear
      }

      //Find how many months
      while (since >= milliMonth) {
        sinceMonths += 1
        since -= milliMonth
      }

      //Format Text
      switch (true) {
        case (sinceYears === 1): text = `${sinceYears} year`
          break
        case (sinceYears > 1): text = `${sinceYears} years`
          break
      }

      switch (true) {
        case (text === '' && sinceMonths > 1): text = `${sinceMonths} months`
          break
        case (text === '' && sinceMonths === 1): text = `${sinceMonths} month`
          break
        case (sinceMonths > 1): text += ` and ${sinceMonths} months`
          break
        case (sinceMonths === 1): text += `and ${sinceMonths} month`
          break
        case (text === '' && sinceMonths === 0): text = 'Less than 1 month'
          break
      }
      this.dateSinceJoin = text
    }
  },
  data() {

    // Filled with the test data taken from the swagger.io API
    return {
      name: null,
      description: null,
      businessType: null,
      address: null,
      dateJoined: null,
      dateSinceJoin: null,
      administrators: null,
    }
  }

}
</script>

