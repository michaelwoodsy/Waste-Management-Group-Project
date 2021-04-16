<template>
  <div>
    <!-- Check if the user is logged in -->
    <login-required
        v-if="!isLoggedIn"
        page="view an individual product"
    />

    <!-- Check if the user is an admin of the business -->
    <admin-required
        v-else-if="!isAdminOfBusiness"
        :page="`of the business ${this.businessId} to edit its products`"
    />

    <div v-else class="text-center">
      <!-- Title of the page -->
      <h1>Edit Product</h1>

      <!-- Display error message if there is one -->
      <alert v-if="errorMessage">Error: {{ errorMessage }}</alert>

      <!-- Display when the product is loading -->
      <div v-if="loading">
        <p class="text-muted">Loading...</p>
      </div>

      <!-- Edit product div -->
      <div v-else-if="product">

        <!-- Name -->
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
            <p>{{ 1 }} </p>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import AdminRequired from "@/components/AdminRequired";
import Alert from "@/components/Alert";
// import {Business} from "@/Api";

export default {
  name: "EditProductPage",
  mounted () {
    this.loadProduct();
  },
  data () {
    return {
      errorMessage: null,
      loading: true,
      product: null,
      newProduct: {}
    }
  },
  components: {
    LoginRequired,
    AdminRequired,
    Alert
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
  },
  methods: {
    loadProduct () {
      // Uncomment when a getProducts method exists

      // Business.getProducts(this.businessId)
      //     .then((res) => {
      //       this.product = res.data.find(prod => prod.id === this.productId.toString())
      //       if (!this.product) {
      //         this.errorMessage = `There is no product with id ${this.productId}.`
      //       }
      //       this.loading = false
      //     })
      //     .catch((err) => {
      //       this.errorMessage = err.response.data.message;
      //       this.loading = false
      //     })

      // Fake data for testing here
      const data = [
        {
          "id": "WATT-420-BEANS",
          "name": "Watties Baked Beans - 420g can",
          "description": "Baked Beans as they should be.",
          "manufacturer": "Heinz Wattie's Limited",
          "recommendedRetailPrice": 2.2,
          "created": "2021-04-16T23:08:48.584Z",
          "images": [
            {
              "id": 1234,
              "filename": "/media/images/23987192387509-123908794328.png",
              "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
            }
          ]
        }
      ]
      this.product = data.find(prod => prod.id === this.productId.toString())
      if (!this.product) {
        this.errorMessage = `There is no product with id ${this.productId}.`
      }
      this.loading = false
    }
  }
}
</script>

<style scoped>

</style>