<template>
  <div>

    <login-required
        v-if="!isLoggedIn"
        page="view a users profile page"
    />

    <div v-else>

      <div class="row">
        <div class="col-12 text-center mb-2">
          <h2>{{ firstName }} {{ lastName }}</h2>
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
          <p>{{ dateJoined }} ({{ dateSinceJoin }}) </p>
        </div>
      </div>

      <!-- Primary Admin to Business links -->
      <div v-if="isPrimaryAdmin" class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Primary Administrator of: </p>
        </div>
        <div class="col-6 ">
          <table>
            <tr v-for="(business, index) in primaryAdminOf" :key="index">
              <td>
                <router-link :to="`/businesses/${business.id}`" class="nav-link d-inline">
                  {{ business.name }}
                </router-link>
              </td>
            </tr>
          </table>
        </div>
      </div>

      <!--Button to add as admin to business currently acting as-->
      <div class="d-flex justify-content-center" v-if="!isViewingSelf && !isAdministrator &&
                                                        this.$root.$data.user.isActingAsBusiness() &&
                                                        this.$root.$data.user.isPrimaryAdminOfBusiness()">
        <button class="btn btn-block btn-secondary" style="width: 40%;margin:0 20px; font-size: 14px;" v-on:click="addAdministrator">Add as administrator to business</button>
      </div>

      <div class="row">
        <div class="col-12 text-center mb-2" v-if="addedAdmin">
          <br>
          <p style="color: green">{{ addedAdmin }}</p>
          <br>
        </div>
        <div class="col-12 text-center mb-2" v-if="error">
          <br>
          <p style="color: red">{{ error }}</p>
          <br>
        </div>
      </div>

    </div>

  </div>
</template>

<script>

import { User } from '@/Api'
import {Business} from '@/Api'
import LoginRequired from "./LoginRequired"

export default {
  name: "ProfilePage",
  props: {
    msg: String
  },
  mounted() {
    User.getUserData(this.userId).then((response) => this.profile(response))
  },
  computed: {
    /**
     * Gets the users' ID
     * @returns {any}
     */
    userId() {
      return this.$route.params.userId
    },
    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },
    /**
     * Returns true if the user is primary admin of any businesses
     * @returns {boolean|*}
     */
    isPrimaryAdmin() {
      return this.primaryAdminOf.length > 0;
    },
    /**
     * Returns true if the user is currently viewing their profile page
     * @returns {boolean|*}
     */
    isViewingSelf () {
      return this.userId === this.$root.$data.user.state.userId
    },
  /**
   * Returns true if the user is an administrator of the curentley acting business
   * @returns {boolean|*}
   */
  isAdministrator () {
    for (let i = 0; i < this.businessesAdministered.length; i++) {
      if (this.businessesAdministered[i].id === this.$root.$data.user.state.actingAs.id) return true
    }
    return false
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
      if (response.data.homeAddress.indexOf(",") === -1) this.homeAddress = response.data.homeAddress
      else this.homeAddress = response.data.homeAddress.slice(response.data.homeAddress.indexOf(",") + 2)

      //Uncomment the following statements and remove the two lines above when the home address is an object. Hopefully it works
      /*
      this.homeAddress = ''
      if (response.data.homeAddress.city !== '') this.homeAddress += `${response.data.homeAddress.city}, `
      if (response.data.homeAddress.region !== '') this.homeAddress += `${response.data.homeAddress.region}, `
      this.homeAddress += response.data.homeAddress.country
      if (response.data.homeAddress.postcode !== '') this.homeAddress += `, ${response.data.homeAddress.postcode}`
      */

      this.dateJoined = response.data.created
      this.timeCalculator(Date.parse(this.dateJoined))
      this.dateJoined = this.dateJoined.substring(0, 10)
      this.businessesAdministered = response.data.businessesAdministered
      this.setPrimaryAdminList()
    },

    /**
     * Function to add an administrator to the currently acting as business.
     * uses the addAdministrator method in the Business api.js file to send a request to the backend
     */
    async addAdministrator() {
      try {
        await Business.addAdministrator(Number(this.$root.$data.user.state.actingAs.id), Number(this.$route.params.userId))
        this.addedAdmin = `Added ${this.firstName} ${this.lastName} to administrators of business`
        //Reload the data
        User.getUserData(this.userId).then((response) => this.profile(response))
      }
      catch (err) {
        this.error = err.response
            ? err.response.data.slice(err.response.data.indexOf(":")+2)
            : err
      }
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
        case (sinceYears === 1):
          text = `${sinceYears} year`
          break
        case (sinceYears > 1):
          text = `${sinceYears} years`
          break
      }

      switch (true) {
        case (text === '' && sinceMonths > 1):
          text = `${sinceMonths} months`
          break
        case (text === '' && sinceMonths === 1):
          text = `${sinceMonths} month`
          break
        case (sinceMonths > 1):
          text += ` and ${sinceMonths} months`
          break
        case (sinceMonths === 1):
          text += `and ${sinceMonths} month`
          break
        case (text === '' && sinceMonths === 0):
          text = 'Less than 1 month'
          break
      }
      this.dateSinceJoin = text
    },

    /**
     * Creates list of businesses that user is primary admin of
     */
    setPrimaryAdminList() {
      this.primaryAdminOf = []
      for (const business of this.businessesAdministered) {
        if (business.primaryAdministratorId === parseInt(this.$route.params.userId)) {
          this.primaryAdminOf.push(business);
        }
      }
    }

  },

  data() {

    return {
      firstName: null,
      middleName: null,
      lastName: null,
      nickName: null,
      bio: null,
      email: null,
      homeAddress: null,
      dateJoined: null,
      dateSinceJoin: null,
      businessesAdministered: [],
      primaryAdminOf: [],
      addedAdmin: null,
      error:null
    }
  }
}

</script>
