<template>
  <div class="container-fluid">

    <!-- Page title -->
    <div class="row mb-4">
      <div v-if="selectingItem" class="col-2 text-left">
        <button class="btn btn-secondary" @click="cancelSelectItem">Back</button>
      </div>
      <div class="col text-center">
        <h2>{{ title }}</h2>
      </div>
      <div v-if="selectingItem" class="col-2"/>

    </div>



    <!-- Form fields -->
    <div v-if="!selectingItem">
      <!-- Product Code -->
      <div class="form-group row">
        <label for="productCode"><strong>Product ID<span class="required">*</span></strong></label>
        <div :class="{'input-group': true, 'is-invalid': msg.productId}">
          <div id="productCode" style="cursor: default" readonly :class="{'form-control': true, 'is-invalid': msg.productId}">
            <span class="text-muted" v-if="!productId" >Select a product from your catalogue...</span>
            <span v-else>{{ productId }}</span>
          </div>
          <div class="input-group-append">
            <button class="btn btn-primary" @click="selectItem">Select</button>
          </div>
        </div>
        <span class="invalid-feedback">{{ msg.productId }}</span>
      </div>

      <!-- Quantity -->
      <div class="form-group row">
        <label for="quantity"><strong>Product Quantity<span class="required">*</span></strong></label>
        <input id="quantity" v-model="quantity" :class="{'form-control': true, 'is-invalid': msg.quantity}"
               placeholder="Enter the quantity" :disabled="!productId"
               required maxlength="10" type="text">
        <span class="invalid-feedback">{{ msg.quantity }}</span>
      </div>

      <!-- Price Per Item -->
      <div class="form-group row">
        <label for="pricePerItem"><strong>Price Per Item</strong></label>
        <div :class="{'input-group': true, 'is-invalid': msg.pricePerItem}">
          <div class="input-group-prepend">
            <span class="input-group-text">{{ this.currencySymbol }}</span>
          </div>
          <input id="pricePerItem" v-model="pricePerItem"
                 :class="{'form-control': true, 'is-invalid': msg.pricePerItem}"
                 maxlength="10" :disabled="!productId"
                 placeholder="Price Per Item" type="text">
          <div class="input-group-append">
            <span class="input-group-text">{{ this.currencyCode }}</span>
          </div>
        </div>
        <span class="invalid-feedback">{{ msg.pricePerItem }}</span>
      </div>

      <!-- Total Price -->
      <div class="form-group row">
        <label for="totalPrice"><strong>Total Price</strong></label>
        <div :class="{'input-group': true, 'is-invalid': msg.totalPrice}">
          <div class="input-group-prepend">
            <span class="input-group-text">{{ this.currencySymbol }}</span>
          </div>
          <input id="totalPrice" v-model="totalPrice"
                 :class="{'form-control': true, 'is-invalid': msg.totalPrice}"
                 maxlength="10" :disabled="!productId"
                 placeholder="Total Price" type="text">
          <div class="input-group-append">
            <span class="input-group-text">{{ this.currencyCode }}</span>
          </div>
        </div>
        <span class="invalid-feedback">{{ msg.totalPrice }}</span>
      </div>

      <!-- Manufactured -->
      <div class="form-group row">
        <label for="manufactured"><strong>Manufactured Date</strong></label>
        <input id="manufactured" v-model="manufactured"
               :class="{'form-control': true, 'is-invalid': msg.manufactured}" required style="width:100%"
               type="date" :disabled="!productId">
        <!--    Error message for the date input    -->
        <span class="invalid-feedback">{{ msg.manufactured }}</span>
      </div>

      <!-- Sell By -->
      <div class="form-group row">
        <label for="sellBy"><strong>Sell By Date</strong></label>
        <input id="sellBy" v-model="sellBy" :class="{'form-control': true, 'is-invalid': msg.sellBy}" required
               style="width:100%" :disabled="!productId"
               type="date">
        <!--    Error message for the date input    -->
        <span class="invalid-feedback">{{ msg.sellBy }}</span>
      </div>

      <!-- Best Before -->
      <div class="form-group row">
        <label for="bestBefore"><strong>Best Before Date</strong></label>
        <input id="bestBefore" v-model="bestBefore" :class="{'form-control': true, 'is-invalid': msg.bestBefore}"
               required style="width:100%" :disabled="!productId"
               type="date">
        <!--    Error message for the date input    -->
        <span class="invalid-feedback">{{ msg.bestBefore }}</span>
      </div>

      <!-- Expires -->
      <div class="form-group row">
        <label for="expires"><strong>Expiry Date<span class="required">*</span></strong></label>
        <input id="expires" v-model="expires" :class="{'form-control': true, 'is-invalid': msg.expires}" required
               style="width:100%" :disabled="!productId"
               type="date">
        <!--    Error message for the date input    -->
        <span class="invalid-feedback">{{ msg.expires }}</span>
      </div>

      <!-- Create Inventory Item button -->
      <div class="form-group row mb-0">
        <div class="btn-group" style="width: 100%">
          <button ref="close" class="btn btn-secondary col-4" data-dismiss="modal" @click="close">Cancel</button>
          <button class="btn btn-primary col-8" @click="checkInputs">Create Item</button>
        </div>
        <!-- Show an error if required fields are missing -->
        <div v-if="msg.errorChecks" class="error-box">
          <alert class="mb-0">{{ msg.errorChecks }}</alert>
        </div>
      </div>
    </div>
    <product-search v-if="selectingItem" :business-id="businessId"></product-search>
    <catalogue-items ref="catalogueItems" v-if="selectingItem" :business-id="businessId" :selecting-item="true"
                     @selected-product="finishSelectItem"
    />

  </div>
</template>

<script>
import {Business} from '@/Api'
import Alert from "@/components/Alert";
import CatalogueItems from "@/components/product-catalogue/ProductCatalogueItems";
import ProductSearch from "@/components/product-catalogue/ProductSearch";

export default {
  name: "CreateInventoryItem",
  components: {ProductSearch, CatalogueItems, Alert},
  data() {
    return {
      title: 'Create a new inventory item',
      selectedProduct: null,
      productId: '', // Required
      quantity: '', // Required
      pricePerItem: '',
      totalPrice: '',
      manufactured: '',
      sellBy: '',
      bestBefore: '',
      expires: '', // Required
      currencySymbol: '',
      currencyCode: '',
      msg: {
        productId: null,
        quantity: null,
        pricePerItem: null,
        totalPrice: null,
        manufactured: null,
        sellBy: null,
        bestBefore: null,
        expires: null,
        errorChecks: null
      },
      valid: true,
      selectingItem: false
    };
  },
  mounted() {
    this.getCurrency()
  },
  computed: {
    businessId() {
      return parseInt(this.$route.params.businessId);
    }
  },
  methods: {
    validateProductCode() {
      if (!/^[a-zA-Z0-9-]+$/.test(this.productId)) {
        this.msg.productId = 'Please select a product';
        this.valid = false;
      } else {
        this.msg.productId = null;
      }
    },
    /**
     * Validate the product Price Per Item field
     */
    validatePricePerItem() {
      // Regex valid for any non negative number with a max of 2 dp, or empty
      let isNotNumber = Number.isNaN(Number(this.pricePerItem))

      if (this.pricePerItem === '' || this.pricePerItem === null) {
        this.msg.pricePerItem = null;
      } else if (isNotNumber || !/^([0-9]+(.[0-9]{0,2})?)?$/.test(this.pricePerItem)) {
        this.msg.pricePerItem = 'Please enter a valid price';
        this.valid = false;
      } else{
        this.msg.pricePerItem = null;
      }
    },
    /**
     * Validate the product Total Price field
     */
    validateTotalPrice() {
      // Regex valid for any non negative number with a max of 2 dp, or empty
      let isNotNumber = Number.isNaN(Number(this.totalPrice))

      if (this.totalPrice === '' || this.totalPrice === null) {
        this.msg.totalPrice = null;
      } else if (isNotNumber || !/^([0-9]+(.[0-9]{0,2})?)?$/.test(this.totalPrice)) {
        this.msg.totalPrice = 'Please enter a valid price';
        this.valid = false;
      } else{
        this.msg.totalPrice = null;
      }
    },

    /**
     * Validate the product Quantity field
     */
    validateQuantity() {
      // Regex valid for any non negative integer under 2147483647
      let isNotNumber = Number.isNaN(Number(this.quantity))

      if (isNotNumber
          || this.quantity === ''
          || this.quantity === null
          || Number(this.quantity) > 2147483647
          || Number(this.quantity) <= 0
          || !/^([0-9]+([0-9]{0,2})?)?$/.test(this.quantity)) {
        this.msg.quantity = 'Please enter a valid quantity';
        this.valid = false;
      } else {
        this.msg.quantity = null;
      }
    },
    /**
     * Validate the product Manufactured field
     */
    validateDateManufactured() {
      if (this.manufactured !== '') {
        let dateNow = new Date()
        let dateGiven = new Date(this.manufactured)

        if ((dateGiven - dateNow > 0)) {
          this.msg.manufactured = 'Please enter a date in the past'
          this.valid = false
        } else {
          this.msg.manufactured = null
        }
      }
    },
    /**
     * Validate the product Sell By field
     */
    validateDateSellBy() {
      if (this.sellBy !== '') {
        let dateNow = new Date()
        let dateGiven = new Date(this.sellBy)

        if ((dateGiven - dateNow <= 0)) {
          this.msg.sellBy = 'Please enter a date in the future'
          this.valid = false
        } else {
          this.msg.sellBy = null
        }
      }
    },
    /**
     * Validate the product Best Before field
     */
    validateDateBestBefore() {
      if (this.bestBefore !== '') {
        let dateNow = new Date()
        let dateGiven = new Date(this.bestBefore)

        if ((dateGiven - dateNow <= 0)) {
          this.msg.bestBefore = 'Please enter a date in the future'
          this.valid = false
        } else {
          this.msg.bestBefore = null
        }
      }
    },
    /**
     * Validate the product Expiry field
     */
    validateDateExpires() {
      if (this.expires !== '') {
        let dateNow = new Date()
        let dateGiven = new Date(this.expires)

        if ((dateGiven - dateNow <= 0)) {
          this.msg.expires = 'Please enter a date in the future'
          this.valid = false
        } else {
          this.msg.expires = null
        }
      } else {
        this.msg.expires = 'Please enter a date'
        this.valid = false
      }

    },

    /**
     * Checks all inputs are valid
     */
    checkInputs() {
      this.validateProductCode();
      this.validatePricePerItem()
      this.validateTotalPrice()
      this.validateQuantity();
      this.validateDateManufactured()
      this.validateDateSellBy()
      this.validateDateBestBefore()
      this.validateDateExpires();

      if (!this.valid) {
        this.msg.errorChecks = 'Please fix the shown errors and try again';
        this.valid = true;
      } else {
        this.msg.errorChecks = null;
        this.addItem();
      }
    },
    /**
     * Add a new product to the business's product catalogue
     */
    addItem() {
      this.$root.$data.business.createItem(
          this.businessId, {
            "productId": this.productId,
            "quantity": Number(this.quantity),
            "pricePerItem": this.pricePerItem !== null && this.pricePerItem !== ''
                ? Number(this.pricePerItem) : null,
            "totalPrice": this.totalPrice !== null && this.totalPrice !== ''
                ? Number(this.totalPrice) : null,
            "manufactured": this.manufactured,
            "sellBy": this.sellBy,
            "bestBefore": this.bestBefore,
            "expires": this.expires
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
     * Cancel creating a new item and go back to inventory
     */
    async getCurrency() {
      const country = (await Business.getBusinessData(this.businessId)).data.address.country
      const currency = await this.$root.$data.product.getCurrency(country)
      this.currencySymbol = currency.symbol
      this.currencyCode = currency.code
    },
    close() {
      this.$emit('refresh-inventory');
    },
    selectItem() {
      this.selectingItem = true;
      this.title = 'Select a product from your catalogue'
      this.$emit('select-product-toggle');
    },
    finishSelectItem(product) {
      this.productId = product.id;
      this.currencySymbol = product.currency.symbol
      this.currencyCode = product.currency.code
      this.selectingItem = false;
      this.title = 'Create a new inventory item'
      this.$emit('select-product-toggle');
    },
    cancelSelectItem() {
      this.selectingItem = false;
      this.title = 'Create a new inventory item'
      this.$emit('select-product-toggle');
    },

    /**
     * Fills the catalogue with search results.
     * Called by the child component ProductSearch
     */
    applySearch(searchResults) {
      this.$refs.catalogueItems.applySearch(searchResults)
      //This makes sure to set the page the user is viewing back to 1
      //Needed if the user was not on page 1 and does a search, as the user needs to be brought back to page 1
      //This is accessing ProductCatalogueItems and setting it's page variable to 1
      this.$refs.catalogueItems.page = 1
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