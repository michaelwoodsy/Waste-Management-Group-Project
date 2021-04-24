<!-- User thumbnail and name for choosing who to act as in navbar -->

<template>
  <div class="nav-item">
    <!-- Image and name -->
    <div class="nav-link pointer" data-toggle="dropdown">
      <!-- Profile photo -->
      <img
          alt="profile"
          class="profile-image rounded-circle"
          src="../../public/profile.png"
      />
      <!-- Users name -->
      {{ actorName }}
    </div>

    <!-- Dropdown menu when name is clicked -->
    <div class="dropdown-menu dropdown-menu-left dropdown-menu-sm-right">

      <!-- Change to business account menu -->
      <div v-if="businessAccounts.length > 0">
        <h6 class="dropdown-header">Businesses</h6>
        <a
            v-for="business in businessAccounts"
            v-bind:key="business.id"
            class="dropdown-item pointer"
            @click="actAsBusiness(business)"
        >
          <img alt="profile" class="profile-image-sm rounded-circle"
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
            class="dropdown-item pointer"
            @click="actAsUser(user)"
        >
          <img alt="profile" class="profile-image-sm rounded-circle"
               src="../../public/profile.png">
          {{ user.firstName }} {{ user.lastName }}
        </a>
        <div class="dropdown-divider"/>
      </div>

      <!-- Profile and logout section -->
      <div v-if="this.actor.type === 'business'">
        <router-link class="dropdown-item" :to="productCatalogueRoute">Product Catalogue</router-link>
        <router-link class="dropdown-item" :to="inventoryRoute">Inventory</router-link>
      </div>
      <div v-else>
        <router-link class="dropdown-item" to="/businesses">Create Business</router-link>
      </div>
      <div class="dropdown-divider"/>
      <router-link class="dropdown-item" @click.native="logOut()" to="/login">Logout</router-link>
    </div>

  </div>
</template>

<script>
export default {
  name: "UserProfileLinks",
  computed: {
    /** Returns the users profile url **/
    userProfileRoute() {
      return `users/${this.$root.$data.user.state.userId}`;
    },

    /** Returns the product catalogue url **/
    productCatalogueRoute() {
      return `businesses/${this.actor.id}/products`;
    },

    /** Returns the inventory url **/
    inventoryRoute () {
      return `businesses/${this.actor.id}/inventory`;
    },

    /**
     * Current actor
     * Returns {name, id, type}
     **/
    actor() {
      return this.$root.$data.user.state.actingAs
    },

    /** Returns the current logged in users data **/
    actorName() {
      return this.actor.name
    },

    /** A list of user accounts the user can change to.
     * Empty list if the user is already acting as themselves **/
    userAccounts() {
      return [this.$root.$data.user.state.userData]
    },

    /** A list of the users associated business accounts **/
    businessAccounts() {
      return this.$root.$data.user.state.userData.businessesAdministered
    }
  },
  methods: {
    /** Logs the user out **/
    logOut() {
      this.$root.$data.user.logout();
    },

    /** Sets the current logged in user to act as a business account **/
    actAsBusiness(business) {
      this.$root.$data.user.setActingAs(business.id, business.name, 'business')
    },

    /** Sets the current logged in user to act as a user account **/
    actAsUser(userData) {
      this.$root.$data.user.setActingAs(userData.id, userData.firstName + ' ' + userData.lastName, 'user')
    }
  }
}
</script>

<style scoped>

.profile-image {
  height: 35px;
  margin-right: 5px;
}

.profile-image-sm {
  height: 20px;
  margin-right: 5px;
}

.dropdown-item {
  display: flex;
  align-items: center;
}

</style>
