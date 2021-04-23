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
              <h2>Create a new item</h2>
            </div>
          </div>

          <!-- Form fields -->
          <div>
            <!-- Product Code -->
            <div class="form-group row">
              <label for="productCode"><b>Product Code<span class="required">*</span></b></label>
              <select id="productCode" v-model="productCode" :class="{'form-control': true, 'is-invalid': msg.productCode}">
                <option v-for="code in productCodes" v-bind:key="code.id">
                  {{code.id}}
                </option>
              </select>
              <span class="invalid-feedback">{{ msg.productCode }}</span>
            </div>

            <!-- Quantity -->
            <div class="form-group row">
              <label for="quantity"><b>Product Quantity<span class="required">*</span></b></label>
              <input id="quantity" v-model="quantity" :class="{'form-control': true, 'is-invalid': msg.quantity}" maxlength="255"
                     placeholder="Enter the quantity" required type="text">
              <span class="invalid-feedback">{{ msg.quantity }}</span>
            </div>

            <!-- Price Per Item -->
            <div class="form-group row">
              <label for="pricePerItem"><b>Price Per Item</b></label>
              <div :class="{'input-group': true, 'is-invalid': msg.price}">
                <div class="input-group-prepend">
                  <span class="input-group-text">{{ this.currency.symbol }}</span>
                </div>
                <input id="pricePerItem" v-model="pricePerItem" :class="{'form-control': true, 'is-invalid': msg.price}"
                       maxlength="255"
                       placeholder="Price Per Item" type="text">
                <div class="input-group-append">
                  <span class="input-group-text">{{ this.currency.code }}</span>
                </div>
              </div>
              <span class="invalid-feedback">{{ msg.price }}</span>
            </div>

            <!-- Total Price -->
            <div class="form-group row">
              <label for="totalPrice"><b>Total Price </b></label>
              <div :class="{'input-group': true, 'is-invalid': msg.pricePerItem}">
                <span id="totalPrice">{{ this.currency.symbol }}{{totalPrice}} {{ this.currency.code }}</span>
              </div>

            </div>

            <!-- Manufactured -->
            <div class="form-row">
              <label for="manufactured"><b>Manufactured Date</b></label><br/>
              <input id="manufactured" v-model="manufactured" :class="{'form-control': true, 'is-invalid': msg.manufactured}" required style="width:100%"
                     type="date"><br>
              <!--    Error message for the date input    -->
              <span class="invalid-feedback">{{ msg.manufactured }}</span><br><br>
            </div>

            <!-- Sell By -->
            <div class="form-row">
              <label for="sellBy"><b>Sell By Date</b></label><br/>
              <input id="sellBy" v-model="sellBy" :class="{'form-control': true, 'is-invalid': msg.sellBy}" required style="width:100%"
                     type="date"><br>
              <!--    Error message for the date input    -->
              <span class="invalid-feedback">{{ msg.sellBy }}</span><br><br>
            </div>

            <!-- Best Before -->
            <div class="form-row">
              <label for="bestBefore"><b>Best Before Date</b></label><br/>
              <input id="bestBefore" v-model="bestBefore" :class="{'form-control': true, 'is-invalid': msg.bestBefore}" required style="width:100%"
                     type="date"><br>
              <!--    Error message for the date input    -->
              <span class="invalid-feedback">{{ msg.bestBefore }}</span><br><br>
            </div>

            <!-- Expires -->
            <div class="form-row">
              <label for="expires"><b>Expiry Date<span class="required">*</span></b></label><br/>
              <input id="expires" v-model="expires" :class="{'form-control': true, 'is-invalid': msg.expires}" required style="width:100%"
                     type="date"><br>
              <!--    Error message for the date input    -->
              <span class="invalid-feedback">{{ msg.expires }}</span><br><br>
            </div>


            <!-- Create Product button -->
            <div class="form-group row">
              <div class="btn-group" style="width: 100%">
                <button class="btn btn-secondary col-4" v-on:click="cancel">Cancel</button>
                <button class="btn btn-primary col-8" v-on:click="checkInputs">Create Item</button>
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
import {Business} from '@/Api'
import Alert from "@/components/Alert";
import LoginRequired from "@/components/LoginRequired";
import AdminRequired from "@/components/AdminRequired";

export default {
  name: "CreateInventoryItemPage",
  components: {AdminRequired, LoginRequired, Alert},
  data() {
    return {
      productCodes: [],
      productCode: '', // Required
      quantity: '', // Required
      pricePerItem: '',
      manufactured: '',
      sellBy: '',
      bestBefore: '',
      expires: '', // Required
      currency: null,
      msg: {
        productCode: null,
        quantity: null,
        price: null,
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
    Business.getProducts(this.$route.params.businessId).then((response) => this.getProductIds(response))
    this.getCurrency();
  },
  computed: {
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
      return this.$root.$data.user.state.actingAs.id === parseInt(this.$route.params.businessId);
    },

    totalPrice: function() {
      return (Math.round(this.quantity * this.pricePerItem* 100)) / 100
    }
  },
  methods: {
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
     * Rounds the Price to 2dp
     */
    roundPrice(price) {
      return (Math.round(price* 100)) / 100;
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
    validatePrice() {
      let isNotNumber = Number.isNaN(Number(this.pricePerItem))

      if (this.pricePerItem !== ''){
        if (isNotNumber) {
          this.msg.price= 'Please enter a valid price';
          this.valid = false;
        } else if (Number(this.pricePerItem) < 0) {
          this.msg.price= 'Please enter a non negative price'
          this.valid = false
        }
      } else {
        this.msg.price = null;
      }
    },
    /**
     * Validate the product Quantity field
     */
    validateQuantity() {
      let isNotNumber = Number.isNaN(Number(this.quantity))
      if (isNotNumber || this.quantity === '') {
        this.msg.quantity= 'Please enter a valid quantity';
        this.valid = false;
      } else if (Number(this.quantity) < 0) {
        this.msg.quantity= 'Please enter a non negative quantity'
        this.valid = false
      } else {
        this.msg.quantity = null;
      }
    },
    /**
     * Validate the product Manufactured field
     */
    validateDateManufactured() {
      if (this.manufactured !== ''){
        let dateNow = new Date()
        let dateGiven = new Date(this.manufactured)

        if ((dateGiven - dateNow <= 0)){
          this.msg.manufactured = 'Please enter a date in the future'
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
      if (this.sellBy !== ''){
        let dateNow = new Date()
        let dateGiven = new Date(this.sellBy)

        if ((dateGiven - dateNow <= 0)){
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
      if (this.bestBefore !== ''){
        let dateNow = new Date()
        let dateGiven = new Date(this.bestBefore)

        if ((dateGiven - dateNow <= 0)){
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
    validateDateExpires(){
      if (this.expires !== ''){
        let dateNow = new Date()
        let dateGiven = new Date(this.expires)

        if ((dateGiven - dateNow <= 0)){
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
      this.validatePrice()
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
      const rrp = Number(this.recommendedRetailPrice)
      this.$root.$data.business.createProduct(
          this.$root.$data.user.state.actingAs.id, {
            "id": this.id,
            "name": this.name,
            "description": this.description,
            "recommendedRetailPrice": this.recommendedRetailPrice !== '' ? this.roundRRP(rrp) : null
          }
      ).then(() => {
        this.$router.push({name: "viewCatalogue", params: {businessId: this.$root.$data.user.state.actingAs.id}});
      }).catch((err) => {
        this.msg.errorChecks = err.response ?
            err.response.data.slice(err.response.data.indexOf(':') + 2) :
            err
      });
    },
    /**
     * Cancel creating a new product and go back to product catalogue
     */
    cancel() {
      this.$router.push({name: "viewCatalogue", params: {businessId: this.$root.$data.user.state.actingAs.id}});
    },
    async getCurrency() {
      const country = 'New Zealand'
      this.currency = await this.$root.$data.product.getCurrency(country)
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