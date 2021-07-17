<template>
  <page-wrapper>
    <!-- Check if the user is logged in -->
    <login-required
        v-if="!isLoggedIn"
        page="view an individual product"
    />

    <required-to-be-user-or-g-a-a
        v-if="(!canDoAdminAction && !isEditingSelf) || !actingAsUser"
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
      <div v-if="success" class="container-flu1">

        <!-- Row for success message -->
        <div class="row">
          <div class="col-12 col-sm-8 offset-sm-2">
            <div class="alert alert-success">Successfully saved changes! Please log in again.</div>

            <!-- Make more changes button -->
            <button
                class="btn btn-secondary float-left"
                type="button"
                @click="resetPage"
            >
              Edit Again
            </button>

            <!-- Product catalogue button -->
            <button
                class="btn btn-primary float-right"
                type="button"
                @click="logout"
            >
              Login
            </button>
          </div>
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
            <label for="phoneNumber"><strong>Phone Number<span class="required">*</span></strong></label>
            <input id="phoneNumber" v-model="phoneNumber" :class="{'form-control': true, 'is-invalid': msg.phoneNumber}"
                   placeholder="Enter your Phone Number"
                   required maxlength="255" type="text">
            <span class="invalid-feedback">{{ msg.phoneNumber }}</span>
          </div>

          <hr/>
          <div class="form-group row">
            <address-input-fields style="width: 100%"
                :showErrors="submitting" :editing="true" :editingData="this.oldAddress"
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
          </div>

          <!-- Current Password -->
          <div class="form-group row" v-if="editingPassword">
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

              <!--    Image upload progress counter    -->
              <p v-if="submitting && imagesEdited"
                 class="ml-1 my-2 float-right">
                {{numImagesUploaded}}/{{numImagesToUpload}} images uploaded
              </p>


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
import {User} from "@/Api";
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
      firstName: null,    //Required
      lastName: null,     //Required
      middleName: null,
      nickname: null,
      bio: null,
      email: null,        //Required
      dateOfBirth: null,  //Required
      phoneNumber: null,
      addressValid: false,
      oldAddress: {},
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
      editingPassword: false,

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
    newPassword(value) {
      this.editingPassword = value.length > 0;
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
      return this.$root.$data.user.state.actingAs.type === 'user'
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
        this.firstName = response.data.firstName
        this.lastName = response.data.lastName
        this.middleName = response.data.middleName
        this.nickname = response.data.nickname
        this.bio = response.data.bio
        this.email = response.data.email
        this.dateOfBirth = response.data.dateOfBirth
        this.phoneNumber = response.data.phoneNumber
        this.oldAddress = response.data.homeAddress
        this.homeAddress = response.data.homeAddress
      }
    },

    /**
     * Validates the first name variable
     * Checks if the string is empty, if so displays a warning message
     */
    validateFirstName() {
      if (this.firstName === '') {
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
      if (this.lastName === '') {
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
      if (this.email === '') {
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
      if (this.dateOfBirth === '') {
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
      if (this.phone === '') {
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

    /** Checks if the address is valid **/
    validateAddress() {
      if (!this.addressIsValid) {
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
      if (this.newPassword !== '' && this.newPassword !== null &&
          (this.currentPassword === '' || this.currentPassword === null)) {
        this.msg.currentPassword = 'Please enter in your current password';
        this.valid = false;
      } else {
        this.msg.currentPassword = null;
      }
    },

    /**
     * Check all inputs are valid, if not show error message otherwise save edit
     */
    checkInputs() {
      this.validateFirstName();
      this.validateLastName();
      this.validateEmail();
      this.validateDateOfBirth();
      this.validatePhoneNumber();
      this.validateAddress();
      this.validatePassword();

      if (!this.valid) {
        this.msg.errorChecks = 'Please fix the shown errors and try again';
        console.log(this.msg.errorChecks);
        this.valid = true;
      } else {
        this.msg.errorChecks = null;
        console.log('No errors');
        this.editUser()
      }
    },

    /**
     * Saves the changes from editing the user
     */
    async editUser() {
      this.submitting = true
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
      if (this.editingPassword) {
        requestJSON.newPassword = this.newPassword
        requestJSON.currentPassword = this.currentPassword
      }
      await User.editUser(this.userId, requestJSON).then(() => {
        this.success = true
        this.submitting = false
      }).catch((err) => {
        this.showError(err)
        this.submitting = false
      });
    },

    /**
     * Logs out the user so they can log in again (after editing their profile)
     */
    logout() {
      this.$root.$data.user.setLoggedOut()
      this.$router.push({name: 'login'})
    },

    /**
     * Resets the page after submitting changes, so the user can make more changes.
     */
    resetPage() {
      // Reset data
      this.addressIsValid = false
      this.submitting = false
      this.success = false
      this.editingPassword = false
      this.images = []
      this.currentPrimaryImageId = null

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