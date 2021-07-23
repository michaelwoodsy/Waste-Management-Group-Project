<!-- Page for editing a business -->
<template>
  <page-wrapper>

    <login-required
        v-if="!isLoggedIn"
        page="Edit a Business"
    />

    <admin-required
        v-else-if="!isAdminOf"
        page="edit this business"
    />

    <div v-else> <!--   If Logged In and Admin   -->
      <div v-if="successfulEdit" class="row justify-content-center">
        <div class="col-12 col-sm-8">
          <div class="alert alert-success">Successfully saved changes!</div>

          <!-- Make more changes button -->
          <button
              class="btn btn-secondary float-left"
              type="button"
              @click="resetPage"
          >
            Edit Again
          </button>

          <!-- Home button -->
          <router-link
              :to="{name: 'home'}"
              class="btn btn-primary float-right"
              type="button"
          >
            Home
          </router-link>

        </div>
      </div>

      <div v-else>
        <div class="row">
          <div class="col-12 text-center my-3">
            <h2>Edit Your Business</h2>
          </div>
        </div>
        <div class="row">

          <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">

            <div class="form-row">
              <!--    Business Name    -->
              <label for="businessName" style="margin-top:20px">
                <strong>Business Name<span class="required">*</span></strong>
              </label>
              <br/>
              <input id="businessName" v-model="businessName"
                     :class="{'form-control': true, 'is-invalid': msg.businessName}"
                     maxlength="200" placeholder="Enter your Business Name"
                     required style="width:100%" type="text">
              <br>
              <!--    Error message for business name input   -->
              <span class="invalid-feedback" style="text-align: left">{{ msg.businessName }}</span>
              <br>
              <br>
            </div>
            <!--    Primary Admin    -->
            <!-- Only a DGAA/GAA or the primary admin themselves can see this-->
            <div class="form-row" v-if="isPrimaryAdmin">
              <label for="businessType">
                <strong>Primary Administrator<span class="required">*</span></strong>
              </label>
              <br/>
              <select id="primaryAdmin" v-model="primaryAdmin"
                      :class="{'form-control': true, 'is-invalid': msg.primaryAdmin}" required style="width:100%" type="text">
                <option v-for="admin in administrators"
                        :key="admin.id"
                        :value="admin"
                >
                  {{admin.firstName}} {{admin.lastName}}
                </option>
              </select>
              <br>

              <!--    Error message for primary admin input   -->
              <span class="invalid-feedback" style="text-align: left">{{ msg.primaryAdmin }}</span>
              <br>
              <br>
            </div>

            <div class="form-row">
              <!--    Business Description    -->
              <label class="description-label" for="description">
                <strong>Bio</strong>
              </label>
              <br>
              <textarea id="description" v-model="description" class="form-control"
                        maxlength="255" placeholder="Write a Business Description (Max length 255 characters)"
                        style="width: 100%; height: 200px;">

            </textarea>
            </div>
            <br>

            <hr/>
            <address-input-fields
                ref="addressInput"
                :showErrors="submitClicked"
                @setAddress="(newAddress) => {this.address = newAddress}"
                @setAddressValid="(isValid) => {this.addressIsValid = isValid}"
            />
            <hr/>

            <div class="form-row">
              <!--    Business Type    -->
              <label for="businessType">
                <strong>Business Type<span class="required">*</span></strong>
              </label>
              <br/>
              <select id="businessType" v-model="businessType"
                      :class="{'form-control': true, 'is-invalid': msg.businessType}" required style="width:100%" type="text">
                <option disabled hidden selected value>Please select one</option>
                <option>Accommodation and Food Services</option>
                <option>Retail Trade</option>
                <option>Charitable organisation</option>
                <option>Non-profit organisation</option>
              </select>
              <br>

              <!--    Error message for business type input   -->
              <span class="invalid-feedback" style="text-align: left">{{ msg.businessType }}</span>
              <br>
              <br>
            </div>
            <!--    Error message for business type input   -->
            <span class="invalid-feedback" style="text-align: left">{{ msg.businessType }}</span>
            <br>
            <br>





            <hr/>

            <div class="form-group row">
              <div class="col text-center">
                <h3 class="">Images</h3>
              </div>
              <div class="col text-center">
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
              </div>
            </div>

            <!-- Images -->
            <div class="form-group row">
              <div class="col">



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

            <div class="form-group row">
              <div class="col text-center">
                <!--    Image upload progress counter    -->
                <p v-if="submitting && imagesEdited"
                   class="ml-1 my-2 ">
                  {{numImagesUploaded}}/{{numImagesToUpload}} images uploaded
                </p>
              </div>

            </div>

            <!-- Save Changes button -->
            <div class="form-group row mb-0">
              <div class="btn-group" style="width: 100%">
                <!-- Cancel button when changes are made -->
                <button id="cancelButton"
                        class="btn btn-danger"
                        type="button"
                        @click="cancel"
                >
                  Cancel
                </button>
                <button class="btn btn-primary"
                        v-on:click="checkInputs"
                        :disabled="submitting"
                >
                  <span v-if="submitting">Saving Changes</span>
                  <span v-else>Save Changes</span>
                </button>

              </div>
              <!--    Error message for the editing process    -->
              <div class="login-box" style="width: 100%; margin:20px 20px; text-align: center">
                <!-- Show error if something wrong -->
                <alert id="errorAlert" v-if="msg.errorChecks">
                  {{ msg.errorChecks }}
                </alert>
              </div>
            </div>



          </div>
        </div>
      </div>
    </div>

    <!-- Currency Confirm Modal -->
    <div id="currencyConfirmModal" class="modal fade" ref="modal" role="dialog" tabindex="-1">
      <div class="modal-dialog" role="document">
        <div class="modal-content">

          <!-- Title section of modal -->
          <div class="modal-header">
            <h5 class="modal-title">Country Change</h5>
            <button aria-label="Close" class="close" data-dismiss="modal" type="button">
              <span ref="close" aria-hidden="true">&times;</span>
            </button>
          </div>

          <!-- Body section of modal -->
          <div class="modal-body">
            The country of your business is changing, would you like to update the currency of your
            existing products to match this new country?
          </div>

          <!-- Footer / button section of modal -->
          <div class="modal-footer justify-content-between">
            <button class="btn btn-secondary float-left" data-dismiss="modal" type="button">Cancel</button>
            <div>
              <button class="btn btn-secondary mr-2" data-dismiss="modal" type="button" @click="editBusiness">
                Keep Same
              </button>
              <button class="btn-primary btn" id="confirmButton" @click="editBusiness(true)"
                      data-dismiss="modal" type="button">
                Update
              </button>
            </div>
          </div>

        </div>
      </div>
    </div>

    <!-- Hidden button, only used to open the modal from methods -->
    <button class="d-none"
            data-toggle="modal"
            data-target="#currencyConfirmModal"
            ref="modalBtn"
    ></button>

  </page-wrapper>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import Alert from "../Alert"
import AddressInputFields from "@/components/AddressInputFields";
import PageWrapper from "@/components/PageWrapper";
import {Business, Images} from "@/Api";
import AdminRequired from "@/components/AdminRequired";
import userState from "@/store/modules/user";


export default {
  name: "EditBusiness",

  components: {
    PageWrapper,
    AddressInputFields,
    LoginRequired,
    AdminRequired,
    Alert
  },

  data() {
    return {
      user: userState,
      businessName: '',
      //isPrimaryAdmin is computed before the prefillFields() method runs
      //So need to have a random number for primaryAdminId so that page loads, then the
      //Primary Administrator section will show/hide based on the actual primary admin
      primaryAdminId: 0, //Id of the current primary admin
      primaryAdmin: null, //Field for editing
      administrators: [],
      description: '',
      address: {
        streetNumber: '',
        streetName: '',
        city: '',
        region: '',
        country: '',
        postcode: '',
      },
      addressIsValid: false,
      businessType: '',

      msg: {
        'businessName': '',
        'description': '',
        'streetName': '',
        'country': '',
        'businessType': '',
        'primaryAdmin': '',
        'errorChecks': null
      },
      valid: true,
      submitClicked: 0,
      submitting: false,
      successfulEdit: false,
      originalCountry: null,
      updateProductCurrency: false,

      images: [],
      imageWantingToDelete: null, //Sets when the user clicks the remove button on an image, used to preserve image through modal
      currentPrimaryImageId: null,
      imagesEdited: false,
      //Used to show progress in uploading images
      numImagesUploaded: 0,
      numImagesToUpload: 0,

      oldBusiness: null,
    }
  },
  async mounted() {
    await this.prefillFields();
  },

  /**
   * These methods are called when the page opens
   * */
  computed: {
    /**
     * ID of the businesses editing
     */
    businessId() {
      return this.$route.params.businessId
    },
    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*} true if user is logged in, otherwise false
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    actor() {
      return this.$root.$data.user.state.actingAs;
    },

    /**
     * Check if the user is an admin of the business and is acting as that business, or is a GAA
     */
    isAdminOf() {
      if (this.$root.$data.user.canDoAdminAction()) return true
      else if (this.actor.type !== "business") return false
      return this.actor.id === parseInt(this.businessId);
    },

    /**
     * Computes if the current user is the primary admin
     * @returns {boolean|*}
     */
    isPrimaryAdmin() {
      return this.$root.$data.user.canDoAdminAction() ||
          Number(this.$root.$data.user.state.userId) === this.primaryAdminId
    },
  },

  /**
   * Methods that can be called by the program
   */
  methods: {

    /**
     * Retrieves the image specified by the path
     */
    getImageURL(path) {
      return Images.getImageURL(path)
    },

    /**
     * Opens the currency conversion modal.
     */
    openModal() {
      const elem = this.$refs.modalBtn
      elem.click()
    },

    /**
     * Validates the business name variable
     * Checks if the string is empty, if so displays a warning message
     */
    validateBusinessName() {
      if (this.businessName === '') {
        this.msg['businessName'] = 'Please enter a Business Name'
        this.valid = false
      } else {
        this.msg['businessName'] = ''
      }
    },

    /**
     * Validates the business type variable
     * Checks if the selected option is not the default, if so displays a warning message
     */
    validateBusinessType() {
      if (this.businessType === '') {
        this.msg['businessType'] = 'Please select a Business Type'
        this.valid = false
      } else {
        this.msg['businessType'] = ''
      }
    },

    /**
     * Validates the primary admin field to make sure one is selected
     */
    validatePrimaryAdmin() {
      if (this.primaryAdmin === null) {
        this.msg['primaryAdmin'] = 'Please select a Primary Administrator'
        this.valid = false
      } else {
        this.msg['primaryAdmin'] = ''
      }
    },

    /**
     * Validates the address
     * Sets valid = false if the address is not valid
     */
    async validateAddress() {
      if (!this.addressIsValid) {
        this.valid = false
        return
      }
      if (!await this.$refs.addressInput.checkAddressCountry()) {
        this.valid = false
      }
    },

    /**
     * Validating to check if the data entered is input correctly
     * If not an error message is displayed
     */
    async checkInputs() {
      this.submitClicked++;
      this.submitting = true
      this.validateBusinessName();
      await this.validateAddress();
      this.validateBusinessType();
      this.validatePrimaryAdmin();

      if (!this.valid) {
        this.msg['errorChecks'] = 'Please fix the shown errors and try again';
        console.log('Please fix the shown errors and try again');
        this.submitting = false
        this.valid = true//Reset the value
      } else {
        this.msg['errorChecks'] = '';
        console.log('No Errors');
        //Send to server here
        if (this.address.country === this.originalCountry || this.updateProductCurrency) {
          await this.editBusiness();
        } else {
          this.openModal()
        }
      }
    },

    /**
     * Saves the changes from editing the business by calling the backend endpoint
     * If this fails the program should set the error text to the error received from the backend server
     * @param updateProductCurrency Boolean, updates the existing products with the new countries currency
     */
    async editBusiness(updateProductCurrency=false) {
      try {
        const requestData = {
          name: this.businessName,
          description: this.description,
          address: this.address,
          businessType: this.businessType,
          primaryAdministratorId: this.primaryAdmin.id
        }
        await Business.editBusiness(this.businessId, requestData, updateProductCurrency)
        if (this.user.isActingAsBusiness()) {
          this.user.state.actingAs.name = this.businessName
        }

        await this.addImages()

        this.submitting = false
        this.$root.$data.user.updateData()

        this.successfulEdit = true
      } catch (error) {
        this.submitting = false
        console.log(error)
        this.msg['errorChecks'] = error
      }
    },

    /**
     * Prefills the fields with their existing values
     */
    async prefillFields() {
      const response = await Business.getBusinessData(this.businessId)
      this.businessName = response.data.name
      this.description = response.data.description
      this.$refs.addressInput.fullAddressMode = false
      this.$refs.addressInput.address = response.data.address
      this.businessType = response.data.businessType
      this.administrators = response.data.administrators
      this.primaryAdminId = response.data.primaryAdministratorId //Used for computing isPrimaryAdmin
      this.oldBusiness = response.data
      this.images = response.data.images
      this.currentPrimaryImageId = response.data.primaryImageId
      this.originalCountry = response.data.address.country

      //Prefill the primary admin dropdown
      for(const admin of this.administrators) {
        if (admin.id === this.primaryAdminId) {
          this.primaryAdmin = admin

        }
      }
    },

    /**
     * Resets the page so that the user can edit the business again
     */
    async resetPage() {
      this.valid = true
      this.addressIsValid = false
      this.successfulEdit = false
      this.submitting = false
      await this.prefillFields()
    },

    /**
     * Method to cancel making changes to business
     */
    cancel() {
      this.$router.push({name: 'home'})
    },


    //IMAGES

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
        Business.removeBusinessImage(this.businessId, imageRemoving.id)
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
      if (this.oldBusiness.primaryImageId === imageRemoving.id &&
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
     * Called to make the image the primary image of the user.
     * Sets the variable currentPrimaryImage, which is then sent to the backend when the save changes button is clicked
     * @param imageId the id of the image to make primary
     */
    makeImagePrimary(imageId) {
      this.imagesEdited = true
      //Sets the new primary image to be set when the user clicks the save changes button
      this.currentPrimaryImageId = imageId
    },

    /**
     * Makes requests to add the user's images
     */
    async addImages() {
      const imagesToUpload = this.images.filter(function(image) {
        return image.id === undefined;
      })
      this.numImagesToUpload = imagesToUpload.length

      for (const image of imagesToUpload) {
        //Id is undefined if it was just added
        await Business.addBusinessImage(this.businessId, image.data)
        this.numImagesUploaded += 1;
      }

      if (this.images.length !== 0 && this.currentPrimaryImageId !== this.oldBusiness.primaryImageId) {
        await Business.makePrimaryBusinessImage(this.businessId, this.currentPrimaryImageId)
      }
    },

    changeDeletingImage(image) {
      this.imageWantingToDelete = image
    }
  }
}

</script>

<style scoped>

.required {
  color: red;
  display: inline;
}

</style>