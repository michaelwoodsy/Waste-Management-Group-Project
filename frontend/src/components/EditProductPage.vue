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
      <div v-if="loading" class="text-center">
        <p class="text-muted">Loading...</p>
      </div>

      <!-- Div to display when the changes are successful -->
      <div v-else-if="success" class="container-fluid">

        <!-- Row for success message -->
        <div class="row">
          <div class="col-12 col-sm-8 offset-sm-2">
            <div class="alert alert-success">Successfully saved changes!</div>

            <!-- Make more changes button -->
            <button
                type="button"
                class="btn btn-secondary float-left"
                @click="resetPage"
            >
              Edit Again
            </button>

            <!-- Product catalogue button -->
            <router-link
                type="button"
                :to="{name: 'viewCatalogue', params: {businessId: this.businessId}}"
                class="btn btn-primary float-right"
            >
              Product Catalogue
            </router-link>

          </div>
        </div>
      </div>

      <!-- Edit product div -->
      <div v-else-if="product" class="container-fluid">

        <!-- Row for submit error message -->
        <div class="row" v-if="submitError && !(submitError || '').includes('ProductIdAlreadyExistsException')">
          <div class="col-12 col-sm-8 offset-sm-2">
            <alert>An error occurred when submitting your changes:
              {{ submitError.slice(submitError.indexOf(':') + 2) }}</alert>
          </div>
        </div>

        <!-- Display when the product is loading -->
        <div v-if="submitting">
          <div class="alert alert-info col-12 col-sm-8 offset-sm-2">Submitting changes...</div>
        </div>

        <!-- Row for edit form -->
        <div class="row">
          <div class="col-12 col-sm-8 offset-sm-2">

            <form>
              <!-- ID -->
              <div class="form-group row">
                <label for="id" class="col-sm-4 col-form-label">ID<span class="text-danger">*</span></label>
                <div class="col-sm-8">
                  <input
                      type="text" maxlength="255"
                      :class="{'form-control': 1, 'is-invalid': !idValid && idBlur}"
                      id="id"
                      v-model="newProduct.id"
                      @blur="idBlur = true"
                  >
                  <div class="invalid-feedback" v-if="idTaken">The ID must be unique</div>
                  <div class="invalid-feedback" v-else>The ID must be unique, and can only contain letters, numbers, hyphens
                    and must be at least 1 character long.</div>
                </div>
              </div>

              <!-- Name -->
              <div class="form-group row">
                <label for="name" class="col-sm-4 col-form-label">Name<span class="text-danger">*</span></label>
                <div class="col-sm-8">
                  <input
                      type="text"
                      maxlength="255"
                      :class="{'form-control': true, 'is-invalid': !nameValid && nameBlur}"
                      id="name"
                      v-model="newProduct.name"
                      @blur="nameBlur = true"
                  >
                  <div class="invalid-feedback">A name is required</div>
                </div>
              </div>

              <!-- Description -->
              <div class="form-group row">
                <label for="description" class="col-sm-4 col-form-label">Description</label>
                <div class="col-sm-8">
                  <textarea type="text" maxlength="255" class="form-control" id="description" rows="3" v-model="newProduct.description"></textarea>
                </div>
              </div>

              <!-- Recommended Retail Price -->
              <div class="form-group row">
                <label for="rrp" class="col-sm-4 col-form-label">Recommended Retail Price</label>
                <div class="col-sm-8">
                  <input
                      type="text"
                      maxlength="255"
                      :class="{'form-control': true, 'is-invalid': !priceValid && priceBlur}"
                      id="rrp"
                      v-model.number="newProduct.recommendedRetailPrice"
                      @blur="priceBlur = true"
                  >
                  <div class="invalid-feedback">The price must be a number</div>
                </div>
              </div>

              <!-- Manufacturer -->
              <div class="form-group row">
                <label for="manufacturer" class="col-sm-4 col-form-label">Manufacturer</label>
                <div class="col-sm-8">
                  <input type="text" maxlength="255" class="form-control" id="manufacturer" v-model="newProduct.manufacturer">
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
                type="button"
                @click="cancel"
                :class="{'btn': true, 'mr-1': true, 'my-1': true, 'btn-danger': this.changesMade,
              'btn-secondary': !this.changesMade, 'float-left': true}"
            >
              Cancel
            </button>

            <!-- Save Changes button -->
            <button
                type="button"
                class="btn btn-primary ml-1 my-1 float-right"
                :disabled="!changesMade"
                @click="submit"
            >
              Save Changes
            </button>

          </div>
        </div>
      </div>

    </div>

    <!-- Cancel Modal -->
    <transition name="fade">
      <div v-if="showModal">
          <div class="modal-mask">
            <div class="modal-wrapper">
              <div class="modal-dialog" role="document">
                <div class="modal-content">

                  <!-- Modal Header -->
                  <div class="modal-header">
                    <h5 class="modal-title">Cancel Edit?</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                      <span aria-hidden="true" @click="showModal = false">&times;</span>
                    </button>
                  </div>

                  <!-- Modal Body -->
                  <div class="modal-body">
                    <p>Do you really want to cancel? Your changes will be lost.</p>
                  </div>

                  <!-- Modal Footer -->
                  <div class="modal-footer">
                    <button type="button" class="btn btn-danger" @click="cancel">Discard Changes</button>
                    <button type="button" class="btn btn-primary" @click="showModal = false">Continue Editing</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
      </div>
    </transition>

  </div>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import AdminRequired from "@/components/AdminRequired";
import Alert from "@/components/Alert";
import {Business} from "@/Api";

export default {
  name: "EditProductPage",
  mounted () {
    this.loadProduct();
  },
  data () {
    return {
      errorMessage: null,
      submitting: false, // True when the changes are being submitted to the api
      success: false, // True when the edits were successful
      submitError: null, // Contains the error message if the submit failed
      showFixesMessage: false,
      loading: true,
      product: null,
      newProduct: null,
      idBlur: false, // True when the user has clicked on then off the input field
      priceBlur: false,
      nameBlur: false,
      showModal: false, // Whether or not to show the cancel modal
      triedIds: [] // List of ids tested for uniqueness
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
      if (!this.product) { return false }
      let allSame = true;
      for (const [key, val] of Object.entries(this.product)) {
        if (this.newProduct[key] !== val && typeof val !== 'object') {
          allSame = false;
        }
      }
      return !allSame
    },

    /** True if the current ID is taken **/
    idTaken() {
      return this.triedIds.includes(this.newProduct.id)
    },

    /** True if the inputted id is valid **/
    idValid() {
      // Regex for numbers, hyphens and letters
      return /^[a-zA-Z0-9-]+$/.test(this.newProduct.id) && !this.idTaken
    },

    /** True if the inputted name is valid **/
    nameValid() {
      return this.newProduct.name !== ''
    },

    /** True if the inputted price is valid **/
    priceValid() {
      // Regex valid for any number with a max of 2 dp, or empty
      if (this.newProduct.recommendedRetailPrice == null) {return true}
      return /^([0-9]+(.[0-9]{0,2})?)?$/.test(this.newProduct.recommendedRetailPrice)
    },

    /** Returns a string list of the fields that aren't valid **/
    fieldsNeedingFixed() {
      let fixes = []
      if (!this.priceValid) { fixes.push('Recommended Retail Price') }
      if (!this.nameValid) { fixes.push('Name') }
      if (!this.idValid) { fixes.push('Id') }
      return fixes.join(', ')
    }
  },
  methods: {
    /**
     * Validates the users inputs, then sends the data to the api.
     */
    submit () {
      // Set id as blurred in case the id was not unique
      this.idBlur = true

      // Check all fields are valid first
      this.showFixesMessage = false
      if (!this.nameValid || !this.idValid || !this.priceValid) {
        this.showFixesMessage = true
        return
      }

      // Set the rrp to null if its an empty string
      if (this.newProduct.recommendedRetailPrice === '') {
        this.newProduct.recommendedRetailPrice = null
      }

      // Submit changes to api
      this.submitting = true;
      Business.editProduct(this.businessId, this.productId, this.newProduct)
          .then(() => {
            this.submitError = null
            this.success = true
            this.submitting = false
          })
          .catch((err) => {
            // Display the response error message if there is one
            this.submitError = err.response.data
                ? err.response.data
                : err
            // Check if the id is taken
            if ((this.submitError || '').includes('ProductIdAlreadyExistsException')) {
              this.triedIds.push(this.newProduct.id)
            }
            this.submitting = false
          })
    },

    /**
     * Cancels the product edit.
     * Will confirm with the user if they want to lose there changes, if they made any.
     */
    cancel () {
      // Check if changes are made and the modal isn't shown
      if (this.changesMade && !this.showModal) {
        this.showModal = true
        return
      }
      this.$router.push({name: 'viewCatalogue', params: {businessId: this.businessId}})
    },

    /**
     * Loads the product data into this.product and this.newProduct
     */
    loadProduct () {
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
            this.errorMessage = err.response.data.message || err;
            this.loading = false
          })
    },

    /**
     * Resets the page after submitting changes, so the user can make more changes.
     */
    resetPage () {
      if (this.productId !== this.newProduct.id) {
        this.$router.push(`/businesses/${this.businessId}/products/${this.newProduct.id}`)
      }

      // Reset data
      this.submitError = null
      this.success = false
      this.product = null
      this.newProduct = null
      this.idBlur = false
      this.nameBlur = false
      this.priceBlur = false
      this.loading = true

      // Reload product
      this.loadProduct()
    }
  }
}
</script>

<style scoped>
.btn {
  transition-duration: 0.1s;
}

.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, .5);
  display: table;
  transition: opacity .3s ease;
}

.modal-wrapper {
  display: table-cell;
  vertical-align: middle;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity .3s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
}
</style>