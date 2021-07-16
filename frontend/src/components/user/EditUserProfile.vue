<template>
  <div class="container-fluid">

    <!-- Page title -->
    <div class="row mb-4">
      <div class="col text-center">
        <h2>{{ modalTitle }}</h2>
      </div>
    </div>

    <!-- Form fields -->
    <div>
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

      <address-input-fields
          :showErrors="submit"
          @setAddress="(newAddress) => {this.homeAddress = newAddress}"
          @setAddressValid="(isValid) => {this.addressIsValid = isValid}"
      />

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
        <span class="invalid-feedback">{{ msg.newPassword }}</span>
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

        <span class="invalid-feedback">{{ msg.currentPassword }}</span>
      </div>


      <!-- Save Changes button -->
      <div class="form-group row mb-0">
        <div class="btn-group" style="width: 100%">
          <button id="cancelButton" ref="close" class="btn btn-secondary col-4" data-dismiss="modal" v-on:click="cancel=true" @click="close">Cancel</button>
          <button id="saveButton" class="btn btn-primary col-8" v-on:click="submit=true" @click="checkInputs">Save Changes</button>
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
import AddressInputFields from "@/components/AddressInputFields";
import {User} from "@/Api";

export default {
  name: "EditUser",
  components: {
    Alert,
    AddressInputFields
  },
  props: ['userId'],
  data() {
    return {
      modalTitle: 'Edit Your Profile',
      firstName: null,    //Required
      lastName: null,     //Required
      middleName: null,
      nickname: null,
      bio: null,
      email: null,        //Required
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
      cancel: false,
      submit: false,
      editingPassword: false,
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
  methods: {
    /**
     * Method to toggle visibility of the newPassword field
     */
    showNewPassword() {
      if(this.newPasswordType === 'password') {
        this.newPasswordType = 'text'
      } else {
        this.newPasswordType = 'password'
      }
    },
    /**
     * Method to toggle visibility of the currentPassword field
     */
    showCurrentPassword() {
      if(this.currentPasswordType === 'password') {
        this.currentPasswordType = 'text'
      } else {
        this.currentPasswordType = 'password'
      }
    },
    /**
     * Prefills all fields with the card's existing values
     */
    async prefillFields() {
      if(this.userId !== null) {
        const response = await User.getUserData(this.userId)
        this.firstName = response.data.firstName
        this.lastName = response.data.lastName
        this.middleName = response.data.middleName
        this.nickname = response.data.nickname
        this.bio = response.data.bio
        this.email = response.data.email
        this.dateOfBirth = response.data.dateOfBirth
        this.phoneNumber = response.data.phoneNumber
      }
    },

    /** Checks if the address is valid **/
    validateAddress() {
      if (!this.addressIsValid) {
        this.valid = false
      }
    },

    /**
     * Check all inputs are valid, if not show error message otherwise save edit
     */
    checkInputs(){
      this.validateAddress()

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
      // await Card.editCard(this.cardId, {
      //   "section": this.section,
      //   "title": this.title,
      //   "description": this.description,
      //   "keywordIds": keywordIds
      // }).then(() => {
      //   this.$refs.close.click();
      // }).catch((err) => {
      //   this.showError(err)
      // });
    },

    /**
     * Closes the popup window to edit a card
     */
    close() {
      this.$emit('user-edited');
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