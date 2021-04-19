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
      <div class="row">
        <div class="col text-center">
          <h2>Create a new Product</h2>
        </div>
      </div>

      <div class="row justify-content-center" style="margin-top: 20px">
        <div class="col-12 col-sm-10 col-md-8 col-lg-6 col-xl-5">

          <div class="form-row" style="margin-bottom: 20px">
            <!-- ID -->
            <label for="id"><b>Product ID<span class="required">*</span></b></label>
            <span v-if="msg.id" class="error-msg" style="margin-left: 10px">{{ msg.id }}</span>
            <input id="id" v-model="id" class="form-control" maxlength="255" placeholder="Enter a product ID"
                   type="text">
          </div>

          <div class="form-row" style="margin-bottom: 20px">
            <!-- Name -->
            <label for="name"><b>Product Name<span class="required">*</span></b></label>
            <span v-if="msg.name" class="error-msg" style="margin-left: 10px">{{ msg.name }}</span>
            <input id="name" v-model="name" class="form-control" maxlength="255" placeholder="Enter a product name"
                   required
                   type="text">
          </div>

          <div class="form-row" style="margin-bottom: 20px">
            <!-- Description -->
            <label for="description"><b>Product Description</b></label>
            <input id="description" v-model="description" class="form-control" maxlength="255"
                   placeholder="Enter a product description" type="text">
          </div>

          <div class="form-row" style="margin-bottom: 20px">
            <!-- RRP -->
            <label for="rrp"><b>Recommended Retail Price</b></label>
            <span v-if="msg.rrp" class="error-msg" style="margin-left: 10px">{{ msg.rrp }}</span>
            <div class="input-group">
              <div class="input-group-prepend">
                <span class="input-group-text">$</span>
              </div>
              <input id="rrp" v-model="recommendedRetailPrice" class="form-control" placeholder="Enter product RRP"
                     type="text">
            </div>
          </div>

          <div class="form-row" style="margin-bottom: 20px">
            <!-- Create Product button -->
            <div class="btn-group" style="width: 100%">
              <button class="btn btn-primary" v-on:click="checkInputs">Create Product</button>
              <button class="btn btn-secondary" v-on:click="cancel">Cancel</button>
            </div>
            <!-- Show an error if required fields are missing -->
            <div class="login-box" style="width: 100%; margin: 20px; text-align: center">
              <alert v-if="msg.errorChecks">{{ msg.errorChecks }}</alert>
            </div>
          </div>

        </div>
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
      recommendedRetailPrice: '',
      msg: {
        id: '',
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
    roundRRP(rrp) {
      return (Math.round(rrp * 100)) / 100;
    },
    /**
     * Validate product ID field.
     */
    validateId() {
      if (!/[a-zA-Z0-9-]+/.test(this.id)) {
        this.msg.id = 'Please enter a valid product ID';
        this.valid = false;
      } else {
        this.msg.id = '';
      }
    },
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
      if (Number.isNaN(Number(this.recommendedRetailPrice))) {
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
      this.validateId();
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
     * Cancel creating a new product and go back to product catalogue.
     */
    cancel() {
      this.$router.push({name: "viewCatalogue", params: {businessId: this.$root.$data.user.state.actingAs.id}});
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