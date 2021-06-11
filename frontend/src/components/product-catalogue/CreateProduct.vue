<template>
  <div class="container-fluid">

    <!-- Page title -->
    <div class="row mb-4">
      <div class="col text-center">
        <h2>Create a new Product</h2>
      </div>
    </div>

    <!-- Form fields -->
    <div>
      <!-- ID -->
      <div class="form-group row">
        <label for="id"><strong>Product ID<span class="required">*</span></strong></label>
        <input id="id" v-model="id" :class="{'form-control': true, 'is-invalid': msg.id}" maxlength="255"
               placeholder="Enter a product ID (Only letters, numbers and hyphens allowed)" required type="text">
        <span class="invalid-feedback">{{ msg.id }}</span>
      </div>

      <!-- Name -->
      <div class="form-group row">
        <label for="name"><strong>Product Name<span class="required">*</span></strong></label>
        <input id="name" v-model="name" :class="{'form-control': true, 'is-invalid': msg.name}" maxlength="255"
               placeholder="Enter a product name" required type="text">
        <span class="invalid-feedback">{{ msg.name }}</span>
      </div>

      <!-- Description -->
      <div class="form-group row">
        <label for="description"><strong>Product Description</strong></label>
        <textarea id="description" v-model="description" class="form-control" maxlength="255"
                  placeholder="Enter a product description" type="text"/>
      </div>

      <!-- Manufacturer -->
      <div class="form-group row">
        <label for="manufacturer"><strong>Manufacturer</strong></label>
        <input id="manufacturer" v-model="manufacturer" class="form-control" maxlength="255"
               placeholder="Enter a manufacturer" required type="text">
      </div>

      <!-- RRP -->
      <div class="form-group row">
        <label for="rrp"><strong>Recommended Retail Price</strong></label>
        <div :class="{'input-group': true, 'is-invalid': msg.rrp}">
          <div class="input-group-prepend">
            <span class="input-group-text">{{ this.currencySymbol }}</span>
          </div>
          <input id="rrp" v-model="recommendedRetailPrice" :class="{'form-control': true, 'is-invalid': msg.rrp}"
                 maxlength="10"
                 placeholder="Enter product RRP" type="text">
          <div class="input-group-append">
            <span class="input-group-text">{{ this.currencyCode }}</span>
          </div>
        </div>
        <span class="invalid-feedback">{{ msg.rrp }}</span>
      </div>

      <!-- Images -->
      <div class="form-group row">
        <label><strong>Images</strong></label>
        <div class="col-sm-8">
          <button
              class="btn btn-primary ml-1 my-1 pad1"
              type="button"
              @click="addImageClicked"
          >
            Add image
          </button>
          <input
              type="file"
              style="display: none"
              ref="fileInput"
              accept="image/*"
              @change="imageUpload"/>

          <div v-for="image in images"
               :key="image.url" class="pad1"
               @mouseover="image.hover = true"
               @mouseleave="image.hover = false"
          >
            <img width="180"
                 :src="image.url"
                 alt="Uploaded product image"
            />
            <button class="btn btn-danger ml-1 my-1 pad1"
                    @click="removeImage(image.url)">
              Remove
            </button>
          </div>

        </div>
      </div>

      <!-- Create Product button -->
      <div class="form-group row mb-0">
        <div class="btn-group" style="width: 100%">
          <button ref="close" class="btn btn-secondary col-4" data-dismiss="modal" @click="close">Cancel</button>
          <button class="btn btn-primary col-8" @click="checkInputs">Create Product</button>
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
import Alert from "@/components/Alert";
import {Business} from "@/Api";

export default {
  name: "CreateProduct",
  components: {Alert},
  props: {
    // Data of the card.
    products: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      id: '', // Required
      name: '', // Required
      description: '',
      manufacturer: '',
      recommendedRetailPrice: '',
      currencySymbol: '',
      currencyCode: '',
      images: [],
      msg: {
        id: null,
        name: null,
        rrp: null,
        errorChecks: null
      },
      valid: true
    };
  },
  mounted() {
    this.getCurrency();
  },
  computed: {
    /**
     * Returns the business ID of the business a product is being created for.
     */
    businessId() {
      return this.$route.params.businessId;
    }
  },
  methods: {
    /**
     * Checks through the businesses product ids and sees if the selected product id is being used
     */
    productIdExists() {
      for (const product of this.products) {
        if (product.id === this.id) {
          return true;
        }
      }
      return false;
    },

    /**
     * Validate product ID field
     */
    validateId() {
      if (this.id === '') {
        this.msg.id = 'Please enter a product ID';
        this.valid = false;
      } else if (!/^[a-zA-Z0-9-]+$/.test(this.id)) {
        this.msg.id = 'Product ID must consist of letters, numbers, and hyphens';
        this.valid = false;
      } else if (this.productIdExists()) {
        this.msg.id = 'Product ID already exists';
        this.valid = false;
      } else {
        this.msg.id = null;
      }
    },

    /**
     * Validate product name field
     */
    validateName() {
      if (this.name === '') {
        this.msg.name = 'Please enter a product name';
        this.valid = false;
      } else {
        this.msg.name = null;
      }
    },

    /**
     * Validate the product RRP field
     */
    validateRRP() {
      let isNotNumber = Number.isNaN(Number(this.recommendedRetailPrice))
      if (isNotNumber || !/^([0-9]+(.[0-9]{0,2})?)?$/.test(this.recommendedRetailPrice)) {
        this.msg.rrp = 'Please enter a valid price';
        this.valid = false;
      } else {
        this.msg.rrp = null;
      }
    },

    /**
     * Checks all inputs are valid
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
     * Add a new product to the business's product catalogue
     */
    addProduct() {
      this.$root.$data.business.createProduct(
          this.businessId, {
            "id": this.id,
            "name": this.name,
            "description": this.description,
            "manufacturer": this.manufacturer,
            "recommendedRetailPrice": this.recommendedRetailPrice !== null && this.recommendedRetailPrice !== ''
                ? Number(this.recommendedRetailPrice) : null
          }
      ).then(() => {
          this.addImages();
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
     * Closes the popup window to create a product
     */
    close() {
      this.$emit('refresh-products');
    },

    /**
     * Programmatically triggers the file input field when the
     * 'Add image' button is clicked.
     */
    addImageClicked () {
      this.$refs.fileInput.click()
    },

    /**
     * Handles the file being uploaded
     * @param event the button click event that triggers this function
     */
    imageUpload (event) {
      const files = event.target.files
      const fileReader = new FileReader()
      console.log(`File with name ${files[0].name} uploaded`)
      fileReader.addEventListener('load', () => {
        this.images.push({
          url: fileReader.result,
          file: files[0],
        })
      })
      fileReader.readAsDataURL(files[0])
    },

    /**
     * Called by the remove button next to an uploaded image.
     * Removes the image from the frontend's list of images.
     * @param imageUrl the url of the image to be removed
     */
    removeImage(imageUrl) {
      this.images = this.images.filter(function(image) {
        return image.url !== imageUrl;
      })
    },

    /**
     * Makes requests to add the product's images
     */
    addImages() {
      for (const image of this.images) {
        this.$root.$data.business.addProductImage(
            this.businessId, this.id, image.file)
      }
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