<template>
  <page-wrapper>
    <!-- Check if the user is logged in -->
    <login-required
        v-if="!isLoggedIn && !success"
        page="view an individual product"
    />

    <required-to-be-user-or-g-a-a
        v-else-if="(!canDoAdminAction && !isEditingSelf) || !actingAsUser"
        page="edit this users profile"
    />


    <div v-else class="container-fluid">

      <!-- Page title -->
      <div class="row mb-4">
        <div class="col text-center">
          <h2>Edit Your Profile</h2>
        </div>
      </div>

      <!-- Div to display when the changes are successful -->
      <div v-if="success" class="row">
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

      <!-- Form fields -->
      <div v-else class="row">
        <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">
          <!-- First Name -->
          <div class="form-group row">
            <label for="firstName"><strong>First Name<span class="required">*</span></strong></label>
            <input id="firstName" v-model="firstName" :class="{'form-control': true, 'is-invalid': msg.firstName}"
                   placeholder="Enter your First Name"
                   required maxlength="255" type="text">
            <span class="invalid-feedback">{{ msg.firstName }}</span>
          </div>

          <!-- Last Name -->
          <div class="form-group row">
            <label for="lastName"><strong>Last Name<span class="required">*</span></strong></label>
            <input id="lastName" v-model="lastName" :class="{'form-control': true, 'is-invalid': msg.lastName}"
                   placeholder="Enter your Last Name"
                   required maxlength="255" type="text">
            <span class="invalid-feedback">{{ msg.lastName }}</span>
          </div>

          <!-- Middle Name -->
          <div class="form-group row">
            <label for="middleName"><strong>Middle Name</strong></label>
            <input id="middleName" v-model="middleName" class="form-control"
                   placeholder="Enter your Middle Name"
                   required maxlength="255" type="text">
          </div>

          <!-- Nickname -->
          <div class="form-group row">
            <label for="nickname"><strong>Nickname</strong></label>
            <input id="nickname" v-model="nickname" class="form-control"
                   placeholder="Enter your Nickname"
                   required maxlength="255" type="text">
          </div>

          <!-- Bio -->
          <div class="form-group row">
            <label for="bio"><strong>Bio</strong></label>
            <textarea id="bio" v-model="bio" class="form-control"
                      placeholder="Write a Bio (Max length 255 characters)"
                      required maxlength="255" type="text"></textarea>
          </div>

          <!-- Email -->
          <div class="form-group row">
            <label for="email"><strong>Email<span class="required">*</span></strong></label>
            <input id="email" v-model="email" :class="{'form-control': true, 'is-invalid': msg.email}"
                   placeholder="Enter your Email"
                   required maxlength="255" type="text">
            <small>Editing this requires you to enter your current password</small>
            <span class="invalid-feedback">{{ msg.email }}</span>
          </div>

          <!-- Date of Birth -->
          <div class="form-group row">
            <label for="dateOfBirth"><strong>Date of Birth<span class="required">*</span></strong></label>
            <input id="dateOfBirth" v-model="dateOfBirth" :class="{'form-control': true, 'is-invalid': msg.dateOfBirth}"
                   placeholder="Enter your Email"
                   required maxlength="255" type="date">
            <span class="invalid-feedback">{{ msg.dateOfBirth }}</span>
          </div>

          <!-- Phone Number -->
          <div class="form-group row">
            <label for="phoneNumber"><strong>Phone Number</strong></label>
            <input id="phoneNumber" v-model="phoneNumber" :class="{'form-control': true, 'is-invalid': msg.phoneNumber}"
                   placeholder="Enter your Phone Number"
                   required maxlength="255" type="text">
            <span class="invalid-feedback">{{ msg.phoneNumber }}</span>
          </div>

          <hr/>
          <div class="form-group row">
            <address-input-fields ref="addressInput" style="width: 100%"
                :showErrors="true"
                @setAddress="(newAddress) => {this.homeAddress = newAddress}"
                @setAddressValid="(isValid) => {this.addressIsValid = isValid}"
            />
          </div>
          <hr/>

          <!-- New Password -->
          <div class="form-group row">
            <label for="newPassword"><strong>New Password</strong></label>
            <div class="input-group">
              <input id="newPassword" v-model="newPassword" :class="{'form-control': true, 'is-invalid': msg.newPassword}"
                     placeholder="Enter your New Password"
                     required maxlength="255" :type="newPasswordType">
              <div class="input-group-append">
                <button class="btn btn-primary no-outline" @click="showNewPassword()">
              <span :class="{bi: true,
                'bi-eye-slash': newPasswordType === 'password',
                'bi-eye': newPasswordType !== 'password'}" aria-hidden="true"></span>
                </button>
              </div>
            </div>
            <span class="invalid-feedback d-block">{{ msg.newPassword }}</span>
            <p style="font-size: small" class="text-left">Password must be a combination of lowercase and uppercase letters, numbers, and
              be at least 8 characters long</p>
          </div>

          <!-- Current Password -->
          <div class="form-group row" v-if="needCurrentPassword">
            <label for="currentPassword"><strong>Current Password<span class="required">*</span></strong></label>
            <div class="input-group">
              <input id="currentPassword" v-model="currentPassword" :class="{'form-control': true, 'is-invalid': msg.currentPassword}"
                     placeholder="Enter your Current Password"
                     required maxlength="255" :type="currentPasswordType">
              <div class="input-group-append">
                <button class="btn btn-primary no-outline" @click="showCurrentPassword()">
              <span :class="{bi: true,
                'bi-eye-slash': currentPasswordType === 'password',
                'bi-eye': currentPasswordType !== 'password'}" aria-hidden="true"></span>
                </button>
              </div>
            </div>
            <span class="invalid-feedback d-block">{{ msg.currentPassword }}</span>
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
              <button v-if="submitting"
                  disabled
                  class="btn btn-primary"
                  type="button"
              >
                Saving changes
              </button>
              <button v-else
                      class="btn btn-primary"
                      type="button"
                      @click="checkInputs"
              >
                Save Changes
              </button>


            </div>
            <!-- Show an error if required fields are missing -->
            <div v-if="msg.errorChecks" class="error-box">
              <alert class="mb-0">{{ msg.errorChecks }}</alert>
            </div>
          </div>
        </div>
      </div>

    </div>
  </page-wrapper>
</template>

<script>
import Alert from "@/components/Alert";
import LoginRequired from "@/components/LoginRequired";
import RequiredToBeUserOrGAA from "@/components/RequiredToBeUserOrGAA";
import AddressInputFields from "@/components/AddressInputFields";
import {Images, User} from "@/Api";
import PageWrapper from "@/components/PageWrapper";

export default {
  name: "EditUser",
  components: {
    PageWrapper,
    Alert,
    AddressInputFields,
    LoginRequired,
    RequiredToBeUserOrGAA
  },
  data() {
    return {
      oldUser: null,
      firstName: null,    //Required
      lastName: null,     //Required
      middleName: null,
      nickname: null,
      bio: null,
      email: null,        //Required
      oldEmail: null,
      dateOfBirth: null,  //Required
      phoneNumber: null,
      addressValid: false,
      homeAddress: {},
      newPassword: '',
      currentPassword: '',

      //Used to toggle visibility of password input
      newPasswordType: 'password',
      currentPasswordType: 'password',

      msg: {
        firstName: null,
        lastName: null,
        email: null,
        dateOfBirth: null,
        phoneNumber: null,
        homeAddress: null,
        newPassword: null,
        currentPassword: null,
        errorChecks: null
      },
      addressIsValid: false,
      valid: true,
      submitting: false,
      success: false,
      successfulEdit: false,
      needCurrentPassword: false,

      images: [],
      imageWantingToDelete: null, //Sets when the user clicks the remove button on an image, used to preserve image through modal
      currentPrimaryImageId: null,
      imagesEdited: false,
      //Used to show progress in uploading images
      numImagesUploaded: 0,
      numImagesToUpload: 0
    };
  },
  async mounted() {
    await this.prefillFields();
  },
  watch: {
    /**
     * Shows or hides the input fields for entering password again and entering current password
     */
    newPassword() {
      this.needCurrentPassword = this.newPassword.length > 0 || this.email !== this.oldEmail
    },
    /**
     * Shows or hides the input fields for entering password again and entering current password
     */
    email() {
      this.needCurrentPassword = this.newPassword.length > 0 || this.email !== this.oldEmail
    }
  },
  computed: {
    /** Gets the user ID that is in the current path **/
    userId() {
      return this.$route.params.userId;
    },
    /** Checks to see if user is logged in currently **/
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },
    /**
     * The userId of the currently logged in user
     */
    loggedInUser() {
      return this.$root.$data.user.state.userId
    },
    /**
     * If the logged in user is a GAA
     */
    canDoAdminAction() {
      return this.$root.$data.user.canDoAdminAction()
    },
    /**
     * If the logged in user is a GAA
     */
    actingAsUser() {
      return this.$root.$data.user.state.actingAs === null || this.$root.$data.user.state.actingAs.type === 'user'
    },
    /**
     * Returns true if the user is currently editing their own profile
     * @returns {boolean|*}
     */
    isEditingSelf() {
      return this.userId === null || this.loggedInUser === null ||
          this.userId.toString() === this.loggedInUser.toString()
    },
  },
  methods: {
    /**
     * Retrieves the image specified by the path
     */
    getImageURL(path) {
      return Images.getImageURL(path)
    },
    /**
     * Method to toggle visibility of the newPassword field
     */
    showNewPassword() {
      if (this.newPasswordType === 'password') {
        this.newPasswordType = 'text'
      } else {
        this.newPasswordType = 'password'
      }
    },
    /**
     * Method to toggle visibility of the currentPassword field
     */
    showCurrentPassword() {
      if (this.currentPasswordType === 'password') {
        this.currentPasswordType = 'text'
      } else {
        this.currentPasswordType = 'password'
      }
    },

    /**
     * Prefills all fields with the card's existing values
     */
    async prefillFields() {
      if (this.userId !== null) {
        const response = await User.getUserData(this.userId)
        this.oldUser = response.data
        this.firstName = response.data.firstName
        this.lastName = response.data.lastName
        this.middleName = response.data.middleName
        this.nickname = response.data.nickname
        this.bio = response.data.bio
        this.oldEmail = response.data.email
        this.email = response.data.email
        this.dateOfBirth = response.data.dateOfBirth
        this.phoneNumber = response.data.phoneNumber
        this.homeAddress = response.data.homeAddress
        this.images = response.data.images
        this.currentPrimaryImageId = response.data.primaryImageId
        this.$refs.addressInput.fullAddressMode = false
        this.$refs.addressInput.address = response.data.homeAddress
      }
    },

    /**
     * Validates the first name variable
     * Checks if the string is empty, if so displays a warning message
     */
    validateFirstName() {
      if (this.firstName === '' || this.firstName === null) {
        this.msg['firstName'] = 'Please enter a first name'
        this.valid = false
      } else {
        this.msg['firstName'] = null
      }
    },
    /**
     * Validates the last name variable
     * Checks if the string is empty, if so displays a warning message
     */
    validateLastName() {
      if (this.lastName === '' || this.lastName === null) {
        this.msg['lastName'] = 'Please enter a last name'
        this.valid = false
      } else {
        this.msg['lastName'] = null
      }
    },

    /**
     * Validates the email variable
     * Checks if the string is of an email format using regex, if not, displays a warning message
     */
    validateEmail() {
      if (this.email === '' || this.email === null) {
        this.msg['email'] = 'Please enter an email address'
        this.valid = false
      } else if (!/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(this.email)) {
        this.msg['email'] = 'Invalid email address'
        this.valid = false
      } else {
        this.msg['email'] = null
      }
    },

    /**
     * Validates the date of birth variable
     * Checks if the string is empty, if so displays a warning message
     */
    validateDateOfBirth() {
      if (this.dateOfBirth === '' || this.dateOfBirth === null) {
        this.msg['dateOfBirth'] = 'Please enter a date of birth'
        this.valid = false
      }
      //If Date of Birth is in the future
      else if (new Date() < new Date(this.dateOfBirth)) {
        this.msg['dateOfBirth'] = 'Date of birth can not be in the future'
        this.valid = false
      }
      //If Date of birth is more than 150 years ago
      else if (new Date().getFullYear() - new Date(this.dateOfBirth).getFullYear() >= 150) {
        this.msg['dateOfBirth'] = 'Date of birth is unrealistic'
        this.valid = false
      } else {
        this.msg['dateOfBirth'] = null
      }
    },

    /**
     * Validates the phone variable
     * Checks if the string is empty, or if it is in an incorrect format
     */
    validatePhoneNumber() {
      //If no phone number is entered (which is allowed)
      if (this.phoneNumber === '' || this.phoneNumber === null) {
        this.msg['phoneNumber'] = null
      }
      //If phone number matches phone number regex
      else if (/^(\+\d{1,2}\s*)?\(?\d{1,6}\)?[\s.-]?\d{3,6}[\s.-]?\d{3,8}$/.test(this.phoneNumber)) {
        this.msg['phoneNumber'] = null
      } else {
        this.msg['phoneNumber'] = 'Invalid phone number'
        this.valid = false
      }
    },

    /**
     * Validates the address
     * Sets valid = false if the address is not valid
     */
    async validateAddress() {
      if (!await this.$refs.addressInput.checkAddressCountry()) {
        this.valid = false
      }
      if (!this.$refs.addressInput.valid) {
        this.valid = false
      }
    },


    /**
     * Validates the newPassword variable
     * Checks if it matches the regex, can be null or empty, in which case the password is not being changed.
     * Checks if the string is empty, if so displays a warning message
     */
    validatePassword() {
      if (this.newPassword !== '' && this.newPassword !== null &&
          !/(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}/.test(this.newPassword)) {
        this.msg.newPassword = 'Password does not meet the requirements';
        this.valid = false;
      } else {
        this.msg.newPassword = null;
      }

      //If email has changed
      if (this.email !== this.oldEmail && (this.currentPassword === null || this.currentPassword === "")) {
        this.msg.currentPassword = "Your current password is required"
        this.valid = false
      }

      if (this.newPassword !== '' && this.newPassword !== null &&
          (this.currentPassword === '' || this.currentPassword === null)) {
        this.msg.currentPassword = 'Your current password is required';
        this.valid = false;
      } else if (this.email !== this.oldEmail && (this.currentPassword === null || this.currentPassword === "")) {
        this.msg.currentPassword = "Your current password is required"
        this.valid = false
      } else {
        this.msg.currentPassword = null;
      }
    },

    /**
     * Check all inputs are valid, if not show error message otherwise save edit
     */
    async checkInputs() {
      this.submitting = true
      this.validateFirstName();
      this.validateLastName();
      this.validateEmail();
      this.validateDateOfBirth();
      this.validatePhoneNumber();
      await this.validateAddress();
      this.validatePassword();

      if (!this.valid) {
        this.msg.errorChecks = 'Please fix the shown errors and try again';
        this.submitting = false
        this.valid = true;
      } else {
        this.msg.errorChecks = null;
        console.log('No errors');
        await this.editUser()
      }
    },

    /**
     * Saves the changes from editing the user
     */
    async editUser() {
      let requestJSON = {
        firstName: this.firstName,
        lastName: this.lastName,
        middleName: this.middleName,
        nickname: this.nickname,
        bio: this.bio,
        email: this.email,
        dateOfBirth: this.dateOfBirth,
        phoneNumber: this.phoneNumber,
        homeAddress: this.homeAddress
      }
      if (this.needCurrentPassword) {
        requestJSON.newPassword = this.newPassword
        requestJSON.currentPassword = this.currentPassword
      }
      //Add current password to request
      if (this.oldEmail !== this.email) {
        requestJSON.currentPassword = this.currentPassword
      }

      await User.editUser(this.userId, requestJSON).then(() => {
        //If email has changed (need to log in again)
        this.successfulEdit = true
        if (this.isEditingSelf && this.successfulEdit && this.oldEmail !== this.email) {
          this.reLogIn()
        }
      }).catch((err) => {
        this.showError(err)
        console.log(err)
        this.submitting = false
      });
      await this.addImages().then(() => {
        this.submitError = null
        this.submitting = false
        this.success = true
      })
      //Sets the correct user data (So the name changes in the nav bar)
      if (this.isEditingSelf) {
        this.$root.$data.user.setLoggedIn(this.userId)
      }
    },

    /**
     * Log in again after your email has been changed
     */
    async reLogIn() {
      let newPassword = this.currentPassword
      if (this.newPassword !== null && this.newPassword !== "") newPassword = this.newPassword
      await this.$root.$data.user.login(this.email, newPassword)
          .catch((err) => {
            this.showError(err)
            this.submitting = false
          });
    },

    /**
     * Resets the page after submitting changes, so the user can make more changes.
     */
    resetPage() {
      // Reset data
      this.addressIsValid = false
      this.submitting = false
      this.success = false
      this.successfulEdit = false
      this.editingPassword = false
      this.images = []
      this.currentPrimaryImageId = null
      this.newPassword = ""
      this.currentPassword = ""

      // Reload user data
      this.prefillFields()
    },

    /**
     * Method to cancel making changes to user
     */
    cancel() {
      this.$router.push({name: 'home'})
    },

    /**
     * Shows the error returned in a request
     * @param err error returned
     */
    showError(err) {
      this.msg.errorChecks = err.response
          ? err.response.data.slice(err.response.data.indexOf(":") + 2)
          : err
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
        User.removeImage(this.userId, imageRemoving.id)
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
      if (this.oldUser.primaryImageId === imageRemoving.id &&
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
        await User.addImage(this.userId, image.data)
        this.numImagesUploaded += 1;
      }

      if (this.currentPrimaryImageId !== this.oldUser.primaryImageId) {
        await User.makePrimaryImage(this.userId, this.currentPrimaryImageId)
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