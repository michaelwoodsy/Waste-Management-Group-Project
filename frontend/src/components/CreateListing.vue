<template>
  <div class="container-fluid">

    <!-- Page title -->
    <div class="row mb-4">
      <div v-if="selectingItem" class="col-3 text-left">
        <button class="btn btn-secondary" @click="finishSelectItem">Back</button>
      </div>
      <div class="col text-center">
        <h2>{{ title }}</h2>
      </div>
      <div v-if="selectingItem" class="col-3"/>
    </div>

    <!-- Form fields -->
    <div v-if="!selectingItem">
      <!-- Listing ID -->
      <div class="form-group row">
        <label for="inventoryItem"><b>Inventory Item<span class="required">*</span></b></label>
        <div :class="{'input-group': true, 'is-invalid': msg.inventoryItemId}">
          <input id="inventoryItem" :class="{'form-control': true, 'is-invalid': msg.inventoryItemId}"
                 :value="formatInventoryItem()" placeholder="Select a product from inventory..." readonly>
          <div class="input-group-append">
            <button class="btn btn-primary" @click="selectItem">Select</button>
          </div>
        </div>
        <span class="invalid-feedback">{{ msg.inventoryItemId }}</span>
      </div>

      <!-- Quantity -->
      <div class="form-group row">
        <label for="quantity"><b>Product Quantity<span class="required">*</span></b></label>
        <div :class="{'input-group': true, 'is-invalid': msg.quantity}">
          <input id="quantity" v-model="quantity" :class="{'form-control': true, 'is-invalid': msg.quantity}"
                 :disabled="!selectedInventoryItem" :maxlength="10"
                 placeholder="Enter the quantity" required type="text" @change="updatePrice">
          <div v-if="this.selectedInventoryItem !== null" class="input-group-append">
                  <span class="input-group-text">Max Quantity: {{
                      this.getMaxQuantity(this.selectedInventoryItem)
                    }}</span>
          </div>
        </div>
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
                 :disabled="!selectedInventoryItem" maxlength="10" placeholder="Price" type="text">
          <div class="input-group-append">
            <span class="input-group-text">{{ this.currencyCode }}</span>
          </div>
        </div>
        <span class="invalid-feedback">{{ msg.price }}</span>
      </div>

      <!-- More Info -->
      <div class="form-group row">
        <label class="moreInfo" for="moreInfo"><b>More Info</b></label>
        <textarea id="moreInfo" v-model="moreInfo" :disabled="!selectedInventoryItem" class="form-control"
                  maxlength="255" placeholder="Write some additional listing information" style="width: 100%">
            </textarea>
      </div>

      <!-- Closing Date -->
      <div class="form-group row">
        <label for="closes"><b>Closing Date<span class="required">*</span></b></label>
        <input id="closes" v-model="closes" :class="{'form-control': true, 'is-invalid': msg.closes}"
               :disabled="!selectedInventoryItem" required style="width:100%" type="date">
        <!--    Error message for the date input    -->
        <span class="invalid-feedback">{{ msg.closes }}</span>
      </div>

      <!-- Create Listing button -->
      <div class="form-group row mb-0">
        <div class="btn-group" style="width: 100%">
          <button ref="close" class="btn btn-secondary col-4" data-dismiss="modal" @click="close">Cancel</button>
          <button class="btn btn-primary col-8" v-on:click="checkInputs">Create Listing</button>
        </div>
        <!-- Show an error if required fields are missing -->
        <div v-if="msg.errorChecks" class="error-box">
          <alert class="mb-0">{{ msg.errorChecks }}</alert>
        </div>
      </div>
    </div>

    <inventory-items v-if="selectingItem" :business-id="this.businessId" :selecting-item="true"></inventory-items>

  </div>
</template>

<script>
import Alert from "@/components/Alert";
import {Business} from "@/Api";
import InventoryItems from "@/components/InventoryItems";

/**
 * Default starting parameters
 */
export default {
  name: "CreateListing",

  mounted() {
    this.getCurrency();
    this.getData();
  },

  components: {
    InventoryItems,
    Alert
  },

  data() {
    return {
      title: 'Create a new sale listing',
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
        errorChecks: null
      },
      valid: true,
      selectingItem: false
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

    /**
     *  When an inventory item is selected in the dropdown for sale,
     *  the inventory item's details are autofilled into the fields such as
     *  quantity, price (if applicable) and expiry date.
     *  This is called in InventoryItems.vue
     */
    updateInventoryItem() {
      for (const item of this.inventoryItems) {
        if (item.id === this.inventoryItemId) {
          this.selectedInventoryItem = item;
          this.quantity = this.getMaxQuantity(item);
          if (this.quantity === item.quantity && item.totalPrice !== null) {
            this.price = item.totalPrice
          } else {
            this.updatePrice();
          }
          this.closes = new Date(item.expires).toISOString().slice(0, 10);
          break;
        }
      }
    },

    /**
     * If an inventory item has a price per item, the price field
     * in the listing is updated to the price x quantity available for sale
     */
    updatePrice() {
      if (this.selectedInventoryItem.pricePerItem !== null) {
        this.price = (Math.round((this.quantity * this.selectedInventoryItem.pricePerItem) * 100) / 100).toFixed(2);
      }
    },

    /**
     * Retrieves the highest number of available items of a specific
     * inventory item type are available to list for sale.
     */
    getMaxQuantity(item) {
      if (!item) {
        return 0;
      } else {
        let quantityListed = 0;
        for (const listing of this.listings) {
          if (listing.inventoryItem.id === item.id) {
            quantityListed += listing.quantity;
          }
        }
        return item.quantity - quantityListed;
      }
    },

    formatInventoryItem() {
      if (this.selectedInventoryItem === null) {
        return '';
      } else {
        return `${this.selectedInventoryItem.product.name} expiring on ${new Date(this.selectedInventoryItem.expires).toDateString()}`;
      }
    },

    /**
     * Checks all inputs are valid
     */
    checkInputs() {
      this.validateInventoryItem();
      this.validateQuantity();
      this.validatePrice();
      this.validateClosingDate();

      if (!this.valid) {
        this.msg.errorChecks = 'Please fix the shown errors and try again';
        console.log(this.msg.errorChecks);
        this.valid = true;
      } else {
        this.msg.errorChecks = null;
        console.log('No errors');
        this.addListing();
      }
    },

    /**
     * Checks whether an inventory item has been selected to list
     */
    validateInventoryItem() {
      if (!this.selectedInventoryItem) {
        this.msg.inventoryItemId = 'Please select an inventory item'
        this.valid = false;
      } else {
        this.msg.inventoryItemId = null;
      }
    },

    /**
     * Validate the listing Price field
     */
    validatePrice() {
      let isNotNumber = Number.isNaN(Number(this.price))

      if (isNotNumber
          || this.price === ''
          || this.price === null
          || !/^([0-9]+(.[0-9]{0,2})?)?$/.test(this.price)) {
        this.msg.price = 'Please enter a valid price';
        this.valid = false;
      } else if (Number(this.price) < 0) {
        this.msg.price = 'Please enter a non negative price'
        this.valid = false
      } else {
        this.msg.price = null;
      }
    },

    /**
     * Validate the listing Quantity field
     */
    validateQuantity() {
      let isNotNumber = Number.isNaN(Number(this.quantity))
      if (isNotNumber
          || this.quantity === ''
          || this.quantity === null
          || Number(this.quantity) > 2147483647
          || !/^([1-9]+([1-9]{0,2})?)?$/.test(this.quantity)) {
        this.msg.quantity = 'Please enter a valid quantity';
        this.valid = false;
      } else if (Number(this.quantity) <= 0) {
        this.msg.quantity = 'Please enter a quantity above 0'
        this.valid = false
      } else if (Number(this.quantity) > this.getMaxQuantity(this.selectedInventoryItem)) {
        this.msg.quantity = 'Please enter a valid quantity';
        this.valid = false;
      } else {
        this.msg.quantity = null;
      }
    },

    /**
     * Validate the listing Expiry field
     */
    validateClosingDate() {
      if (this.closes !== '') {
        let dateGiven = new Date(this.closes)
        let dateNow = new Date()
        let dateExpires = new Date(this.selectedInventoryItem.expires)

        if (dateGiven < dateNow) {
          this.msg.closes = 'Please enter a date in the future'
          this.valid = false
        } else if (dateGiven > dateExpires) {
          this.msg.closes = 'Please enter a date before the expiry date'
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
      this.$root.$data.business.createListing(
          this.businessId, {
            "inventoryItemId": this.inventoryItemId,
            "quantity": Number(this.quantity),
            "price": Number(this.price),
            "moreInfo": this.moreInfo,
            "closes": new Date(this.closes),
          }
      ).then(() => {
        this.$refs.close.click();
        this.close();
      }).catch((err) => {
        this.msg.errorChecks = err.response ?
            err.response.data.slice(err.response.data.indexOf(':') + 2) :
            err
      });

    },

    /**
     * Retrieves the Business' currency
     */
    async getCurrency() {
      const country = (await Business.getBusinessData(this.businessId)).data.address.country;
      const currency = await this.$root.$data.product.getCurrency(country)
      this.currencySymbol = currency.symbol
      this.currencyCode = currency.code
    },

    /**
     * Close the create listing dialogue and refresh listings.
     */
    close() {
      this.$emit('refresh-listings');
    },
    selectItem() {
      this.selectingItem = true;
      this.title = 'Select a product from your inventory'
      this.$parent.$refs.createListingWindow.classList.add('modal-xl');
    },
    finishSelectItem() {
      this.selectingItem = false;
      this.title = 'Create a new sale listing'
      this.$parent.$refs.createListingWindow.classList.remove('modal-xl');
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