<template>
  <div>

    <login-required
        v-if="!isLoggedIn"
        page="create a new product for this business"
    />

    <admin-required
        v-else-if="!isAdminOf"
        page="create a new product for this business"
    />

    <div v-else class="container-fluid">
      <br/><br/>
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h2>Create a new Product</h2>
        </div>
      </div>

      <div class="row" style="margin-top: 20px">
        <div class="col"></div>
        <div class="col-5">

          <div class="form-row">
            <!-- ID -->
            <label for="id"><b>Product ID</b></label>
            <input id="id" v-model="id" class="form-control" maxlength="255" placeholder="Enter a product ID"
                   type="text">
          </div>
          <br/>

          <div class="form-row">
            <!-- Name -->
            <label for="name"><b>Product Name<span class="required">*</span></b></label>
            <div v-if="msg.name" class="error-msg" style="margin-left: 10px">{{ msg.name }}</div>
            <input id="name" v-model="name" class="form-control" maxlength="255" placeholder="Enter a product name"
                   required
                   type="text">
          </div>
          <br/>

          <div class="form-row">
            <!-- Description -->
            <label for="description"><b>Product Description</b></label>
            <input id="description" v-model="description" class="form-control" maxlength="255"
                   placeholder="Enter a product description" type="text">
          </div>
          <br/>

          <div class="form-row">
            <!-- RRP -->
            <label for="rrp"><b>Recommended Retail Price</b></label>
            <div v-if="msg.rrp" class="error-msg" style="margin-left: 10px">{{ msg.rrp }}</div>
            <input id="rrp" v-model="recommendedRetailPrice" class="form-control" placeholder="Enter product RRP"
                   type="number" min="0" max="9999.99" step="0.01">
          </div>
          <br/>

          <div class="form-row">
            <!-- Create Product button -->
            <button class="btn btn-block btn-primary" style="margin: 20px" v-on:click="checkInputs">
              Create Product
            </button>
            <!-- Show an error if required fields are missing -->
            <div class="login-box" style="width: 100%; margin: 20px; text-align: center">
              <alert v-if="msg.errorChecks">{{ msg.errorChecks }}</alert>
            </div>
          </div>

        </div>
        <div class="col"></div>
      </div>

    </div>

  </div>
</template>

<script>
import Alert from "@/components/Alert";
import LoginRequired from "@/components/LoginRequired";
import AdminRequired from "@/components/AdminRequired";

export default {
  name: "CreateProductPage",
  components: {AdminRequired, LoginRequired, Alert},
  data() {
    return {
      id: '',
      name: '', // Required
      description: '',
      recommendedRetailPrice: null,
      msg: {
        name: '',
        rrp: '',
        errorChecks: null
      },
      valid: true
    };
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
    }
  },
  methods: {
    /**
     * Validate product name field.
     */
    validateName() {
      if (this.name === '') {
        this.msg.name = 'Please enter a product name';
        this.valid = false;
      } else {
        this.msg.name = '';
      }
    },
    /**
     * Validate the product RRP field.
     */
    validateRRP() {
      console.log(this.recommendedRetailPrice);
      if (this.recommendedRetailPrice && isNaN(parseFloat(this.recommendedRetailPrice))) {
        this.msg.rrp = 'Please enter a valid price';
        this.valid = false;
      } else {
        this.msg.rrp = '';
      }
    },
    /**
     * Checks all inputs are valid.
     */
    checkInputs() {
      this.validateName();
      this.validateRRP();

      if (!this.valid) {
        this.msg.errorChecks = 'Please fix the shown errors and try again';
        console.log(this.msg.errorChecks);
        this.valid = true;
      } else {
        this.msg.errorChecks = null;
        console.log('No errors');
        this.addProduct();
      }
    },
    /**
     * Add a new product to the business's product catalogue.
     */
    addProduct() {
      this.$root.$data.business.createProduct(
          this.$root.$data.user.state.actingAs.id,
          this.id,
          this.name,
          this.description,
          parseFloat(this.recommendedRetailPrice)
      ).then(() => {
        this.$router.push({name: "viewCatalogue", params: {businessId: this.$root.$data.user.state.actingAs.id}})
      }).catch((err) => {
        this.msg.errorChecks = err.response ?
            err.response.data.slice(err.response.data.indexOf(':') + 2) :
            err
      });
    }
  }
}
</script>

<style scoped>

.error-msg {
  color: red;
}

.required {
  color: red;
}

</style>