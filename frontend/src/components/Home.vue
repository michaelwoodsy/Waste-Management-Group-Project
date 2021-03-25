<template>
  <div>
    <login-required
            page="view your profile page"
            v-if="!isLoggedIn"
    />

    <div v-else>
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h2>Home Page</h2>
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

      <!-- Date of Birth -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Date of Birth: </p>
        </div>
        <div class="col-6">
          <p>{{ dob }} </p>
        </div>
      </div>

      <!-- Phone Number -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Phone Number: </p>
        </div>
        <div class="col-6">
          <p>{{ phoneNumber }} </p>
        </div>
      </div>

      <!-- Home Address -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Home Address: </p>
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
    isLoggedIn () {
      return this.$root.$data.user.state.loggedIn
    }
  },
  components: {
    LoginRequired
  },
  methods: {
    // Save the data given by the API into the variables used to display the information on screen
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
