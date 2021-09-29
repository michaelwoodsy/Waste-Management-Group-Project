<template>
  <page-wrapper>

    <div class="row">
      <div class="col-12 text-center my-3">
        <h2>Register for an Account</h2>
      </div>
    </div>
    <div v-if="!submitting && isLoggedIn">
      <logout-required></logout-required>
    </div>
    <div v-else class="row justify-content-center">
      <div class="col-12 col-sm-8 col-lg-6 col-xl-5 mb-2">

        <div class="form-row mb-3">
          <!--    First Name    -->
          <label for="firstName" style="margin-top:20px"><strong>First Name<span
              class="required">*</span></strong></label>
          <input id="firstName" v-model="firstName" :class="{'form-control': true, 'is-invalid': msg.firstName}"
                 maxlength="100"
                 placeholder="Enter your First Name" required style="width:100%" type="text">
          <!--    Error message for the first name input    -->
          <span class="invalid-feedback">{{ msg.firstName }}</span>
        </div>

        <div class="form-row mb-3">
          <!--    Middle Name    -->
          <label for="middleName"><strong>Middle Name</strong></label>
          <input id="middleName" v-model="middleName" class="form-control" maxlength="100"
                 placeholder="Enter your Middle Name" style="width:100%" type="text">
        </div>

        <div class="form-row mb-3">
          <!--    Last Name    -->
          <label for="lastName"><strong>Last Name<span class="required">*</span></strong></label>
          <input id="lastName" v-model="lastName" :class="{'form-control': true, 'is-invalid': msg.lastName}"
                 maxlength="100"
                 placeholder="Enter your Last Name" required style="width:100%" type="text">
          <!--    Error message for the last name input    -->
          <span class="invalid-feedback" style="margin: 0">{{ msg.lastName }}</span>
        </div>

        <div class="form-row mb-3">
          <!--    Nickname    -->
          <label for="nickname"><strong>Nickname</strong></label>
          <input id="nickname" v-model="nickname" class="form-control" maxlength="100"
                 placeholder="Enter your Nickname"
                 style="width: 100%" type="text">
        </div>

        <div class="form-row mb-3">
          <!--    Email    -->
          <label for="email"><strong>Email<span class="required">*</span></strong></label>
          <input id="email" v-model="email" :class="{'form-control': true, 'is-invalid': msg.email}" maxlength="100"
                 placeholder="Enter your Email"
                 required style="width: 100%" type="email">
          <!--    Error message for the email input    -->
          <span class="invalid-feedback">{{ msg.email }}</span>
        </div>

        <div class="form-row mb-3">
          <!--    bio    -->
          <label class="bio-label" for="bio"><strong>Bio</strong></label>
          <textarea id="bio" v-model="bio" class="form-control" maxlength="255"
                    placeholder="Write a Bio (Max length 255 characters)"
                    style="width: 100%; height: 200px;"></textarea>
        </div>

        <div class="form-row mb-3">
          <!--    Date of Birth    -->
          <label for="dateOfBirth"><strong>Date of Birth<span class="required">*</span></strong></label>
          <input id="dateOfBirth" v-model="dateOfBirth" :class="{'form-control': true, 'is-invalid': msg.dateOfBirth}"
                 maxlength="100" required
                 style="width:100%" type="date">
          <!--    Error message for the date of birth input    -->
          <span class="invalid-feedback">{{ msg.dateOfBirth }}</span>
        </div>

        <div class="form-row mb-3">
          <!--    Phone Number    -->
          <label for="phoneNumber"><strong>Phone Number</strong></label>
          <input id="phoneNumber" v-model="phone" class="form-control"
                 maxlength="30"
                 placeholder="Enter your Phone Number with extension" style="width:100%" type="text">
          <!--    Error message for the phone input    -->
          <span v-if="msg.phone" class="error-msg">{{ msg.phone }}</span>
        </div>

        <hr/>
        <address-input-fields
            ref="addressInput" :showErrors="submitClicked"
            @setAddress="(newAddress) => {this.homeAddress = newAddress}"
            @setAddressValid="(isValid) => {this.addressIsValid = isValid}"
        />
        <hr/>

        <div class="form-row mb-3">
          <!--    Password    -->
          <label for="password"><strong>Password<span class="required">*</span></strong></label>
          <div class="input-group">
            <input id="password" v-model="password" :class="{'form-control': true, 'is-invalid': msg.password}"
                   :type="passwordType"
                   maxlength="200" placeholder="Enter your Password"><br>
            <div class="input-group-append">
              <button class="btn btn-primary no-outline" @click="showPassword()">
                <span :class="{bi: true,
                  'bi-eye-slash': passwordType === 'password',
                  'bi-eye': passwordType !== 'password'}" aria-hidden="true"></span>
              </button>
            </div>
          </div>
          <!--    Error message for the password input    -->
          <span class="invalid-feedback">{{ msg.password }}</span>
          <p style="font-size: small">Password must be a combination of lowercase and uppercase letters, numbers, and
            be at least 8 characters long</p>
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
                ref="fileInput"
                accept="image/png, image/jpeg"
                style="display: none"
                type="file"
                @change="imageUpload"/>
          </div>
        </div>


        <!-- Images -->
        <div class="form-group row">
          <div class="col">


            <div v-for="image in images"
                 :key="image.url" class="pad1"
                 @mouseleave="image.hover = false"
                 @mouseover="image.hover = true"
            >
              <img v-if="image.id === undefined" :src="image.url"
                   alt="Uploaded product image"
                   width="250"
              />
              <img v-else :src="getImageURL(image.filename)"
                   alt="Current product image"
                   width="250"
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
              {{ numImagesUploaded }}/{{ numImagesToUpload }} images uploaded
            </p>
          </div>

        </div>


        <div class="form-row mb-3">
          <button v-if="!submitting" id="createButton" class="btn btn-block btn-primary"
                  style="width: 100%; margin:0 20px"
                  v-on:click="checkInputs">Create
            Account
          </button>
          <button v-else class="btn btn-block btn-primary disabled" style="width: 100%; margin:0 20px"
                  v-on:click="checkInputs">Creating Account
          </button>
          <!--    Error message for the registering process    -->

          <div class="login-box" style="width: 100%; margin:20px 20px; text-align: center">
            <!-- Show error if something wrong -->
            <alert v-if="msg.errorChecks">
              {{ msg.errorChecks }}
            </alert>

          </div>

          <p style="width: 100%; margin:0 20px; text-align: center">Already have an account?
            <router-link class="text-primary pointer" to="/login">Login here</router-link>
          </p>
          <br><br>
        </div>
      </div>
    </div>


  </page-wrapper>
</template>

<script>
import LogoutRequired from "../LogoutRequired";
import Alert from "../Alert"
import AddressInputFields from "@/components/AddressInputFields";
import PageWrapper from "@/components/PageWrapper";
import {User} from "@/Api";

/**
 * Default starting parameters
 */
export default {
  name: "RegisterPage",
  components: {
    PageWrapper,
    LogoutRequired,
    Alert,
    AddressInputFields
  },

  data() {
    return {
      //Sets text boxes to empty at start
      firstName: '',    //Required
      lastName: '',     //Required
      middleName: '',
      nickname: '',
      bio: '',
      email: '',        //Required
      dateOfBirth: '',  //Required
      phone: '',

      addressValid: false,
      homeAddress: {},

      password: '',
      //Used to toggle visibility of password input
      passwordType: 'password',

      images: [],
      imageWantingToDelete: null, //Sets when the user clicks the remove button on an image, used to preserve image through modal
      //Used to show progress in uploading images
      numImagesUploaded: 0,
      numImagesToUpload: 0,

      msg: {
        'firstName': null,
        'lastName': null,
        'email': null,
        'dateOfBirth': null,
        'streetName': null,
        'country': null,
        'password': null,
        'phone': null,
        'errorChecks': null
      },
      valid: true,
      submitting: false,
      submitClicked: false
    }
  },
  computed: {
    /**
     * Used to check if the user is logged in. Used so the "you are already logged in" message does not show when images are uploading
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
     * Method to toggle visibility of the password field
     */
    showPassword() {
      if (this.passwordType === 'password') {
        this.passwordType = 'text'
      } else {
        this.passwordType = 'password'
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
     * Validates the password variable
     * Checks if the string is empty, if so displays a warning message
     */
    validatePassword() {
      if (this.password === '') {
        this.msg['password'] = 'Please enter a password';
        this.valid = false;
      } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}/.test(this.password)) {
        this.msg['password'] = 'Password does not meet the requirements';
        this.valid = false;
      } else {
        this.msg['password'] = null;
      }
    },

    /**
     * Validates the phone variable
     * Checks if the string is empty, or if it is in an incorrect format
     */
    validatePhoneNumber() {
      //If no phone number is entered (which is allowed)
      if (this.phone === '') {
        this.msg['phone'] = null
      }
      //If phone number matches phone number regex
      else if (/^(\+\d{1,2}\s*)?\(?\d{1,6}\)?[\s.-]?\d{3,6}[\s.-]?\d{3,8}$/.test(this.phone)) {
        this.msg['phone'] = null
      } else {
        this.msg['phone'] = 'Invalid phone number'
        this.valid = false
      }
    },

    /**
     * Validating to check if the data entered is inputted correctly, If not displays a warning message
     */
    async checkInputs() {
      this.submitClicked = true
      this.submitting = true

      this.validateFirstName();
      this.validateLastName();
      this.validateEmail();
      this.validateDateOfBirth();
      this.validatePhoneNumber();
      await this.validateAddress();
      this.validatePassword();

      if (!this.valid) {
        this.msg['errorChecks'] = 'Please fix the shown errors and try again';
        this.submitting = false
        this.valid = true;//Reset the value
      } else {
        this.msg['errorChecks'] = '';
        //Send to server here
        await this.addUser();
      }
    },

    /** Checks if the address is valid **/
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
     * Add the new user to the server
     * Calls the api function createNew which sends a new user to the backend server
     * If this fails the program should set the error text to the error received from the backend server
     */
    async addUser() {
      try {
        await this.$root.$data.user.register({
          firstName: this.firstName,
          lastName: this.lastName,
          middleName: this.middleName,
          nickname: this.nickname,
          bio: this.bio,
          email: this.email,
          dateOfBirth: this.dateOfBirth,
          phone: this.phone,
          homeAddress: this.homeAddress,
          password: this.password
        })
        await this.addImages()
        this.submitting = false
        await this.$root.$data.user.setLoggedIn(this.$root.$data.user.state.userId)
        this.$router.push({name: 'home'})
      } catch (err) {
        this.msg.errorChecks = err.response
            ? err.response.data.slice(err.response.data.indexOf(":") + 2)
            : err
        this.submitting = false
      }
    },

    //IMAGES

    /**
     * Programmatically triggers the file input field when the
     * 'Add image' button is clicked.
     */
    addImageClicked() {
      this.imagesEdited = true
      this.$refs.fileInput.click()
    },

    /**
     * Handles the file being uploaded
     * @param event the button click event that triggers this function
     */
    imageUpload(event) {
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
      this.images = this.images.filter(function (image) {
        return image.url !== imageUrl;
      })
    },

    /**
     * Makes requests to add the product's images
     */
    async addImages() {
      const imagesToUpload = this.images.filter(function (image) {
        return image.id === undefined;
      })
      this.numImagesToUpload = imagesToUpload.length

      for (const image of imagesToUpload) {
        await User.addImage(this.$root.$data.user.state.userId, image.data)
        this.numImagesUploaded += 1;
      }
    }
  }
};

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
