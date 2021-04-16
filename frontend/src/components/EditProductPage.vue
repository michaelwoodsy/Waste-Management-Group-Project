<template>
  <div>
    <login-required
        v-if="!isLoggedIn"
        page="view an individual product"
    />

    <admin-required
        v-else-if="!isAdminOfBusiness"
        :page="`of the business ${this.businessId} to edit its products`"
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
import LoginRequired from "@/components/LoginRequired";
import AdminRequired from "@/components/AdminRequired";

export default {
  name: "EditProductPage",
  components: {
    LoginRequired,
    AdminRequired
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
     * Gets the business ID that is in the current path
     * @returns {any}
     */
    businessId() {
      return this.$route.params.businessId;
    },

    /**
     * Gets the product ID that is in the current path
     * @returns {any}
     */
    productId() {
      return this.$route.params.productId;
    },

    /**
     * A list of businesses the current user administers.
     */
    businessesAdministered() {
      if (this.isLoggedIn) {
        return this.$root.$data.user.state.userData.businessesAdministered;
      }
      return []
    },

    /**
     * Checks if the user is an admin of the business.
     * @returns {boolean}
     */
    isAdminOfBusiness() {
      let isAdmin = false;
      // iterate over each business they administer and check if they administer this one
      this.businessesAdministered.forEach((business) => {
        if (business.id.toString() === this.businessId.toString()) {
          isAdmin = true;
        }
      })
      return isAdmin
    }
  }
}
</script>

<style scoped>

</style>