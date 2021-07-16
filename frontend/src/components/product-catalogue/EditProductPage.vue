<template>
  <page-wrapper>

    <!-- Check if the user is logged in -->
    <login-required
        v-if="!isLoggedIn"
        page="view an individual product"
    />

    <!-- Check if the user is an admin of the business -->
    <admin-required
        v-else-if="!isAdminOf"
        :page="`of the business ${this.businessId} to edit its products`"
    />

    <div v-else>
      <!-- Title of the page -->
      <div class="text-center mb-4">
        <h1 class="test-center">Edit Product</h1>
      </div>

      <!-- Display error message if there is one -->
      <div class="row">
        <div class="col-12 col-sm-8 offset-sm-2">
          <alert v-if="errorMessage">Error: {{ errorMessage }}</alert>
        </div>
      </div>

      <!-- Display when the product is loading -->
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

            <!-- Product catalogue button -->
            <router-link
                :to="{name: 'viewCatalogue', params: {businessId: this.businessId}}"
                class="btn btn-primary float-right"
                type="button"
            >
              Product Catalogue
            </router-link>

          </div>
        </div>
      </div>

      <!-- Edit product div -->
      <div v-else-if="product" class="container-fluid">

        <!-- Row for submit error message -->
        <div v-if="submitError && !(submitError || '').includes('ProductIdAlreadyExistsException')" class="row">
          <div class="col-12 col-sm-8 offset-sm-2">
            <alert>An error occurred when submitting your changes:
              {{ submitError.slice(submitError.indexOf(':') + 2) }}
            </alert>
          </div>
        </div>

        <!-- Display when the product is loading -->
        <div v-if="submitting">
          <div class="alert alert-info col-12 col-sm-8 offset-sm-2">Submitting changes...</div>
        </div>

        <!-- Row for edit form -->
        <div class="row">
          <div class="col-12 col-sm-8 offset-sm-2">

            <form>
              <!-- ID -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="id">ID<span class="text-danger">*</span></label>
                <div class="col-sm-8">
                  <input
                      id="id" v-model="newProduct.id"
                      :class="{'form-control': 1, 'is-invalid': !idValid && idBlur}"
                      maxlength="255"
                      type="text"
                      @blur="idBlur = true"
                  >
                  <div v-if="idTaken" class="invalid-feedback">The ID must be unique</div>
                  <div v-else class="invalid-feedback">The ID must be unique, and can only contain letters, numbers,
                    hyphens
                    and must be at least 1 character long.
                  </div>
                </div>
              </div>

              <!-- Name -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="name">Name<span class="text-danger">*</span></label>
                <div class="col-sm-8">
                  <input
                      id="name"
                      v-model="newProduct.name"
                      :class="{'form-control': true, 'is-invalid': !nameValid && nameBlur}"
                      maxlength="255"
                      type="text"
                      @blur="nameBlur = true"
                  >
                  <div class="invalid-feedback">A name is required</div>
                </div>
              </div>

              <!-- Description -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="description">Description</label>
                <div class="col-sm-8">
                  <textarea id="description" v-model="newProduct.description" class="form-control" maxlength="255"
                            rows="3"
                            type="text"></textarea>
                </div>
              </div>

              <!-- Recommended Retail Price -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="rrp">Recommended Retail Price</label>
                <div class="col-sm-8">
                  <input
                      id="rrp"
                      v-model="newProduct.recommendedRetailPrice"
                      :class="{'form-control': true, 'is-invalid': !priceValid && priceBlur}"
                      maxlength="10"
                      type="text"
                      @blur="priceBlur = true"
                  >
                  <div class="invalid-feedback">The price must be a number</div>
                </div>
              </div>

              <!-- Manufacturer -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label" for="manufacturer">Manufacturer</label>
                <div class="col-sm-8">
                  <input id="manufacturer" v-model="newProduct.manufacturer" class="form-control" maxlength="255"
                         type="text">
                </div>
              </div>


              <!-- Images -->
              <div class="form-group row">
                <label class="col-sm-4 col-form-label">Images</label>
                <div class="col-sm-8">
                  <button
                      id="addImage"
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
                      accept="image/png, image/jpeg"
                      @change="imageUpload"/>

                  <div v-for="image in images"
                        :key="image.url" class="pad1"
                        @mouseover="image.hover = true"
                        @mouseleave="image.hover = false"
                  >
                    <img v-if="image.id === undefined" width="250"
                         :src="image.url"
                         alt="Uploaded product image"
                    />
                    <img v-else width="250"
                         :src="getImageURL(image.filename)"
                         alt="Current product image"
                    />
                    <button class="btn btn-danger ml-1 my-1 pad1"
                            type="button"
                            :data-target="'#removeImageModal'"
                            data-toggle="modal"
                            @click="changeDeletingImage(image)">

                    Remove
                    </button>



                    <!-- Remove Image modal -->
                    <div :id="'removeImageModal'" class="modal fade" role="dialog" tabindex="-1">
                      <div class="modal-dialog" role="document">
                        <div class="modal-content">

                          <!-- Title section of modal -->
                          <div class="modal-header">
                            <h5 class="modal-title">Remove Image</h5>
                            <button aria-label="Close" class="close" data-dismiss="modal" type="button">
                              <span ref="close" aria-hidden="true">&times;</span>
                            </button>
                          </div>

                          <!-- Body section of modal -->
                          <div class="modal-body">
                            <p>Do you really want to remove this image?</p>
                          </div>

                          <!-- Footer / button section of modal -->
                          <div class="modal-footer">
                            <button class="btn btn-danger" data-dismiss="modal" type="button" @click="removeImage(imageWantingToDelete)">Remove</button>
                            <button class="btn btn-secondary" data-dismiss="modal" type="button">Cancel</button>
                          </div>

                        </div>
                      </div>
                    </div>




                    <!--                    If the image cant be made primary because it is not uploaded yet-->
                    <button class="btn btn-secondary disabled ml-1 my-1 pad1"
                            v-if="image.id === undefined"
                            type="button" :data-target="'#cantMakePrimaryImageModal'" data-toggle="modal">
                      Make Primary
                    </button>
                    <button class="btn btn-primary ml-1 my-1 pad1 disabled"
                            v-else-if="image.id === currentPrimaryImageId"
                            type="button">
                      Already Primary
                    </button>
                    <button class="btn btn-primary ml-1 my-1 pad1"
                            v-else-if="image.id !== currentPrimaryImageId"
                            type="button" @click="makeImagePrimary(image.id)">
                      Make Primary
                    </button>




                    <!-- Can't make image primary information -->
                    <div :id="'cantMakePrimaryImageModal'" class="modal fade" role="dialog" tabindex="-1">
                      <div class="modal-dialog" role="document">
                        <div class="modal-content">

                          <!-- Title section of modal -->
                          <div class="modal-header">
                            <h5 class="modal-title">Information</h5>
                            <button aria-label="Close" class="close" data-dismiss="modal" type="button">
                              <span ref="close" aria-hidden="true">&times;</span>
                            </button>
                          </div>

                          <!-- Body section of modal -->
                          <div class="modal-body">
                            <p>This image is not on our servers yet. Please save changes before making this image Primary</p>
                          </div>

                          <!-- Footer / button section of modal -->
                          <div class="modal-footer">
                            <button class="btn btn-primary" data-dismiss="modal" type="button">Ok</button>
                          </div>

                        </div>
                      </div>
                    </div>




                  </div>
                </div>
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

            <div v-if="submitting">
              <!-- Saving changes button
              Shows when product is being saved (useful for images which take a while to upload) -->
              <button
                      disabled
                      class="btn btn-primary ml-1 my-1 float-right"
                      type="button"
              >
                Saving changes
              </button>

              <!--    Image upload progress counter    -->
              <p v-if="imagesEdited"
                 class="ml-1 my-2 float-right">
                {{numImagesUploaded}}/{{numImagesToUpload}} images uploaded
              </p>
            </div>




            <!-- Save Changes button -->
            <button v-else
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
import {Business, Images} from "@/Api";
import PageWrapper from "@/components/PageWrapper";

export default {
  name: "EditProductPage",
  mounted() {
    this.loadProduct();
  },

  data() {
    return {
      errorMessage: null,
      submitting: false, // True when the changes are being submitted to the api
      success: false, // True when the edits were successful
      submitError: null, // Contains the error message if the submit failed
      showFixesMessage: false,
      loading: true,
      product: null,
      newProduct: null,
      idBlur: false, // True when the user has clicked on then off the input field
      priceBlur: false,
      nameBlur: false,
      triedIds: [], // List of ids tested for uniqueness
      //Test Image Data
      images: [],
      imageWantingToDelete: null, //Sets when the user clicks the remove button on an image, used to preserve image through modal
      currentPrimaryImageId: null,
      imagesEdited: false,
      //Used to show progress in uploading images
      numImagesUploaded: 0,
      numImagesToUpload: 0
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

    /** Gets the product ID that is in the current path **/
    productId() {
      return this.$route.params.productId;
    },

    actor() {
      return this.$root.$data.user.state.actingAs;
    },

    /** A list of businesses the current user administers **/
    businessesAdministered() {
      if (this.isLoggedIn) {
        return this.$root.$data.user.state.userData.businessesAdministered;
      }
      return []
    },

    /**
     * Check if the user is an admin of the business and is acting as that business, or is a GAA
     */
    isAdminOf() {
      if (this.$root.$data.user.canDoAdminAction()) return true
      else if (this.actor.type !== "business") return false
      return this.actor.id === parseInt(this.$route.params.businessId);
    },

    /** Returns true if changes have been made to the product **/
    changesMade() {
      if (!this.product) {
        return false
      }
      if (this.imagesEdited) {
        return true
      }
      let allSame = true;
      for (const [key, val] of Object.entries(this.product)) {
        if (this.newProduct[key] !== val) {
          allSame = false;
        }
      }
      return !allSame
    },

    /** True if the current ID is taken **/
    idTaken() {
      return this.triedIds.includes(this.newProduct.id)
    },

    /** True if the inputted id is valid **/
    idValid() {
      // Regex for numbers, hyphens and letters
      return /^[a-zA-Z0-9-]+$/.test(this.newProduct.id) && !this.idTaken
    },

    /** True if the inputted name is valid **/
    nameValid() {
      return this.newProduct.name !== ''
    },

    /** True if the inputted price is valid **/
    priceValid() {
      // Regex valid for any number with a max of 2 dp, or empty
      let isNotNumber = Number.isNaN(Number(this.newProduct.recommendedRetailPrice))
      if (this.newProduct.recommendedRetailPrice === null || this.newProduct.recommendedRetailPrice === '') {
        return true
      } else if (isNotNumber) {
        return false
      }
      return /^([0-9]+(.[0-9]{0,2})?)?$/.test(this.newProduct.recommendedRetailPrice)
    },

    /** Returns a string list of the fields that aren't valid **/
    fieldsNeedingFixed() {
      let fixes = []
      if (!this.priceValid) {
        fixes.push('Recommended Retail Price')
      }
      if (!this.nameValid) {
        fixes.push('Name')
      }
      if (!this.idValid) {
        fixes.push('Id')
      }
      return fixes.join(', ')
    }
  },
  methods: {
    /**
     * Retrieves the image specified by the path
     */
    getImageURL(path) {
      return Images.getImageURL(path)
    },
    /**
     * Validates the users inputs, then sends the data to the api.
     */
    async submit() {
      // Set id as blurred in case the id was not unique
      this.idBlur = true

      // Check all fields are valid first
      this.showFixesMessage = false
      if (!this.nameValid || !this.idValid || !this.priceValid) {
        this.showFixesMessage = true
        return
      }

      // Set the rrp typeof to Number even if its an empty string
      this.newProduct.recommendedRetailPrice = Number(this.newProduct.recommendedRetailPrice)

      const newProductData = {
        id: this.newProduct.id,
        name: this.newProduct.name,
        description: this.newProduct.description,
        manufacturer: this.newProduct.manufacturer,
        recommendedRetailPrice: this.newProduct.recommendedRetailPrice
      }

      // Submit changes to api
      this.submitting = true;
      await Business.editProduct(this.businessId, this.productId, newProductData)
          .then(() => {
            this.addImages()
                .then(() => {
              if (this.currentPrimaryImageId !== this.product.primaryImageId) {
                Business.makePrimaryProductImage(this.businessId, this.newProduct.id, this.currentPrimaryImageId)
              }
            })
                .then(() => {
              this.submitError = null
              this.success = true
              this.submitting = false
            })
          })
          .catch((err) => {
            // Display the response error message if there is one
            this.submitError = err.response.data
                ? err.response.data
                : err
            // Check if the id is taken
            if ((this.submitError || '').includes('ProductIdAlreadyExistsException')) {
              this.triedIds.push(this.newProduct.id)
            }
            this.submitting = false
          })
    },

    /**
     * Cancels the product edit.
     * Will confirm with the user if they want to lose there changes, if they made any.
     */
    cancel() {
      this.$router.push({name: 'viewCatalogue', params: {businessId: this.businessId}})
    },

    /**
     * Loads the product data into this.product and this.newProduct
     */
    loadProduct() {
      Business.getProducts(this.businessId)
          .then((res) => {
            this.product = res.data.find(prod => prod.id === this.productId.toString())
            if (!this.product) {
              this.errorMessage = `There is no product with id ${this.productId}.`
            } else {
              this.currentPrimaryImageId = this.product.primaryImageId
              this.images = this.product.images
              this.newProduct = {...this.product}
              this.loading = false
            }
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
      if (this.productId !== this.newProduct.id) {
        this.$router.push(`/businesses/${this.businessId}/products/${this.newProduct.id}`)
      }

      // Reset data
      this.submitError = null
      this.success = false
      this.product = null
      this.images = []
      this.currentPrimaryImageId = null
      this.newProduct = null
      this.idBlur = false
      this.nameBlur = false
      this.priceBlur = false
      this.loading = true

      // Reload product
      this.loadProduct()
    },

    /**
     * Programmatically triggers the file input field when the
     * 'Add image' button is clicked.
     */
    addImageClicked () {
      this.imagesEdited = true
      this.$refs.fileInput.click()
    },
    /**
     * Handles the file being uploaded
     * @param event the button click event that triggers this function
     */
    imageUpload (event) {
      const files = event.target.files

      const formData = new FormData()
      formData.append("file", files[0])

      const fileReader = new FileReader()
      console.log(`File with name ${files[0].name} uploaded`)
      fileReader.addEventListener('load', () => {
        this.images.push({
          data: formData,
          url: fileReader.result,
          file: files[0]
        })
      })
      fileReader.readAsDataURL(files[0])
    },

    /**
     * Called by the remove button next to an uploaded image.
     * Calls the API to make a request to delete an image from the backend.
     * Removes the image from the frontend's list of images.
     * @param imageRemoving the image to be removed
     */
    removeImage(imageRemoving) {
      this.imagesEdited = true
      //If image has already been uploaded
      if(imageRemoving.id){
        Business.removeProductImage(this.businessId, this.newProduct.id, imageRemoving.id)
            .then(() => {
              this.removeImageFromList(imageRemoving)
            })
            .catch((err) => {
              this.errorMessage = err.response.data.message || err;
            })
      } else {
        //If the image has just been uploaded and then is removed
        this.removeImageFromList(imageRemoving)
      }

      //If the removing image is the primary image, a new one is set on the backend. this is updating to show that.
      if (this.product.primaryImageId === imageRemoving.id &&
          this.currentPrimaryImageId === imageRemoving.id &&
          this.images.length !== 0) {
        for (const image of this.images) {
          if (image.id !== undefined && image.id !== imageRemoving.id) {
            this.currentPrimaryImageId = image.id
            break
          }
        }
      }
    },

    /**
     * Used to remove the image from the list that is visible to the user
     *@param removedImage the image to be removed
     */
    removeImageFromList(removedImage){
      //Remove the deleted image from the list of images on screen
      this.images = this.images.filter(function(image) {
        return image !== removedImage;
      })
    },

    /**
     * Called to make the image the primary image of the product.
     * Sets the variable currentPrimaryImage, which is then sent to the backend when the save changes button is clicked
     * @param imageId the id of the image to make primary
     */
    makeImagePrimary(imageId) {
      this.imagesEdited = true
      //Sets the new primary image to be set when the user clicks the save changes button
      this.currentPrimaryImageId = imageId
    },

    /**
     * Makes requests to add the product's images
     */
    async addImages() {
      const imagesToUpload = this.images.filter(function(image) {
        return image.id === undefined;
      })
      this.numImagesToUpload = imagesToUpload.length

      for (const image of imagesToUpload) {
        //Id is undefined if it was just added
        await Business.addProductImage(this.businessId, this.newProduct.id, image.data)
        this.numImagesUploaded += 1;
      }
    },

    changeDeletingImage(image) {
      this.imageWantingToDelete = image
    }
  }
}
</script>

<style scoped>

</style>