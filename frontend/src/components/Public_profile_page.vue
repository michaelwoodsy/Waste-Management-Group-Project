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

    </div>

  </div>
</template>

<script>

import { User } from '@/Api'
import LoginRequired from "./LoginRequired";

export default {
  name: "Public_profile_page",
  props: {
    msg: String
  },
  mounted() {
    User.getUserDataFake(this.userId).then((response) => this.profile(response)) // TODO: Change to real function when teamed up with backend team
  },
  computed: {
    userId () {
      return this.$route.params.userId
    },
    isLoggedIn () {
      console.log(this.$root.$data.user.state.loggedIn)
      return this.$root.$data.user.state.loggedIn
    }
  },
  components: {
    LoginRequired
  },
  methods: {
    profile(response) {
      this.firstName = response.data.firstName
      this.middleName = response.data.middleName
      this.lastName = response.data.lastName
      this.nickName = response.data.nickname
      this.bio = response.data.bio
      this.email = response.data.email

      //Need to remove the street and number part of this address, just splice from the first ','
      this.homeAddress = response.data.homeAddress.slice(response.data.homeAddress.indexOf(",")+2);
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
    }
  }
}

</script>
