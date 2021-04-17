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

    <div v-else>
      <!-- Title of the page -->
      <div class="text-center mb-4">
        <h1 class="test-center">Edit Product</h1>
      </div>

      <!-- Display error message if there is one -->
      <alert v-if="errorMessage">Error: {{ errorMessage }}</alert>

      <!-- Display when the product is loading -->
      <div v-if="loading">
        <p class="text-muted">Loading...</p>
      </div>

      <!-- Edit product div -->
      <div v-else-if="product" class="container-fluid">

        <!-- Row for edit form -->
        <div class="row">
          <div class="col-12 col-sm-8 offset-sm-2">

            <form>
              <!-- ID -->
              <div class="form-group row">
                <label for="id" class="col-sm-4 col-form-label">ID:</label>
                <div class="col-sm-8">
                  <input type="text" class="form-control" id="id" v-model="newProduct.id">
                </div>
              </div>

              <!-- Name -->
              <div class="form-group row">
                <label for="name" class="col-sm-4 col-form-label">Name:</label>
                <div class="col-sm-8">
                  <input type="text" class="form-control" id="name" v-model="newProduct.name">
                </div>
              </div>

              <!-- Description -->
              <div class="form-group row">
                <label for="description" class="col-sm-4 col-form-label">Description:</label>
                <div class="col-sm-8">
                  <textarea type="text" class="form-control" id="description" rows="3" v-model="newProduct.description"></textarea>
                </div>
              </div>

              <!-- Recommended Retail Price -->
              <div class="form-group row">
                <label for="rrp" class="col-sm-4 col-form-label">Recommended Retail Price:</label>
                <div class="col-sm-8">
                  <input type="text" class="form-control" id="rrp" v-model.number="newProduct.recommendedRetailPrice">
                </div>
              </div>

              <!-- Manufacturer -->
              <div class="form-group row">
                <label for="manufacturer" class="col-sm-4 col-form-label">Manufacturer:</label>
                <div class="col-sm-8">
                  <input type="text" class="form-control" id="manufacturer" v-model="newProduct.manufacturer">
                </div>
              </div>

            </form>

          </div>
        </div>

        <!-- Row for submit / cancel buttons -->
        <div class="row text-center mt-3">
          <div class="col-12 col-sm-8 offset-sm-2">

            <!-- Cancel button when changes are made -->
            <button type="button" :class="cancelBtnClass">
              Cancel
            </button>

            <!-- Save Changes button -->
            <button type="button" class="btn btn-primary ml-1 my-1 float-right" :disabled="!changesMade">
              Save Changes
            </button>

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
      newProduct: null
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
    },

    /**
     * Returns true if changes have been made to the product
     * @returns {boolean}
     */
    changesMade() {
      if (!this.product) { return false }
      let allSame = true;
      for (const [key, val] of Object.entries(this.product)) {
        if (this.newProduct[key] !== val && typeof val !== 'object') {
          console.log(val)
          console.log(this.newProduct[key])
          console.log(typeof val)
          allSame = false;
        }
      }
      return !allSame
    },

    /**
     * Defines the css classes used by the cancel button
     * @returns {{"m-1": boolean, "btn-danger": boolean, "btn-primary": boolean, btn: boolean}}
     */
    cancelBtnClass() {
      return {
        "btn": true,
        "mr-1": true,
        "my-1": true,
        "btn-danger": this.changesMade,
        "btn-link": !this.changesMade,
        "float-left": true
      }
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
      this.newProduct = {...this.product}
      if (!this.product) {
        this.errorMessage = `There is no product with id ${this.productId}.`
      }
      this.loading = false
    }
  }
}
</script>

<style scoped>
.btn {
  transition-duration: 0.1s;
}
</style>