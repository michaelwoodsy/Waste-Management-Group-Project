<template>
  <page-wrapper>

    <login-required
        v-if="!isLoggedIn"
        page="Register a Business"
    />

    <div v-else> <!--    If Logged In    -->
      <div class="row">
        <div class="col-12 text-center my-3">
          <h2>Register a Business</h2>
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
            <input id="businessName" v-model="businessName" class="form-control"
                   maxlength="200" placeholder="Enter your Business Name"
                   required style="width:100%" type="text">
            <br>
            <!--    Error message for business name input   -->
            <span v-if="msg.businessName" class="error-msg" style="margin: 0">{{ msg.businessName }}</span>
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
              ref="addressInput" :showErrors="submitClicked"
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
            <select id="businessType" v-model="businessType" class="form-control"
                    required style="width:100%" type="text">
              <option disabled hidden selected value>Please select one</option>
              <option>Accommodation and Food Services</option>
              <option>Retail Trade</option>
              <option>Charitable organisation</option>
              <option>Non-profit organisation</option>
            </select>
            <br>

            <!--    Error message for business type input   -->
            <span v-if="msg.businessType" class="error-msg" style="margin: 0">{{ msg.businessType }}</span>
            <br>
            <br>
          </div>





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
                <img width="250"
                     :src="image.url"
                     alt="Uploaded business image"
                />
                <button class="btn btn-danger ml-1 my-1 pad1"
                        @click="removeImage(image.url)">
                  Remove
                </button>
              </div>
            </div>
          </div>

          <div class="form-group row">
            <div class="col text-center">
              <!--    Image upload progress counter    -->
              <p v-if="submitting && images.length > 0"
                 class="ml-1 my-2 ">
                {{numImagesUploaded}}/{{numImagesToUpload}} images uploaded
              </p>
            </div>

          </div>





          <p style="width: 100%; text-align: center; margin: 0"><small>You must be at least 16 years old to register a
            business</small></p>
          <div class="form-row">
            <button v-if="!submitting" class="btn btn-block btn-primary" style="width: 100%; margin:0 20px"
                    v-on:click="checkInputs">
              Create Business
            </button>
            <button v-else class="btn btn-block btn-primary disabled" style="width: 100%; margin:0 20px"
                    v-on:click="checkInputs">Creating Business
            </button>
            <!--    Error message for the registering process    -->

            <div class="login-box" style="width: 100%; margin:20px 20px; text-align: center">
              <!-- Show error if something wrong -->
              <alert v-if="msg.errorChecks">
                {{ msg.errorChecks }}
              </alert>
            </div>
          </div>
        </div>
      </div>
    </div>

  </page-wrapper>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import Alert from "../Alert"
import AddressInputFields from "@/components/AddressInputFields";
import PageWrapper from "@/components/PageWrapper";
import {Business} from "@/Api";

/**
 * Default starting parameters
 */
export default {
  name: "RegisterBusinessPage",

  components: {
    PageWrapper,
    AddressInputFields,
    LoginRequired,
    Alert
  },

  data() {
    return {
      //Sets text boxes to empty at start
      businessName: '',    //Required
      description: '',
      address: {  //Required
        streetNumber: '',
        streetName: '',
        city: '',
        region: '',
        country: '',
        postcode: '',
      },
      addressIsValid: false,
      businessType: '',    //Required

      images: [],
      imageWantingToDelete: null, //Sets when the user clicks the remove button on an image, used to preserve image through modal
      //Used to show progress in uploading images
      numImagesUploaded: 0,
      numImagesToUpload: 0,


      msg: {
        'businessName': '',
        'description': '',
        'streetName': '',
        'country': '',
        'businessType': '',
        'errorChecks': null
      },
      valid: true,
      submitting: false,
      submitClicked: false
    }
  },

  /**
   * These methods are called when the page opens
   * */
  computed: {
    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*} true if user is logged in, otherwise false
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    }
  },

  /**
   * Methods that can be called by the program
   */
  methods: {

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

    isUnder16YearsOld() {
      const dateOfBirth = new Date(this.$root.$data.user.state.userData.dateOfBirth)
      const dateNow = new Date()
      return (dateNow.getUTCFullYear() - dateOfBirth.getUTCFullYear()) < 16
    },

    /**
     * Validating to check if the data entered is input correctly
     * If not an error message is displayed
     */
    async checkInputs() {
      this.submitClicked = true
      this.submitting = true
      this.validateBusinessName();
      await this.validateAddress();
      this.validateBusinessType();

      if (this.isUnder16YearsOld()) {
        this.msg['errorChecks'] = 'You must be at least 16 years old to register a business';
        console.log('Please fix the shown errors and try again');
      } else if (!this.valid) {
        this.msg['errorChecks'] = 'Please fix the shown errors and try again';
        console.log('Please fix the shown errors and try again');
        this.submitting = false
        this.valid = true;//Reset the value
      } else {
        this.msg['errorChecks'] = '';
        console.log('No Errors');
        //Send to server here
        this.addBusiness();
      }
    },

    /**
     * Add the new business to the server
     * Calls the api function createNew which sends a new business to the backend server
     * If this fails the program should set the error text to the error received from the backend server
     */
    addBusiness() {
      this.$root.$data.business.register(
          this.$root.$data.user.state.userId,
          this.businessName,
          this.description,
          this.address,
          this.businessType
      ).then((res) => {
        this.addImages(res.data.businessId).then(() => {
          this.submitting = false
          this.$root.$data.user.updateData()
          this.$root.$data.user.setActingAs(res.data.businessId, this.businessName, "business")
          this.$router.push({name: 'home'})
        })
      })
          .catch((err) => {
            this.msg.errorChecks = err.response
                ? err.response.data.slice(err.response.data.indexOf(":") + 2)
                : err
          });
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
     * Removes the image from the frontends list of images.
     * @param imageUrl the url of the image to be removed
     */
    removeImage(imageUrl) {
      this.images = this.images.filter(function(image) {
        return image.url !== imageUrl;
      })
    },

    /**
     * Makes requests to add the business's images
     */
    async addImages(businessId) {
      const imagesToUpload = this.images.filter(function(image) {
        return image.id === undefined;
      })
      this.numImagesToUpload = imagesToUpload.length

      for (const image of imagesToUpload) {
        await Business.addBusinessImage(businessId, image.data)
        this.numImagesUploaded += 1;
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
  display: inline;
}

</style>