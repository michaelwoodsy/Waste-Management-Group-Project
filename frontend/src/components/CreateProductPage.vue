<template>

  <div class="container-fluid">
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
          <input id="id" v-model="id" class="form-control" placeholder="Enter a product ID" type="text">
        </div>
        <br/>

        <div class="form-row">
          <!-- Name -->
          <label for="name"><b>Product Name<span class="required">*</span></b></label>
          <div v-if="msg.name" class="error-msg" style="margin-left: 10px">{{ msg.name }}</div>
          <input id="name" v-model="name" class="form-control" placeholder="Enter a product name" required type="text">
        </div>
        <br/>

        <div class="form-row">
          <!-- Description -->
          <label for="description"><b>Product Description</b></label>
          <input id="description" v-model="description" class="form-control" placeholder="Enter a product description"
                 type="text">
        </div>
        <br/>

        <div class="form-row">
          <!-- RRP -->
          <label for="rrp"><b>Recommended Retail Price</b></label>
          <input id="rrp" v-model="recommendedRetailPrice" class="form-control" placeholder="Enter product RRP"
                 type="number">
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

</template>

<script>

import Alert from "@/components/Alert";

export default {
  name: "CreateProductPage",
  components: {Alert},
  data() {
    return {
      id: '',
      name: '', // Required
      description: '',
      recommendedRetailPrice: null,
      msg: {
        name: '',
        errorChecks: null
      },
      valid: true
    };
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
     * Checks all inputs are valid.
     */
    checkInputs() {
      this.validateName();

      if (!this.valid) {
        this.msg.errorChecks = 'Please fix the shown errors and try again';
        console.log(this.msg.errorChecks);
        this.valid = true;
      } else {
        this.msg.errorChecks = null;
        console.log('No errors');
        // TODO this.addProduct()
      }
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