<template>
  <page-wrapper>

    <!-- Check if the user is logged in -->
    <login-required
        v-if="!isLoggedIn"
        page="view an individual item"
    />

    <!-- Check if the user is an admin of the business -->
    <admin-required
        v-else-if="!isAdminOf"
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
                class="btn btn-secondary float-left"
                type="button"
                @click="resetPage"
            >
              Edit Again
            </button>

            <!-- Inventory button -->
            <router-link
                :to="{name: 'InventoryPage', params: {businessId: this.businessId}}"
                class="btn btn-primary float-right"
                type="button"
            >
              Inventory
            </router-link>

          </div>
        </div>
      </div>

      <!-- Edit item div -->
      <div v-else-if="item" class="container-fluid">

        <!-- Row for submit error message -->
        <div v-if="submitError" class="row">
          <div class="col-12 col-sm-8 offset-sm-2">
            <alert>An error occurred when submitting your changes:
              {{ submitError.slice(submitError.indexOf(':') + 2) }}
            </alert>
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
                <label class="col-sm-4 col-form-label" for="productCode"><strong>Product Code<span
                    class="text-danger">*</span></strong></label>
                <div class="col-sm-8">
                  <select
                      id="productCode"
                      v-model="newItem.product.id"
                      :class="{'form-control': true, 'is-invalid': !productCodeValid && productCodeBlur}"
                      @blur="productCodeBlur= true"
                  >
                    <option v-for="code in productCodes" v-bind:key="code.id">
                      {{ code.id }}
                    </option>
                  </select>
                  <div v-if="!productCodeValid" class="invalid-feedback">The Product Code is invalid</div>
                </div>
              </div>

              <!-- Quantity -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="quantity"><strong>Product Quantity<span
                    class="text-danger">*</span></strong></label>
                <div class="input-group col-sm-8">
                  <input
                      id="quantity"
                      v-model="newItem.quantity"
                      :class="{'form-control': true, 'is-invalid': !quantityValid && quantityBlur}"
                      maxlength="10"
                      required
                      type="text"
                      @blur="quantityBlur = true"
                  >
                  <span v-if="this.getMinQuantity() !== 0" class="input-group-text">Min Quantity: {{
                      this.getMinQuantity()
                    }}</span>
                  <div v-if="!quantityValid" class="invalid-feedback"> Please enter a valid quantity</div>
                </div>
              </div>

              <!-- Price Per Item -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="pricePerItem"><strong>Price Per Item<span
                    class="text-danger"></span></strong></label>
                <div :class="{'input-group': true, 'is-invalid': !pricePerItemValid && pricePerItemBlur}"
                     class="col-sm-8">
                  <div class="input-group-prepend">
                    <span class="input-group-text">{{ this.currencySymbol }}</span>
                  </div>
                  <input id="pricePerItem" v-model="newItem.pricePerItem"
                         :class="{'form-control': true, 'is-invalid': !pricePerItemValid && pricePerItemBlur}"
                         maxlength="10"
                         placeholder="Price Per Item"
                         type="text"
                         @blur="pricePerItemBlur = true">
                  <div class="input-group-append">
                    <span class="input-group-text">{{ this.currencyCode }}</span>
                  </div>
                  <div v-if="!pricePerItemValid" class="invalid-feedback">Please enter a valid price</div>
                </div>

              </div>

              <!-- Total Price -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="totalPrice"><strong>Total Price</strong></label>
                <div :class="{'input-group': true, 'is-invalid': !totalPriceValid && totalPriceBlur}" class="col-sm-8">
                  <div class="input-group-prepend">
                    <span class="input-group-text">{{ this.currencySymbol }}</span>
                  </div>
                  <input id="totalPrice" v-model="newItem.totalPrice"
                         :class="{'form-control': true, 'is-invalid': !totalPriceValid && totalPriceBlur}"
                         maxlength="10"
                         placeholder="Total Price" type="text" @blur="totalPriceBlur = true">
                  <div class="input-group-append">
                    <span class="input-group-text">{{ this.currencyCode }}</span>
                  </div>
                  <div v-if="!totalPriceValid" class="invalid-feedback">Please enter a valid price</div>
                </div>

              </div>

              <!-- Manufactured -->
              <div class="form-row">
                <label for="manufactured"><strong>Manufactured Date</strong></label><br/>
                <input id="manufactured" v-model="newItem.manufactured"
                       :class="{'form-control': true, 'is-invalid': !manufacturedValid && manufacturedBlur}" required
                       style="width:100%"
                       type="date" @blur="manufacturedBlur=true"><br>
                <!--    Error message for the date input    -->
                <span v-if="!manufacturedValid" class="invalid-feedback">Please enter a date in the past</span><br><br>
              </div>

              <!-- Sell By -->
              <div class="form-row">
                <label for="sellBy"><strong>Sell By Date</strong></label><br/>
                <input id="sellBy" v-model="newItem.sellBy"
                       :class="{'form-control': true, 'is-invalid': !sellByValid && sellByBlur}" required
                       style="width:100%"
                       type="date" @blur="sellByBlur=true"><br>
                <!--    Error message for the date input    -->
                <span v-if="!sellByValid" class="invalid-feedback">Please enter a date in the future</span><br><br>
              </div>

              <!-- Best Before -->
              <div class="form-row">
                <label for="bestBefore"><strong>Best Before Date</strong></label><br/>
                <input id="bestBefore" v-model="newItem.bestBefore"
                       :class="{'form-control': true, 'is-invalid': !bestBeforeValid && bestBeforeBlur}" required
                       style="width:100%"
                       type="date" @blur="bestBeforeBlur=true"><br>
                <!--    Error message for the date input    -->
                <span v-if="!bestBeforeValid" class="invalid-feedback">Please enter a date in the future</span><br><br>
              </div>

              <!-- Expiry -->
              <div class="form-row">
                <label for="expiry"><strong>Expiry Date<span class="text-danger">*</span></strong></label><br/>
                <input id="expiry" v-model="newItem.expires"
                       :class="{'form-control': true, 'is-invalid': !expiryValid && expiryBlur}" required
                       style="width:100%"
                       type="date" @blur="expiryBlur=true"><br>
                <!--    Error message for the date input    -->
                <span v-if="!expiryValid" class="invalid-feedback">Please enter a date in the future</span><br><br>
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
                class="btn mr-1 my-1 btn-secondary float-left"
                type="button"
                @click="cancel"
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

  </page-wrapper>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import AdminRequired from "@/components/AdminRequired";
import Alert from "@/components/Alert";
import {Business} from "@/Api";
import PageWrapper from "@/components/PageWrapper";

export default {
  name: "EditItemPage",
  mounted() {
    this.getListings()
    this.loadItem()
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
      expiryBlur: false,
      businessesListings: [],

    }
  },

  components: {
    PageWrapper,
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

    /**
     * Currently acting as user/business
     */
    actor() {
      return this.$root.$data.user.state.actingAs;
    },

    /**
     * Check if the user is an admin of the business and is acting as that business
     */
    isAdminOf() {
      if (this.$root.$data.user.canDoAdminAction()) return true
      else if (this.actor.type !== "business") return false
      return this.actor.id === parseInt(this.$route.params.businessId);
    },

    /** Returns true if changes have been made to the item **/
    changesMade() {
      if (!this.item) {
        return false
      }
      let allSame = true;
      if (this.originalProduct !== this.newItem.product.id) {
        allSame = false
      }

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
      return /^[a-zA-Z0-9-]+$/.test(this.newItem.product.id)
    },

    /** True if the inputted quantity is valid **/
    quantityValid() {
      // Regex valid for any non negative integer under 2147483647
      let isNotANumber = Number.isNaN(Number(this.newItem.quantity))
      if (this.newItem.quantity === null || this.newItem.quantity === '' || isNotANumber) {
        return false
      }
      //If quantity entered is less than the quantity used in sales listings
      if (this.newItem.quantity < this.getMinQuantity()) return false

      //32 bit highest number
      if (this.newItem.quantity > 2147483647) {
        return false
      }
      return /^([0-9]+([0-9]{0,2})?)?$/.test(this.newItem.quantity)
    },

    /**
     * Validate the product Price Per Item field
     */
    pricePerItemValid() {
      // Regex valid for any non negative number with a max of 2 dp, or empty
      let isNotANumber = Number.isNaN(Number(this.newItem.pricePerItem))
      if (this.newItem.pricePerItem === null || this.newItem.pricePerItem === '') {
        return true
      }
      if (isNotANumber) {
        return false
      }
      return /^([0-9]+(.[0-9]{0,2})?)?$/.test(this.newItem.pricePerItem)
    },
    /**
     * Validate the product Price Per Item field
     */
    totalPriceValid() {
      // Regex valid for any non negative number with a max of 2 dp, or empty
      let isNotANumber = Number.isNaN(Number(this.newItem.totalPrice))
      if (this.newItem.totalPrice === null || this.newItem.totalPrice === '') {
        return true
      }
      if (isNotANumber) {
        return false
      }
      return /^([0-9]+(.[0-9]{0,2})?)?$/.test(this.newItem.totalPrice)
    },

    /**
     * Validate the product Manufactured field
     */
    manufacturedValid() {
      if (this.newItem.manufactured !== '' && this.newItem.manufactured !== null) {
        let dateNow = new Date()
        let dateGiven = new Date(this.newItem.manufactured)

        return (dateGiven - dateNow <= 0);
      } else {
        return true
      }
    },

    /**
     * Validate the product Sell By field
     */
    sellByValid() {
      if (this.newItem.sellBy !== '' && this.newItem.sellBy !== null) {
        let dateNow = new Date()
        let dateGiven = new Date(this.newItem.sellBy)
        return dateGiven - dateNow > 0;
      } else {
        return true
      }
    },

    /**
     * Validate the product Sell By field
     */
    bestBeforeValid() {
      if (this.newItem.bestBefore !== '' && this.newItem.bestBefore !== null) {
        let dateNow = new Date()
        let dateGiven = new Date(this.newItem.bestBefore)
        return dateGiven - dateNow > 0;
      } else {
        return true
      }
    },

    /**
     * Validate the product Sell By field
     */
    expiryValid() {
      if (this.newItem.expires !== '') {
        let dateNow = new Date()
        let dateGiven = new Date(this.newItem.expires)
        return dateGiven - dateNow > 0;
      } else {
        return false
      }
    },

    /** Returns a string list of the fields that aren't valid **/
    fieldsNeedingFixed() {
      let fixes = []
      if (!this.productCodeValid) {
        fixes.push('Product Code')
      }
      if (!this.quantityValid) {
        fixes.push('Quantity')
      }
      if (!this.pricePerItemValid) {
        fixes.push('Price Per Item')
      }
      if (!this.totalPriceValid) {
        fixes.push('Total Price')
      }
      if (!this.manufacturedValid) {
        fixes.push('Manufactured Date')
      }
      if (!this.sellByValid) {
        fixes.push('Sell By Date')
      }
      if (!this.bestBeforeValid) {
        fixes.push('Best Before Date')
      }
      if (!this.expiryValid) {
        fixes.push('Expiry Date')
      }
      return fixes.join(', ')
    },
  },

  methods: {
    /**
     * Get all sales listings for the current Business
     * Used to validate quantity editing
     */
    getListings() {
      Business.getListings(this.businessId)
          .then((res) => {
            this.businessesListings = res.data;
          });
    },

    /**
     * Retrieves the highest number of available items of a specific
     * inventory item type are available to list for sale.
     */
    getMinQuantity() {
      if (!this.item) {
        return 0;
      } else {
        let quantityListed = 0;
        for (const listing of this.businessesListings) {
          if (listing.inventoryItem.id === this.item.id) {
            quantityListed += listing.quantity;
          }
        }
        return quantityListed;
      }
    },

    /**
     * Get Currency data
     */
    async getCurrency() {
      const country = (await Business.getBusinessData(parseInt(this.$route.params.businessId))).data.address.country
      const currency = await this.$root.$data.product.getCurrency(country)
      this.currencySymbol = currency.symbol
      this.currencyCode = currency.code
    },

    /**
     * Get all product IDs for the current Business
     */
    getProductIds(response) {
      let ids = []
      let n = response.data.length
      for (let i = 0; i < n; i++) {
        ids.push({id: response.data[i].id})
      }
      this.productCodes = ids
    },

    /**
     * Validates the users inputs, then sends the data to the api.
     */
    submit() {
      // Check all fields are valid first
      this.showFixesMessage = false
      if (!this.productCodeValid || !this.quantityValid || !this.pricePerItemValid || !this.totalPriceValid || !this.manufacturedValid || !this.sellByValid || !this.bestBeforeValid || !this.expiryValid) {
        this.showFixesMessage = true
        return
      }
      // Submit changes to api
      this.submitting = true;
      let item = {
        productId: this.newItem.product.id,
        quantity: Number(this.newItem.quantity),
        "pricePerItem": this.newItem.pricePerItem !== null && this.newItem.pricePerItem !== ''
            ? Number(this.newItem.pricePerItem) : null,
        "totalPrice": this.newItem.totalPrice !== null && this.newItem.totalPrice !== ''
            ? Number(this.newItem.totalPrice) : null,
        manufactured: this.newItem.manufactured,
        sellBy: this.newItem.sellBy,
        bestBefore: this.newItem.bestBefore,
        expires: this.newItem.expires
      }

      Business.editItem(this.businessId, this.inventoryItemId, item)
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
    cancel() {
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
            this.originalProduct = this.item.product.id
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
    resetPage() {
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
      this.expiryBlur = false
      this.loading = true

      // Reload item
      this.loadItem()
    }
  }
}
</script>
