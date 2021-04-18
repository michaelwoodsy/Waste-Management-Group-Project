<template>
  <div>
    <login-required
        v-if="!isLoggedIn"
        page="view an individual product"
    />

    <admin-required
        v-else-if="!isAdminOf()"
        page="view an individual product"
    />

    <div v-else>
      <!-- Product Id -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Product Id: </p>
        </div>
        <div class="col-6">
          <p>{{ Id }} </p>
        </div>
      </div>

      <!-- Name -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Name: </p>
        </div>
        <div class="col-6">
          <p>{{ name }} </p>
        </div>
      </div>

      <!-- Description -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Description: </p>
        </div>
        <div class="col-6">
          <p>{{ description }} </p>
        </div>
      </div>

      <!-- Recommended Retail Price -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Recommended Retail Price: </p>
        </div>
        <div class="col-6">
          <p>{{ recommendedRetailPrice }} </p>
        </div>
      </div>

      <!-- Created -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Created: </p>
        </div>
        <div class="col-6">
          <p>{{ created }} </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import {Business} from '@/Api'
import LoginRequired from "@/components/LoginRequired";
import AdminRequired from "@/components/AdminRequired";


export default {
  name: "IndividualProduct",
  props: {
    msg: String
  },

  mounted() {
    Business.getBusinessData(this.businessId).then((response) => this.profileBusiness(response))
    Business.getProductData(this.businessId, this.productId).then((response) => this.product(response))
  },

  computed: {
    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    /**
     * Gets the business ID
     * @returns {any}
     */
    businessId() {
      return this.$route.params.businessId;
    },

    /**
     * Gets the product ID
     * @returns {any}
     */
    productId() {
      return this.$route.params.productId;
    }
  },
  components: {
    LoginRequired,
    AdminRequired
  },

  methods: {
    /**
     * Check if the user is an admin of the current business
     */
    isAdminOf() {
      if (this.$root.$data.user.state.actingAs.type !== "business") return false
      const businessesAdministered = this.$root.$data.user.state.userData.businessesAdministered
      for (let i = 0; i < businessesAdministered.length; i++) {
        if (businessesAdministered[i].id === Number(this.businessId)) return true
      }
      return false
    },

    /**
     * Get the list of Administrators for the business
     * @param response
     */
    profileBusiness(response) {
      this.administrators = response.data.administrators;
    },
    /**
     * Set the variables for the current product
     * @param response
     */
    product(response) {
      this.Id = response.data.id
      this.name = response.data.name
      this.description = response.data.description
      this.recommendedRetailPrice = response.data.recommendedRetailPrice
      this.created = response.data.created
    }
  },

  data() {
    return {
      Id: null,
      name: null,
      description: null,
      recommendedRetailPrice: null,
      created: null,
    }
  }
}
</script>

<style scoped>

</style>