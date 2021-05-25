<template>
  <page-wrapper>

    <div class="row">
      <div class="col-12 text-center my-3">
        <h2>Register for an Account</h2>
      </div>
    </div>
    <logout-required>
      <div class="row justify-content-center">
        <div class="col-12 col-sm-8 col-lg-6 col-xl-5 mb-2">

          <div class="form-row mb-3">
            <!--    First Name    -->
            <label for="firstName" style="margin-top:20px"><strong>First Name<span class="required">*</span></strong></label>
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
              :showErrors="submitClicked"
              @setAddress="(newAddress) => {this.homeAddress = newAddress}"
              @setAddressValid="(isValid) => {this.addressIsValid = isValid}"
          />
          <hr/>

          <div class="form-row mb-3">
            <!--    Password    -->
            <label for="password"><strong>Password<span class="required">*</span></strong></label>
            <input id="password" v-model="password" :class="{'form-control': true, 'is-invalid': msg.password}"
                   maxlength="200"
                   placeholder="Enter your Password" style="width:100%" type="password"><br>
            <!--    Error message for the password input    -->
            <span class="invalid-feedback">{{ msg.password }}</span>
            <p style="font-size: small">Password must be a combination of lowercase and uppercase letters, numbers, and
              be at least 8 characters long</p>
          </div>

          <div class="form-row mb-3">
            <button id="createButton" class="btn btn-block btn-primary" style="width: 100%; margin:0 20px"
                    v-on:click="checkInputs">Create
              Account
            </button>
            <!--    Error message for the registering process    -->

            <div class="login-box" style="width: 100%; margin:20px 20px; text-align: center">
              <!-- Show error if something wrong -->
              <alert v-if="msg.errorChecks">
                {{ msg.errorChecks }}
              </alert>

            </div>

            <p style="width: 100%; margin:0 20px; text-align: center">Already have an account?
              <router-link class="link-text" to="/login">Login here</router-link>
            </p>
            <br><br>
          </div>
        </div>
      </div>
    </logout-required>

  </page-wrapper>
</template>

<script>
import LogoutRequired from "./LogoutRequired";
import Alert from "./Alert"
import AddressInputFields from "@/components/AddressInputFields";
import PageWrapper from "@/components/PageWrapper";

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
      submitClicked: false
    }
  },

  /**
   * Methods that can be called by the program
   */
  methods: {
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
      else if (/^\+(\(\d{1,4}\)|\d{1,4})\d+([-\s./]\d+)*$/.test(this.phone)) {
        this.msg['phone'] = null
      } else {
        this.msg['phone'] = 'Invalid phone number'
        this.valid = false
      }
    },

    /**
     * Validating to check if the data entered is inputted correctly, If not displays a warning message
     */
    checkInputs() {
      this.submitClicked = true

      this.validateFirstName();
      this.validateLastName();
      this.validateEmail();
      this.validateDateOfBirth();
      this.validatePhoneNumber();
      this.validateAddress();
      this.validatePassword();

      if (!this.valid) {
        this.msg['errorChecks'] = 'Please fix the shown errors and try again';
        console.log('Please fix the shown errors and try again');
        this.valid = true;//Reset the value
      } else {
        this.msg['errorChecks'] = '';
        console.log('No Errors');
        //Send to server here
        this.addUser();
      }
    },

    /** Checks if the address is valid **/
    validateAddress() {
      return this.addressValid
    },

    /**
     * Add the new user to the server
     * Calls the api function createNew which sends a new user to the backend server
     * If this fails the program should set the error text to the error received from the backend server
     */
    addUser() {
      this.$root.$data.user.register(
          this.firstName,
          this.lastName,
          this.middleName,
          this.nickname,
          this.bio,
          this.email,
          this.dateOfBirth,
          this.phone,
          this.homeAddress,
          this.password
      ).then(() => {
        this.$router.push({name: 'home'})
      })
          .catch((err) => {
            this.msg.errorChecks = err.response
                ? err.response.data.slice(err.response.data.indexOf(":") + 2)
                : err
          });
    },
  }
};

</script>

<style scoped>

.link-text {
  color: blue;
  cursor: pointer;
  margin-right: 10px;
}

.error-msg {
  color: red;
}

.required {
  color: red;
  display: inline;
}

</style>
