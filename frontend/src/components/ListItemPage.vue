<template>
  <div>

    <!-- Displayed if not logged in -->
    <login-required
        v-if="!isLoggedIn"
        page="create a new item for this business"
    />

    <!-- Displayed if not admin of business -->
    <admin-required
        v-else-if="!isAdminOf"
        page="create a new item for this business"
    />

    <!-- Page content -->
    <div v-else class="container-fluid">
      <div class="row justify-content-center">
        <div class="col-10 col-md-8 col-lg-6 col-xl-5">

          <!-- Page title -->
          <div class="row mb-4">
            <div class="col text-center">
              <h2>Create a new listing</h2>
            </div>
          </div>

          <!-- Form fields -->
          <div>
            <!-- Listing ID -->
            <div class="form-group row">
              <label for="inventoryItem"><b>Inventory Item<span class="required">*</span></b></label>
              <select id="inventoryItem" v-model="inventoryItemId"
                      :class="{'form-control': true, 'is-invalid': msg.inventoryItemId}"
                      @change="updateInventoryItem">
                <option v-for="item in availableInventoryItems" v-bind:key="item.id" v-bind:value="item.id">
                  {{item.product.name}} expiring on {{ item.expires }}
                </option>
              </select>
              <span class="invalid-feedback">{{ msg.inventoryItemId }}</span>
            </div>

            <!-- Quantity -->
            <div class="form-group row">
              <label for="quantity"><b>Product Quantity<span class="required">*</span></b></label>
              <input id="quantity" v-model="quantity" :class="{'form-control': true, 'is-invalid': msg.quantity}"
                     maxlength="255" placeholder="Enter the quantity" required type="number">
              <span class="invalid-feedback">{{ msg.quantity }}</span>
            </div>

            <!-- Price -->
            <div class="form-group row">
              <label for="price"><b>Price<span class="required">*</span></b></label>
              <div :class="{'input-group': true, 'is-invalid': msg.price}">
                <div class="input-group-prepend">
                  <span class="input-group-text">{{ this.currencySymbol }}</span>
                </div>
                <input id="price" v-model="price" :class="{'form-control': true, 'is-invalid': msg.price}"
                       maxlength="255"
                       placeholder="Price" type="number">
                <div class="input-group-append">
                  <span class="input-group-text">{{ this.currencyCode }}</span>
                </div>
              </div>
              <span class="invalid-feedback">{{ msg.price }}</span>
            </div>

            <!-- More Info -->
            <div class="form-row">
              <label class="moreInfo" for="moreInfo"><b>More Info:</b></label>
              <textarea id="moreInfo" maxlength="255" v-model="moreInfo"
                        class="form-control" placeholder="Write some additional listing information"
                        style="width: 100%; height: 200px;">
            </textarea>
            </div><br>

            <!-- Closing Date -->
            <div class="form-row">
              <label for="closes"><b>Closing Date<span class="required">*</span></b></label><br/>
              <input id="closes" v-model="closes" :class="{'form-control': true, 'is-invalid': msg.expires}" required style="width:100%"
                     type="date"><br>
              <!--    Error message for the date input    -->
              <span class="invalid-feedback">{{ msg.expires }}</span><br><br>
            </div>


            <!-- Create Listing button -->
            <div class="form-group row">
              <div class="btn-group" style="width: 100%">
                <button class="btn btn-secondary col-4" v-on:click="cancel">Cancel</button>
                <button class="btn btn-primary col-8" v-on:click="checkInputs">Create Listing</button>
              </div>
              <!-- Show an error if required fields are missing -->
              <div class="error-box">
                <alert v-if="msg.errorChecks">{{ msg.errorChecks }}</alert>
              </div>
            </div>
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

/**
 * Default starting parameters
 */
export default {
  name: "ListItemPage",

  mounted() {
    this.getCurrency();
    this.getData();
  },

  components: {
    AdminRequired,
    LoginRequired,
    Alert
  },

  data() {
    return {
      inventoryItems: [],
      listings: [],
      selectedInventoryItem: null,
      inventoryItemId: null, // Required
      quantity: '', // Required
      price: '', // Required
      moreInfo: '',
      closes: '', // Required
      currencySymbol: '',
      currencyCode: '',
      msg: {
        inventoryItemId: null,
        quantity: null,
        price: null,
        moreInfo: null,
        closes: null,
      },
      valid: true
    };
  },

  computed: {
    /**
     * Returns the Business ID
     */
    businessId() {
      return parseInt(this.$route.params.businessId)
    },

    /**
     * Checks to see if the user is logged in.
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },
    /**
     * Checks to see if the user is acting as the current business.
     */
    isAdminOf() {
      if (this.$root.$data.user.state.actingAs.type !== 'business') return false;
      return this.$root.$data.user.state.actingAs.id === this.businessId;
    },
    /**
     * Returns a list of all inventory items that have stock to list
     */
    availableInventoryItems() {
      let items = []
      for (const item of this.inventoryItems) {
        if (this.getMaxQuantity(item) > 0) {
          items.push(item);
        }
      }
      return items;
    }
  },

  methods: {
    /**
     * Cancel creating a new item and go back to inventory
     */
    cancel() {
      this.$router.push({name: "listings", params: {businessId: this.businessId}});
    },

    /**
     * Gets a business's inventory items.
     */
    getData() {
      Business.getInventory(this.businessId)
          .then((res) => {
            this.inventoryItems = res.data;
          });

      Business.getListings(this.businessId)
          .then((res) => {
            this.listings = res.data;
          });
    },

    updateInventoryItem() {
      for (const item of this.inventoryItems) {
        if (item.id === this.inventoryItemId) {
          this.selectedInventoryItem = item;
          this.quantity = this.getMaxQuantity(item);
          if (this.quantity === item.quantity) {
            this.price = item.price
          } else {
            this.updatePrice();
          }
          this.closes = new Date(item.expires).toISOString().slice(0, 10);
          break;
        }
      }
    },

    updatePrice() {
      if (this.selectedInventoryItem.pricePerItem !== null) {
        this.price = this.quantity * this.selectedInventoryItem.pricePerItem
      }
    },

    getMaxQuantity(item) {
      let quantityListed = 0;
      for (const listing of this.listings) {
        if (listing.inventoryItem.id === item.id) {
          quantityListed += listing.quantity;
        }
      }
      return item.quantity - quantityListed;
    },

    /**
     * Checks all inputs are valid
     */
    checkInputs() {
      /*
      this.validateClosingDate();
      this.validateQuantity();
      this.validatePrice();

      if (!this.valid) {
        this.msg.errorChecks = 'Please fix the shown errors and try again';
        console.log(this.msg.errorChecks);
        this.valid = true;
      } else {
        this.msg.errorChecks = null;
        console.log('No errors');
        this.addListing();
      }
      */
    },

    /**
     * Validate the listing Price field
     */
    validatePrice() {
      let isNotNumber = Number.isNaN(Number(this.price))

      if (this.price !== '') {
        if (isNotNumber) {
          this.msg.price = 'Please enter a valid price';
          this.valid = false;
        } else if (Number(this.price) < 0) {
          this.msg.price = 'Please enter a non negative price'
          this.valid = false
        }
      } else {
        this.msg.price = null;
      }
    },

    /**
     * Validate the listing Quantity field
     */
    validateQuantity() {
      let isNotNumber = Number.isNaN(Number(this.quantity))
      if (isNotNumber || this.quantity === '') {
        this.msg.quantity = 'Please enter a valid quantity';
        this.valid = false;
      } else if (Number(this.quantity) <= 0) {
        this.msg.quantity = 'Please enter a quantity above 0'
        this.valid = false
      } else {
        this.msg.quantity = null;
      }
    },

    /**
     * Validate the listing Expiry field
     */
    validateClosingDate() {
      if (this.closes !== '') {
        let dateNow = new Date()
        let dateGiven = new Date(this.expires)

        if ((dateGiven - dateNow <= 0)) {
          this.msg.closes = 'Please enter a date in the future'
          this.valid = false
        } else {
          this.msg.closes = null
        }
      } else {
        this.msg.closes = 'Please enter a date'
        this.valid = false
      }

    },

    /**
     * Add a new listing to the Business' listings
     */
    addListing() {
      /*
      //const rrp = Number(this.recommendedRetailPrice)
      const ppi = Number(this.pricePerItem)
      const tp = Number(this.totalPrice)
      this.$root.$data.business.createItem(
          this.$root.$data.user.state.actingAs.id, {
            "productId": this.productCode,
            "quantity": this.quantity,
            "pricePerItem": this.pricePerItem !== '' ? this.roundPrice(ppi) : null,
            "totalPrice": this.totalPrice !== '' ? this.roundPrice(tp) : null,
            "manufactured": this.manufactured,
            "sellBy": this.sellBy,
            "bestBefore": this.bestBefore,
            "expires": this.expires
          }
      ).then(() => {
        this.$router.push({name: "InventoryPage", params: {businessId: this.$root.$data.user.state.actingAs.id}});
      }).catch((err) => {
        this.msg.errorChecks = err.response ?
            err.response.data.slice(err.response.data.indexOf(':') + 2) :
            err
      });
       */

    },

    /**
     * Retrieves the Business' currency
     */
    async getCurrency() {
      const country = (await Business.getBusinessData(this.businessId)).data.address.country;
      const currency = await this.$root.$data.product.getCurrency(country)
      this.currencySymbol = currency.symbol
      this.currencyCode = currency.code
    }
  }
}
</script>

<style scoped>

.required {
  color: red;
}

.form-group {
  margin-bottom: 30px;
}

.error-box {
  width: 100%;
  margin: 20px;
  text-align: center;
}

</style>