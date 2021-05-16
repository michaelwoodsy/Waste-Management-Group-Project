<template>
  <div>

    <login-required
        v-if="!isLoggedIn"
        page="view your profile page"
    />

    <!-- Greeting User -->
    <div v-else-if="this.actor.type === 'user'">
      <div class="row">
        <div class="col text-center mb-2">
          <h2>Hello {{ actorName }}</h2>
          <router-link to="/marketplace" class="btn btn-outline-primary mx-2">View Marketplace</router-link>
        </div>
      </div>
    </div>

    <!-- Greeting Business -->
    <div v-else>
      <div class="row">
        <div class="col text-center mb-2">
          <h2>{{ actorName }}</h2>
          <router-link :to="productCatalogueRoute" class="btn btn-outline-primary mx-2">View Product Catalogue
          </router-link>
          <router-link :to="inventoryRoute" class="btn btn-outline-primary mx-2">View Inventory</router-link>
          <router-link :to="saleListingRoute" class="btn btn-outline-primary mx-2">View Sale Listings</router-link>
        </div>
      </div>
    </div>

  </div>
</template>

<script>

import LoginRequired from "./LoginRequired";

export default {
  name: "Home",
  props: {
    msg: String
  },

  mounted() {
  },

  computed: {
    /**
     * Check if user is logged in
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    /** Returns the product catalogue url **/
    productCatalogueRoute() {
      return `businesses/${this.actor.id}/products`;
    },

    /** Returns the inventory url **/
    inventoryRoute() {
      return `businesses/${this.actor.id}/inventory`;
    },

    /** Returns the sale listing url **/
    saleListingRoute() {
      return `businesses/${this.actor.id}/listings`;
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

  },
  components: {
    LoginRequired
  }
}

</script>

<style scoped>

</style>