<template>
  <div>
    <login-required
            page="view your profile page"
            v-if="!isLoggedIn"
    />
    <!-- Greeting User -->
    <div v-else>
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h2>Hello {{ firstName }}</h2>
        </div>
      </div>
      <div class="row">
        <div class="col-12 text-center mb-2">
          <router-link class="nav-link d-inline" to="/registerbusiness">Create Business</router-link>
        </div>
      </div>

    </div>
  </div>
</template>

<script>

import { User } from '@/Api'
import LoginRequired from "./LoginRequired";

export default {
  name: "Home",
  props: {
    msg: String
  },
  mounted() {
    //the getUserData function at this moment requires a id (required for the API), this will have to be changed later
    //to be the users email not id as id will be from the database
    User.getUserData(this.$root.$data.user.state.userId).then((response) => this.profile(response))
  },
  computed: {
    /**
     * Check if user is logged in
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
    // Save the data given by the API into the variables used to display the information on screen
    /**
     * Assigns the data from the response to the profile variables
     * @param response is the response from the server
     */
    profile(response) {

      this.firstName = response.data.firstName
      this.middleName = response.data.middleName
      this.lastName = response.data.lastName
      this.nickName = response.data.nickname
      this.bio = response.data.bio
      this.email = response.data.email
      this.dob = response.data.dateOfBirth
      this.phoneNumber = response.data.phoneNumber
      this.homeAddress = response.data.homeAddress
      this.dateJoined = response.data.created
      this.timeCalculator(Date.parse(this.dateJoined))
      this.dateJoined = this.dateJoined.substring(0, 10)
    },

    /**
     * Calculate the time since the user joined in Unix time (in milliseconds)
     * @param joined is the date the user joined in Unix time (in milliseconds)
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

    // Initialise the profile variables with null when the page loads
    return {
      firstName: null,
      middleName: null,
      lastName: null,
      nickName: null,
      bio: null,
      email: null,
      dob: null,
      phoneNumber: null,
      homeAddress: null,
      dateJoined: null,
      dateSinceJoin: null,
    }
  }
}

</script>
