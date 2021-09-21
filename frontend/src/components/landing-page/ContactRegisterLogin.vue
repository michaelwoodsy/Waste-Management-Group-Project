<template>
  <div>

    <div class="row row-cols-1 row-cols-md-3 mb-5 justify-content-center">
      <div class="col mb-3 mb-md-0">
        <!-- Contact Us Card -->
        <div class="card text-light bg-secondary shadow">
          <div class="card-body">
            <h5 class="card-title">Have any questions?</h5>
            <button class="btn btn-lg btn-block btn-primary" @click="contactUs" data-toggle="modal" :data-target="'#contactUsModal'">Contact Us</button>
          </div>
        </div>
      </div>
      <div class="col mb-3 mb-md-0">
        <!-- Register Card -->
        <div class="card text-light bg-secondary shadow">
          <div class="card-body">
            <h5 class="card-title">Don't have an account?</h5>
            <button class="btn btn-lg btn-block btn-primary" @click="register">Register</button>
          </div>
        </div>
      </div>
      <div class="col mb-3 mb-md-0">
        <!-- Login Card -->
        <div class="card text-light bg-secondary shadow">
          <div class="card-body">
            <h5 class="card-title">Already have an account?</h5>
            <button class="btn btn-lg btn-block btn-primary" @click="login">Login</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Contact Us Modal -->
    <div id="contactUsModal" :key="this.contactUsModal" class="modal fade bd-example-modal-lg" data-backdrop="static">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-body" v-if="this.contactUsModal">
            <div class="container-fluid">
              <h5 class="form-row mb-3"><strong>Contact Us:</strong></h5>

              <!-- Email Input -->
              <div class="form-row">
                <label for="email"><strong>Email<span class="required">*</span></strong></label>
                <input id="email" v-model="email" :class="{'form-control': true, 'is-invalid': msg.email}" maxlength="100"
                       placeholder="Enter your Email"
                       required style="width: 100%" type="email">
                <span class="invalid-feedback">{{ msg.email }}</span>
              </div>

              <!-- Message Input -->
              <div class="form-row mb-3">
                <label for="message" style="margin-top:20px"><strong>Message:<span
                    class="required">*</span></strong></label>
                <textarea id="message" v-model="message" :class="{'form-control': true, 'is-invalid': msg.message}"
                       maxlength="100"
                       placeholder="Enter your Message" required style="width:100%" type="text"></textarea>
                <span class="invalid-feedback">{{ msg.message }}</span>
              </div>

              <!-- Buttons -->
              <div class="form-group row mb-0">
                <div class="btn-group" style="width: 100%">
                  <!-- Cancel Button -->
                  <button id="cancelButton" ref="close" class="btn btn-secondary col-4" data-dismiss="modal" @click="resetContactUsModal">Cancel</button>
                  <!-- Send Message Button -->
                  <button v-if="!submitting" id="sendButton" class="btn btn-primary col-8" @click="checkInputs">Send Message</button>
                  <!-- Sending Message Button -->
                  <button v-else id="sendingButton" class="btn btn-primary col-8" @click="checkInputs">Sending Message</button>
                </div>

                <!-- Error Display -->
                <div v-if="msg.errorChecks" class="error-box">
                  <alert class="mb-0">{{ msg.errorChecks }}</alert>
                </div>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Email Sent Confirmation Modal -->
    <div v-if="emailSent" id="viewMessageSentModal" class="modal fade" data-backdrop="static">
      <div class="modal-dialog modal-md">
        <div class="modal-content">
          <div class="modal-body">
            <button aria-label="Close"
                    class="close"
                    data-dismiss="modal"
                    type="button">
              <span aria-hidden="true">&times;</span>
            </button>
            <p>
              Message was successfully sent to re:sale. <br>
              We will get back to you as soon as possible.
            </p>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import Alert from "@/components/Alert";
import {Landing} from "@/Api";
import $ from "jquery";

export default {
  name: "ContactRegisterLogin",

  components: {
    Alert,
  },

  computed: {
    /**
     * Check if user is logged in
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    }
  },

  data() {
    return {
      contactUsModal: false,
      email: '',
      message: '',
      emailSent: true,
      msg: {
        'email': null,
        'message': null,
        'errorChecks': null
      },
      valid: true,
      submitting: false,
    }
  },

  methods: {
    /***
     * Takes the user to the register page
     */
    register() {
        this.$router.push({name: 'register'})
    },

    /***
     * Takes the user to the login page
     */
    login() {
        this.$router.push({name: 'login'})
    },

    /***
     * Opens the Contact Us Modal
     */
    contactUs(){
      this.contactUsModal = true
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
     * Validates the message variable
     * Checks if the string is empty, if so displays a warning message
     */
    validateMessage() {
      if (this.message === '') {
        this.msg['message'] = 'Please enter a message'
        this.valid = false
      } else {
        this.msg['message'] = null
      }
    },

    /**
     * Resets all input fields and closes the modal once either:
     * - An email has been successfully sent to re:sale, OR
     * - The user pushes the 'Cancel' button on the modal
     */
    resetContactUsModal() {
      this.contactUsModal = false
      this.email = ''
      this.message = ''
      this.submitting = false
      this.msg['email'] = null
      this.msg['message'] = null
      this.msg['errorChecks'] = null
      this.valid = true
    },

    /**
     * Checks that all input fields in the Contact Us Modal are valid.
     * If so then contact() is called, if not then a general error message is shown
     * and those fields which have been filled incorrectly will display specific
     * error messages.
     */
    async checkInputs() {
      this.submitting = true
      this.validateEmail();
      this.validateMessage();
      if (!this.valid) {
        this.msg['errorChecks'] = 'Please fix the shown errors and try again';
        this.submitting = false
        this.valid = true;
      } else {
        this.msg['errorChecks'] = '';
        console.log('No Errors');
        await this.contact();
      }
    },

    /**
     * Runs the backend call for contacting re:sale.
     * If successful then a confirmation modal is displayed and all input fields are reset.
     * If unsuccessful then an appropriate error message is sent to the console.
     */
    async contact() {
      Landing.contact(this.email, this.message)
          .then(() => {
            this.emailSent = true
            this.resetContactUsModal()
            $('#contactUsModal').modal('hide');
            $('#viewMessageSentModal').modal('show');
          })
          .catch((err) => {
            this.error = err.response
                ? err.response.data.slice(err.response.data.indexOf(":") + 2)
                : err
            this.submitting = false
          })
    }
  }
}
</script>

<style scoped>

.error-box {
  width: 100%;
  margin: 20px;
  text-align: center;
}

.required {
  color: red;
  display: inline;
}

</style>