<template>
  <div class="container-fluid">

    <!-- Page title -->
    <div class="row mb-4">
      <div class="col text-center">
        <h2>Create a new Inventory Item</h2>
      </div>
    </div>

    <!-- Form fields -->
    <div>
      <!-- Product Code -->
      <div class="form-group row">
        <label for="productCode"><b>Product Code<span class="required">*</span></b></label>
        <select id="productCode" v-model="productCode"
                :class="{'form-control': true, 'custom-select': true, 'is-invalid': msg.productCode}">
          <option v-for="code in productCodes" v-bind:key="code.id">
            {{ code.id }}
          </option>
        </select>
        <span class="invalid-feedback">{{ msg.productCode }}</span>
      </div>

      <!-- Quantity -->
      <div class="form-group row">
        <label for="quantity"><b>Product Quantity<span class="required">*</span></b></label>
        <input id="quantity" v-model="quantity" :class="{'form-control': true, 'is-invalid': msg.quantity}"
               min="1" placeholder="Enter the quantity"
               required step="1" type="number">
        <span class="invalid-feedback">{{ msg.quantity }}</span>
      </div>

      <!-- Price Per Item -->
      <div class="form-group row">
        <label for="pricePerItem"><b>Price Per Item</b></label>
        <div :class="{'input-group': true, 'is-invalid': msg.pricePerItem}">
          <div class="input-group-prepend">
            <span class="input-group-text">{{ this.currencySymbol }}</span>
          </div>
          <input id="pricePerItem" v-model="pricePerItem"
                 :class="{'form-control': true, 'is-invalid': msg.pricePerItem}"
                 maxlength="255"
                 placeholder="Price Per Item" type="number">
          <div class="input-group-append">
            <span class="input-group-text">{{ this.currencyCode }}</span>
          </div>
        </div>
        <span class="invalid-feedback">{{ msg.pricePerItem }}</span>
      </div>

      <!-- Total Price -->
      <div class="form-group row">
        <label for="totalPrice"><b>Total Price</b></label>
        <div :class="{'input-group': true, 'is-invalid': msg.totalPrice}">
          <div class="input-group-prepend">
            <span class="input-group-text">{{ this.currencySymbol }}</span>
          </div>
          <input id="totalPrice" v-model="totalPrice"
                 :class="{'form-control': true, 'is-invalid': msg.totalPrice}"
                 maxlength="255"
                 placeholder="Total Price" type="number">
          <div class="input-group-append">
            <span class="input-group-text">{{ this.currencyCode }}</span>
          </div>
        </div>
        <span class="invalid-feedback">{{ msg.totalPrice }}</span>
      </div>

      <!-- Manufactured -->
      <div class="form-group row">
        <label for="manufactured"><b>Manufactured Date</b></label>
        <input id="manufactured" v-model="manufactured"
               :class="{'form-control': true, 'is-invalid': msg.manufactured}" required style="width:100%"
               type="date">
        <!--    Error message for the date input    -->
        <span class="invalid-feedback">{{ msg.manufactured }}</span>
      </div>

      <!-- Sell By -->
      <div class="form-group row">
        <label for="sellBy"><b>Sell By Date</b></label>
        <input id="sellBy" v-model="sellBy" :class="{'form-control': true, 'is-invalid': msg.sellBy}" required
               style="width:100%"
               type="date">
        <!--    Error message for the date input    -->
        <span class="invalid-feedback">{{ msg.sellBy }}</span>
      </div>

      <!-- Best Before -->
      <div class="form-group row">
        <label for="bestBefore"><b>Best Before Date</b></label>
        <input id="bestBefore" v-model="bestBefore" :class="{'form-control': true, 'is-invalid': msg.bestBefore}"
               required style="width:100%"
               type="date">
        <!--    Error message for the date input    -->
        <span class="invalid-feedback">{{ msg.bestBefore }}</span>
      </div>

      <!-- Expires -->
      <div class="form-group row">
        <label for="expires"><b>Expiry Date<span class="required">*</span></b></label>
        <input id="expires" v-model="expires" :class="{'form-control': true, 'is-invalid': msg.expires}" required
               style="width:100%"
               type="date">
        <!--    Error message for the date input    -->
        <span class="invalid-feedback">{{ msg.expires }}</span>
      </div>

      <!-- Create Product button -->
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

  </div>
</template>

<script>
import {Business} from '@/Api'
import Alert from "@/components/Alert";

export default {
  name: "CreateInventoryItem",
  components: {Alert},
  data() {
    return {
      productCodes: [],
      productCode: '', // Required
      quantity: '', // Required
      pricePerItem: '',
      totalPrice: '',
      manufactured: '',
      sellBy: '',
      bestBefore: '',
      expires: '', // Required
      currencySymbol: "",
      currencyCode: "",
      msg: {
        productCode: null,
        quantity: null,
        pricePerItem: null,
        totalPrice: null,
        manufactured: null,
        sellBy: null,
        bestBefore: null,
        expires: null,
        errorChecks: null
      },
      valid: true
    };
  },

  mounted() {
    this.getCurrency()
    Business.getProducts(this.$route.params.businessId).then((response) => this.getProductIds(response))
  },

  methods: {
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
     * Rounds the Price to 2dp
     */
    roundPrice(price) {
      return (Math.round(price * 100)) / 100;
    },
    /**
     * Validate product Code field
     */
    validateProductCode() {
      if (!/^[a-zA-Z0-9-]+$/.test(this.productCode)) {
        this.msg.productCode = 'Please enter a valid product ID';
        this.valid = false;
      } else {
        this.msg.productCode = null;
      }
    },

    /**
     * Validate the product Price Per Item field
     */
    validatePricePerItem() {
      let isNotNumber = Number.isNaN(Number(this.pricePerItem))

      if (this.pricePerItem !== '') {
        if (isNotNumber) {
          this.msg.pricePerItem = 'Please enter a valid price';
          this.valid = false;
        } else if (Number(this.pricePerItem) < 0) {
          this.msg.pricePerItem = 'Please enter a non negative price'
          this.valid = false
        }
      } else {
        this.msg.pricePerItem = null;
      }
    },

    /**
     * Validate the product Total Price field
     */
    validateTotalPrice() {
      let isNotNumber = Number.isNaN(Number(this.totalPrice))

      if (this.totalPrice !== '') {
        if (isNotNumber) {
          this.msg.totalPrice = 'Please enter a valid price';
          this.valid = false;
        } else if (Number(this.totalPrice) < 0) {
          this.msg.totalPrice = 'Please enter a non negative price'
          this.valid = false
        }
      } else {
        this.msg.totalPrice = null;
      }
    },

    /**
     * Validate the product Quantity field
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
        console.log(this.msg.errorChecks);
        this.valid = true;
      } else {
        this.msg.errorChecks = null;
        console.log('No errors');
        this.addItem();
      }
    },

    /**
     * Add a new product to the business's product catalogue
     */
    addItem() {
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
      const country = (await Business.getBusinessData(parseInt(this.$route.params.businessId))).data.address.country
      const currency = await this.$root.$data.product.getCurrency(country)
      this.currencySymbol = currency.symbol
      this.currencyCode = currency.code
    },
    close() {
      this.$emit('refresh-inventory');
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