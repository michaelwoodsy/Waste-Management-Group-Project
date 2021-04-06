<!-- User thumbnail and name for choosing who to act as in navbar -->

<template>
  <div class="btn-group">
    <!-- Image and name -->
    <span class="float-right d-inline pointer"  data-toggle="dropdown">
      <!-- Profile photo -->
      <img
          class="img-fluid profile-image rounded-circle mr-1"
          alt="profile"
          src="../../public/profile.png"
      >
      <!-- Users name -->
      {{ actorName }}
    </span>

    <!-- Dropdown menu when name is clicked -->
    <div class="dropdown-menu dropdown-menu-left dropdown-menu-sm-right">

      <!-- Change to business account menu -->
      <div v-if="businessAccounts.length > 0">
        <h6 class="dropdown-header">Businesses</h6>
        <a
            v-for="business in businessAccounts"
            v-bind:key="business.id"
            @click="actAsBusiness(business)"
            class="dropdown-item"
        >
          <img class="profile-image-sm rounded-circle mb-1" alt="profile"
               src="../../public/profile.png">
          {{ business.name }}
        </a>
        <div class="dropdown-divider"/>
      </div>

      <!-- Change to user account menu -->
      <div v-if="userAccounts.length > 0">
        <h6 class="dropdown-header">Users</h6>
        <a
            v-for="user in userAccounts"
            v-bind:key="user.id"
            @click="actAsUser(user)"
            class="dropdown-item"
        >
          <img class="profile-image-sm rounded-circle mb-1" alt="profile"
               src="../../public/profile.png">
          {{ user.firstName }} {{ user.lastName }}
        </a>
        <div class="dropdown-divider"/>
      </div>

      <!-- Profile and logout section -->
      <router-link class="dropdown-item" :to="userProfileRoute">My Profile</router-link>
      <router-link class="dropdown-item" @click.native="logOut()" to="/login">Logout</router-link>
    </div>

  </div>
</template>

<script>
export default {
  name: "UserProfileLinks",
  computed: {
    /** Returns the users profile url **/
    userProfileRoute () {
      return `users/${this.$root.$data.user.state.userId}`;
    },

    /**
     * Current actor
     * Returns {name, id, type}
     **/
    actor () {
      return this.$root.$data.user.state.actingAs
    },

    /** Returns the current logged in users data **/
    actorName () {
      return this.actor.name
    },

    /** A list of user accounts the user can change to.
     * Empty list if the user is already acting as themselves **/
    userAccounts () {
      return [this.$root.$data.user.state.userData]
    },

    /** A list of the users associated business accounts **/
    businessAccounts () {
      // Returns a list of fake business accounts for the time being
      return [
        {
          "id": 100,
          "administrators": [
            null
          ],
          "primaryAdministratorId": 20,
          "name": "Lumbridge General Store",
          "description": "A one-stop shop for all your adventuring needs",
          "address": {
            "streetNumber": "3/24",
            "streetName": "Ilam Road",
            "city": "Christchurch",
            "region": "Canterbury",
            "country": "New Zealand",
            "postcode": "90210"
          },
          "businessType": "Accommodation and Food Services",
          "created": "2020-07-14T14:52:00Z"
        }
      ]
    }
  },
  methods: {
    /** Logs the user out **/
    logOut() {
      this.$root.$data.user.logout();
    },

    /** Sets the current logged in user to act as a business account **/
    actAsBusiness (business) {
      this.$root.$data.user.setActingAs(business.id, business.name, 'business')
    },

    /** Sets the current logged in user to act as a user account **/
    actAsUser (userData) {
      this.$root.$data.user.setActingAs(userData.id, userData.firstName + ' ' + userData.lastName, 'business')
    }
  }
}
</script>

<style scoped>
.profile-image {
  height: 35px;
}

.profile-image-sm {
  height: 20px;
}
</style>