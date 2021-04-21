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
      <div class="row">
        <div class="col-12 col-sm-8 offset-sm-2">
          <alert v-if="errorMessage">Error: {{ errorMessage }}</alert>
        </div>
      </div>

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
                <label class="col-sm-4 col-form-label" for="id">ID<span class="text-danger">*</span></label>
                <div class="col-sm-8">
                  <input
                      id="id" v-model="newProduct.id"
                      :class="{'form-control': 1, 'is-invalid': !idValid && idBlur}"
                      maxlength="255"
                      type="text"
                      @blur="idBlur = true"
                  >
                  <div class="invalid-feedback">The ID can only contain letters, numbers, hyphens
                    and must be at least 1 character
                  </div>
                </div>
              </div>

              <!-- Name -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="name">Name<span class="text-danger">*</span></label>
                <div class="col-sm-8">
                  <input
                      id="name"
                      v-model="newProduct.name"
                      :class="{'form-control': true, 'is-invalid': !nameValid && nameBlur}"
                      maxlength="255"
                      type="text"
                      @blur="nameBlur = true"
                  >
                  <div class="invalid-feedback">A name is required</div>
                </div>
              </div>

              <!-- Description -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="description">Description</label>
                <div class="col-sm-8">
                  <textarea id="description" v-model="newProduct.description" class="form-control" maxlength="255" rows="3"
                            type="text"></textarea>
                </div>
              </div>

              <!-- Recommended Retail Price -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="rrp">Recommended Retail Price</label>
                <div class="col-sm-8">
                  <input
                      id="rrp"
                      v-model.number="newProduct.recommendedRetailPrice"
                      :class="{'form-control': true, 'is-invalid': !priceValid && priceBlur}"
                      maxlength="255"
                      type="text"
                      @blur="priceBlur = true"
                  >
                  <div class="invalid-feedback">The price must be a number</div>
                </div>
              </div>

              <!-- Manufacturer -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="manufacturer">Manufacturer</label>
                <div class="col-sm-8">
                  <input id="manufacturer" v-model="newProduct.manufacturer" class="form-control" maxlength="255"
                         type="text">
                </div>
              </div>

            </form>
          </div>
        </div>

        <!-- Row for fixes message -->
        <div v-if="showFixesMessage && fieldsNeedingFixed" class="row text-center mt-3">
          <div class="col-12 col-sm-8 offset-sm-2 text-danger text-center">
            <p>The following fields need fixed first: {{ fieldsNeedingFixed }}</p>
          </div>
        </div>

        <!-- Row for submit / cancel buttons -->
        <div class="row text-center mt-3 mb-5">
          <div class="col-12 col-sm-8 offset-sm-2">

            <!-- Cancel button when changes are made -->
            <button
                :class="{'btn': true, 'mr-1': true, 'my-1': true, 'btn-danger': this.changesMade,
              'btn-secondary': !this.changesMade, 'float-left': true}"
                type="button"
            >
              Cancel
            </button>

            <!-- Save Changes button -->
            <button
                :disabled="!changesMade"
                class="btn btn-primary ml-1 my-1 float-right"
                type="button"
                @click="submit"
            >
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
import {Business} from "@/Api";

export default {
  name: "EditProductPage",
  mounted() {
    this.loadProduct();
  },
  data() {
    return {
      errorMessage: null,
      showFixesMessage: false,
      loading: true,
      product: null,
      newProduct: null,
      idBlur: false,
      priceBlur: false,
      nameBlur: false
    }
  },
  components: {
    LoginRequired,
    AdminRequired,
    Alert
  },
  computed: {
    /** Checks to see if user is logged in currently **/
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    /** Gets the business ID that is in the current path **/
    businessId() {
      return this.$route.params.businessId;
    },

    /** Gets the product ID that is in the current path **/
    productId() {
      return this.$route.params.productId;
    },

    /** A list of businesses the current user administers **/
    businessesAdministered() {
      if (this.isLoggedIn) {
        return this.$root.$data.user.state.userData.businessesAdministered;
      }
      return []
    },

    /** Checks if the user is an admin of the business **/
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

    /** Returns true if changes have been made to the product **/
    changesMade() {
      if (!this.product) {
        return false
      }
      let allSame = true;
      for (const [key, val] of Object.entries(this.product)) {
        if (this.newProduct[key] !== val && typeof val !== 'object') {
          allSame = false;
        }
      }
      return !allSame
    },

    /** True if the inputted id is valid **/
    idValid() {
      // Regex for numbers, hyphens and letters
      return /^[a-zA-Z0-9-]+$/.test(this.newProduct.id)
    },

    /** True if the inputted name is valid **/
    nameValid() {
      return this.newProduct.name !== ''
    },

    /** True if the inputted price is valid **/
    priceValid() {
      // Regex valid for any number with a max of 2 dp, or empty
      return /^([0-9]+(.[0-9]{0,2})?)?$/.test(this.newProduct.recommendedRetailPrice)
    },

    /** Returns a string list of the fields that aren't valid **/
    fieldsNeedingFixed() {
      let fixes = []
      if (!this.priceValid) {
        fixes.push('Recommended Retail Price')
      }
      if (!this.nameValid) {
        fixes.push('Name')
      }
      if (!this.idValid) {
        fixes.push('Id')
      }
      return fixes.join(', ')
    }
  },
  methods: {
    /**
     * Validates the users inputs, then sends the data to the api.
     */
    submit() {
      console.log('Submitting')
      this.showFixesMessage = false
      if (!this.nameValid || !this.idValid || !this.priceValid) {
        this.showFixesMessage = true

      }
    },

    /**
     * Loads the product data into this.product and this.newProduct
     */
    loadProduct() {
      Business.getProducts(this.businessId)
          .then((res) => {
            this.product = res.data.find(prod => prod.id === this.productId.toString())
            if (!this.product) {
              this.errorMessage = `There is no product with id ${this.productId}.`
            }
            this.newProduct = {...this.product}
            this.loading = false
          })
          .catch((err) => {
            this.errorMessage = err.response.data.message;
            this.loading = false
          })
    }
  }
}
</script>

<style scoped>
.btn {
  transition-duration: 0.1s;
}
</style>