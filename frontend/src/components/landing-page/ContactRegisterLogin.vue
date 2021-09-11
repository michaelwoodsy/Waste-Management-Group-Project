<template>
  <div class="row mb-3">
    <div class="card col-sm text-white bg-secondary ml-2 mr-2">
      <div class="card-body">
        <h5 class="card-title d-inline">Have any questions?</h5>
        <button class="btn-block btn-sm btn-primary" @click="contactUs" data-toggle="modal" :data-target="'#contactUsModal'"><h5>Contact Us</h5></button>
      </div>
    </div>
    <div class="card col-sm text-white bg-secondary ml-2 mr-2">
      <div class="card-body">
        <h5 class="card-title d-inline">Don't have an account?</h5>
        <button class="btn-block btn-sm btn-primary" @click="register"><h5>Register</h5></button>
      </div>
    </div>
    <div class="card col-sm text-white bg-secondary ml-2 mr-2">
      <div class="card-body">
        <h5 class="card-title d-inline">Already have an account?</h5>
        <button class="btn-block btn-sm btn-primary" @click="login"><h5>Login</h5></button>
      </div>
    </div>
    <div id="contactUsModal" :key="this.contactUsModal" class="modal fade bd-example-modal-lg" data-backdrop="static">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-body" v-if="this.contactUsModal">
            <div class="container-fluid">
              <h5 class="form-row"><strong>Contact Us:</strong></h5>
              <div class="form-row mb-3">
                <label for="name" style="margin-top:20px"><strong>Name:<span
                    class="required">*</span></strong></label>
                <input id="name" v-model="name" :class="{'form-control': true, 'is-invalid': msg.name}"
                       maxlength="100"
                       placeholder="Enter your Name" required style="width:100%" type="text">
                <span class="invalid-feedback">{{ msg.name }}</span>
              </div>
              <div class="form-row">
                <label for="email"><strong>Email<span class="required">*</span></strong></label>
                <input id="email" v-model="email" :class="{'form-control': true, 'is-invalid': msg.email}" maxlength="100"
                       placeholder="Enter your Email"
                       required style="width: 100%" type="email">
                <span class="invalid-feedback">{{ msg.email }}</span>
              </div>
              <div class="form-row mb-3">
                <label for="message" style="margin-top:20px"><strong>Message:<span
                    class="required">*</span></strong></label>
                <textarea id="message" v-model="message" :class="{'form-control': true, 'is-invalid': msg.message}"
                       maxlength="100"
                       placeholder="Enter your Message" required style="width:100%" type="text"></textarea>
                <span class="invalid-feedback">{{ msg.message }}</span>
              </div>
              <div class="form-group row mb-0">
                <div class="btn-group" style="width: 100%">
                  <button id="cancelButton" ref="close" class="btn btn-secondary col-4" data-dismiss="modal" @click="resetContactUsModal">Cancel</button>
                  <button v-if="!submitting" id="sendButton" class="btn btn-primary col-8" @click="checkInputs">Send Message</button>
                  <button v-else id="sendingButton" class="btn btn-primary col-8" @click="checkInputs">Sending Message</button>
                </div>
                <div v-if="msg.errorChecks" class="error-box">
                  <alert class="mb-0">{{ msg.errorChecks }}</alert>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
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
      name: '',
      email: '',
      message: '',
      emailSent: true,
      msg: {
        'name': null,
        'email': null,
        'message': null,
        'errorChecks': null
      },
      valid: true,
      submitting: false,
    }
  },

  methods: {
    register() {
        this.$router.push({name: 'register'})
    },

    login() {
        this.$router.push({name: 'login'})
    },

    contactUs(){
      this.contactUsModal = true
    },

    validateName() {
      if (this.name === '') {
        this.msg['name'] = 'Please enter a name'
        this.valid = false
      } else {
        this.msg['name'] = null
      }
    },

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

    validateMessage() {
      if (this.message === '') {
        this.msg['message'] = 'Please enter a message'
        this.valid = false
      } else {
        this.msg['message'] = null
      }
    },

    resetContactUsModal() {
      this.contactUsModal = false
      this.name = ''
      this.email = ''
      this.message = ''
      this.submitting = false
      this.msg['name'] = null
      this.msg['email'] = null
      this.msg['message'] = null
      this.msg['errorChecks'] = null
      this.valid = true
    },

    async checkInputs() {
      this.submitting = true
      this.validateName();
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