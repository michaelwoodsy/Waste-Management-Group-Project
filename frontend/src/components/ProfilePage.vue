<template>
  <div>

    <login-required
        page="view a users profile page"
        v-if="!isLoggedIn"
    />

    <div v-else>

      <div class="row">
        <div class="col-12 text-center mb-2">
          <h2>User Page</h2>
        </div>
      </div>


      <!-- First Name -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>First Name: </p>
        </div>
        <div class="col-6">
          <p>{{ firstName }} </p>
        </div>
      </div>

      <!-- Middle Name -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Middle Name: </p>
        </div>
        <div class="col-6">
          <p>{{ middleName }} </p>
        </div>
      </div>

      <!-- Last Name -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Last Name: </p>
        </div>
        <div class="col-6">
          <p>{{ lastName }} </p>
        </div>
      </div>

      <!-- Nickname -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Nickname: </p>
        </div>
        <div class="col-6">
          <p>{{ nickName }} </p>
        </div>
      </div>

      <!-- Bio -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Bio: </p>
        </div>
        <div class="col-6">
          <p>{{ bio }} </p>
        </div>
      </div>

      <!-- Email -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold ">
          <p>Email: </p>
        </div>
        <div class="col-6">
          <p>{{ email }} </p>
        </div>
      </div>

      <!-- Home Address -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Location: </p>
        </div>
        <div class="col-6">
          <p>{{ homeAddress }} </p>
        </div>
      </div>

      <!-- Member Since -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Member Since: </p>
        </div>
        <div class="col-6 ">
          <p>{{dateJoined}} ({{dateSinceJoin}}) </p>
        </div>
      </div>

    </div>

  </div>
</template>

<script>

import { User } from '@/Api'
import LoginRequired from "./LoginRequired";

export default {
  name: "ProfilePage",
  props: {
    msg: String
  },
  mounted() {
    User.getUserData(this.userId).then((response) => this.profile(response))
    //User.getUserDataFake(this.userId).then((response) => this.profile(response)) // TODO: Change to real function when teamed up with backend team
  },
  computed: {
    /**
     * Gets the users' ID
     * @returns {any}
     */
    userId () {
      return this.$route.params.userId
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
      this.firstName = response.data.firstName
      this.middleName = response.data.middleName
      this.lastName = response.data.lastName
      this.nickName = response.data.nickname
      this.bio = response.data.bio
      this.email = response.data.email

      //Need to remove the street and number part of this address, just splice from the first ','
      this.homeAddress = response.data.homeAddress.slice(response.data.homeAddress.indexOf(",")+2);

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
      firstName: null,
      middleName: null,
      lastName: null,
      nickName: null,
      bio: null,
      email: null,
      homeAddress: null,
      dateJoined: null,
      dateSinceJoin: null
    }
  }
}

</script>
