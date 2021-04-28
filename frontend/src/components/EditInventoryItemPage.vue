<template>
  <div>
    <!-- Check if the user is logged in -->
    <login-required
        v-if="!isLoggedIn"
        page="view an individual item"
    />

    <!-- Check if the user is an admin of the business -->
    <admin-required
        v-else-if="!isAdminOfBusiness"
        :page="`of the business ${this.businessId} to edit its item`"
    />

    <div v-else>
      <!-- Title of the page -->
      <div class="text-center mb-4">
        <h1 class="test-center">Edit item</h1>
      </div>

      <!-- Display error message if there is one -->
      <div class="row">
        <div class="col-12 col-sm-8 offset-sm-2">
          <alert v-if="errorMessage">Error: {{ errorMessage }}</alert>
        </div>
      </div>

      <!-- Display when the item is loading -->
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

            <!-- Inventory button -->
            <router-link
                type="button"
                :to="{name: 'InventoryPage', params: {businessId: this.businessId}}"
                class="btn btn-primary float-right"
            >
              Inventory
            </router-link>

          </div>
        </div>
      </div>

      <!-- Edit item div -->
      <div v-else-if="item" class="container-fluid">

        <!-- Row for submit error message -->
        <div class="row" v-if="submitError && !(submitError || '')">
          <div class="col-12 col-sm-8 offset-sm-2">
            <alert>An error occurred when submitting your changes:
              {{ submitError.slice(submitError.indexOf(':') + 2) }}</alert>
          </div>
        </div>

        <!-- Display when the item is loading -->
        <div v-if="submitting">
          <div class="alert alert-info col-12 col-sm-8 offset-sm-2">Submitting changes...</div>
        </div>

        <!-- Row for edit form -->
        <div class="row">
          <div class="col-12 col-sm-8 offset-sm-2">

            <form>
              <!-- Product Code -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="productCode">Product Code<span class="text-danger">*</span></label>
                <div class="col-sm-8">
                  <select
                      id="productCode"
                      v-model="newItem.id"
                      :class="{'form-control': true, 'is-invalid': !productCodeValid && productCodeBlur}"
                      @blur="productCodeBlur= true"
                      >
                    <option v-for="code in productCodes" v-bind:key="code.id">
                      {{code.id}}
                    </option>
                  </select>
                  <div class="invalid-feedback" v-if="productCodeValid">The Product Code is invalid</div>
                </div>
              </div>

              <!-- Quantity -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="quantity"><b>Product Quantity<span class="text-danger">*</span></b></label>
                <div class="col-sm-8">
                  <input
                      id="quantity"
                      v-model="newItem.quantity"
                      :class="{'form-control': true, 'is-invalid': !quantityValid && quantityBlur}"
                      maxlength="255"
                      type="text"
                      @blur="quantityBlur = true"
                  >
                  <div class="invalid-feedback" v-if="quantityValid"> Please enter a valid quantity</div>
                </div>
              </div>

              <!-- Price Per Item -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="pricePerItem"><b>Price Per Item</b></label>
                <div class="col-sm-8" :class="{'input-group': true, 'is-invalid': !pricePerItemValid && pricePerItemBlur}">
                  <div class="input-group-prepend">
                    <span class="input-group-text">{{ this.currencySymbol }}</span>
                  </div>
                  <input id="pricePerItem" v-model="newItem.pricePerItem" :class="{'form-control': true, 'is-invalid': !pricePerItemValid && pricePerItemBlur}"
                         maxlength="255"
                         placeholder="Price Per Item" type="text" @blur="pricePerItemBlur = true">
                  <div class="input-group-append">
                    <span class="input-group-text">{{ this.currencyCode }}</span>
                  </div>
                </div>
                <div class="invalid-feedback" v-if= "pricePerItemValid">Please enter a valid price</div>
              </div>

              <!-- Total Price -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="totalPrice"><b>Total Price</b></label>
                <div class="col-sm-8" :class="{'input-group': true, 'is-invalid': !totalPriceValid && totalPriceBlur}">
                  <div class="input-group-prepend">
                    <span class="input-group-text">{{ this.currencySymbol }}</span>
                  </div>
                  <input id="totalPrice" v-model="newItem.totalPrice" :class="{'form-control': true, 'is-invalid': !totalPriceValid && totalPriceBlur}"
                         maxlength="255"
                         placeholder="Total Price" type="text" @blur="totalPriceBlur = true">
                  <div class="input-group-append">
                    <span class="input-group-text">{{ this.currencyCode }}</span>
                  </div>
                </div>
                <div class="invalid-feedback" v-if= "totalPriceValid">Please enter a valid price</div>
              </div>

              <!-- Manufactured -->
              <div class="form-row">
                <label for="manufactured"><b>Manufactured Date</b></label><br/>
                <input id="manufactured" v-model="newItem.manufactured" :class="{'form-control': true, 'is-invalid': !manufacturedValid && manufacturedBlur}" required style="width:100%"
                       type="date" @blur="manufacturedBlur=true"><br>
                <!--    Error message for the date input    -->
                <span class="invalid-feedback" v-if="manufacturedValid">Please enter a date in the past</span><br><br>
              </div>

              <!-- Sell By -->
              <div class="form-row">
                <label for="sellBy"><b>Sell By Date</b></label><br/>
                <input id="sellBy" v-model="newItem.sellBy" :class="{'form-control': true, 'is-invalid': !sellByValid && sellByBlur}" required style="width:100%"
                       type="date" @blur="sellByBlur=true"><br>
                <!--    Error message for the date input    -->
                <span class="invalid-feedback" v-if="sellByValid">Please enter a date in the future</span><br><br>
              </div>

              <!-- Best Before -->
              <div class="form-row">
                <label for="bestBefore"><b>Best Before Date</b></label><br/>
                <input id="bestBefore" v-model="newItem.bestBefore" :class="{'form-control': true, 'is-invalid': !bestBeforeValid && bestBeforeBlur}" required style="width:100%"
                       type="date" @blur="bestBeforeBlur=true"><br>
                <!--    Error message for the date input    -->
                <span class="invalid-feedback" v-if="bestBeforeValid">Please enter a date in the future</span><br><br>
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
                @click="cancel"
                class="btn mr-1 my-1 btn-secondary float-left"
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
  name: "EditItemPage",
  mounted() {
    this.loadItem();
    this.getCurrency()
    Business.getProducts(this.$route.params.businessId).then((response) => this.getProductIds(response))
  },
  data() {
    return {
      errorMessage: null,
      submitting: false, // True when the changes are being submitted to the api
      success: false, // True when the edits were successful
      submitError: null, // Contains the error message if the submit failed
      showFixesMessage: false,
      loading: true,
      productCodes: [],
      currencySymbol: null,
      currencyCode: null,
      item: null,
      newItem: null,
      productCodeBlur: false, // True when the user has clicked on then off the input field
      quantityBlur: false,
      pricePerItemBlur: false,
      totalPriceBlur: false,
      manufacturedBlur: false,
      sellByBlur: false,
      bestBeforeBlur: false,
      expiryBlur: false
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

    /** Gets the inventory item ID that is in the current path **/
    inventoryItemId() {
      return this.$route.params.inventoryItemId;
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

    /** Returns true if changes have been made to the item **/
    changesMade() {
      if (!this.item) {
        return false
      }
      let allSame = true;
      for (const [key, val] of Object.entries(this.item)) {
        if (this.newItem[key] !== val) {
          allSame = false;
        }
      }
      return !allSame
    },

    /** True if the inputted id is valid **/
    productCodeValid() {
      // Regex for numbers, hyphens and letters
      return /^[a-zA-Z0-9-]+$/.test(this.newItem.productCode)
    },

    /** True if the inputted quantity is valid **/
    quantityValid() {
      let isNotNumber = Number.isNaN(Number(this.newItem.quantity))
      if (isNotNumber || this.newItem.quantity === '') {
        return false
      } else return Number(this.quantity) >= 0;
    },

    /**
     * Validate the product Price Per Item field
     */
    pricePerItemValid() {
      let isNotNumber = Number.isNaN(Number(this.newItem.pricePerItem))
      if (this.newItem.pricePerItem === ''){
        return true
      } else if (isNotNumber){
        return false
      } else return Number(this.newItem.pricePerItem) >= 0;
    },
    /**
     * Validate the product Price Per Item field
     */
    totalPriceValid() {
      let isNotNumber = Number.isNaN(Number(this.newItem.totalPrice))
      if (this.newItem.totalPrice === ''){
        return true
      } else if (isNotNumber){
        return false
      } else return Number(this.newItem.totalPrice) >= 0;

    },
    /**
     * Validate the product Manufactured field
     */
    manufacturedValid() { //TODO: Fix manufactured date in create inventory item to be in the past not future
      if (this.newItem.manufactured !== ''){
        let dateNow = new Date()
        let dateGiven = new Date(this.newItem.manufactured)

        return (dateGiven - dateNow <= 0);
      } else {
        return false
      }
    },
    /**
     * Validate the product Sell By field
     */
    sellByValid() {
      if (this.newItem.sellBy !== ''){
        let dateNow = new Date()
        let dateGiven = new Date(this.newItem.sellBy)
        return dateGiven - dateNow > 0;
      } else {
        return false
      }
    },
    /**
     * Validate the product Sell By field
     */
    bestBeforeValid() {
      if (this.newItem.bestBefore !== ''){
        let dateNow = new Date()
        let dateGiven = new Date(this.newItem.bestBefore)
        return dateGiven - dateNow > 0;
      } else {
        return false
      }
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
     * Get Currency data
     */
    async getCurrency() {
      const country = 'New Zealand'
      const currency = await this.$root.$data.product.getCurrency(country)
      this.currencySymbol = currency.symbol
      this.currencyCode = currency.code
    },
    /**
     * Get all product IDs for the current Business
     */
    getProductIds(response){
      let ids = []
      let n = response.data.length
      for (let i = 0; i < n; i++){
        ids.push({id: response.data[i].id})
      }
      this.productCodes = ids
    },

    /**
     * Validates the users inputs, then sends the data to the api.
     */
    submit () {
      // Set id as blurred in case the id was not unique
      this.Blur = true

      // Check all fields are valid first
      this.showFixesMessage = false
      if (!this.nameValid || !this.idValid || !this.priceValid) {
        this.showFixesMessage = true
        return
      }

      // Submit changes to api
      this.submitting = true;
      Business.editItem(this.businessId, this.inventoryItemId, this.newItem)
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
            this.submitting = false
          })
    },

    /**
     * Cancels the item edit.
     * Will confirm with the user if they want to lose there changes, if they made any.
     */
    cancel () {
      this.$router.push({name: 'InventoryPage', params: {businessId: this.businessId}})
    },

    /**
     * Loads the item data into this.item and this.newItem
     */
    loadItem() {
      Business.getInventory(this.businessId)
          .then((res) => {
            this.item = res.data.find(item => item.id === Number(this.inventoryItemId))
            if (!this.item) {
              this.errorMessage = `There is no item with id ${this.inventoryItemId}.`
            }
            this.newItem = {...this.item}
            this.newItem.id = this.item.product.id
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
      if (this.inventoryItemId !== this.newItem.id) {
        this.$router.push(`/businesses/${this.businessId}/inventory/${this.newItem.id}`)
      }

      // Reset data
      this.submitError = null
      this.success = false
      this.item = null
      this.newItem = null
      this.productCodeBlur = false
      this.quantityBlur = false
      this.pricePerItemBlur = false
      this.totalPriceBlur = false
      this.manufacturedBlur = false
      this.sellByBlur = false
      this.bestBeforeBlur = false
      this.expiryBlur= false
      this.loading = true

      // Reload item
      this.loadItem()
    }
  }
}
</script>

<style scoped>
</style>