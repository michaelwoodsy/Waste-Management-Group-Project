<template>
  <page-wrapper>

    <div class="row">
      <div class="col-12 text-center my-3">
        <h2>Login</h2>
      </div>
    </div>

    <logout-required>
      <div class="row">
        <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">
          <div class="login-box">
            <!-- Show error if something wrong -->
            <alert v-if="error">
              {{ error }}
            </alert>
          </div>
          <!-- Username -->
          <div class="form-row">
            <label for="username" style="margin-top:20px"><strong>Email<span class="required">*</span></strong></label><br/>
            <input id="username"
                   v-model="username"
                   :class="{'form-control': true, 'is-invalid': msg.username}"
                   placeholder="Enter Email"
                   required
                   style="width: 100%"
                   type="text"
                   @keyup.enter="login">
            <span class="invalid-feedback" style="text-align: left">{{ msg.username }}</span>
          </div>
          <br>
          <!-- Password -->
          <div class="form-row">
            <label for="password"><strong>Password<span class="required">*</span></strong></label><br/>
            <div class="input-group">
              <input id="password"
                     v-model="password"
                     :class="{'form-control': true, 'is-invalid': msg.password}"
                     placeholder="Enter Password"
                     required
                     :type="passwordType"
                     @keyup.enter="login">
              <div class="input-group-append">
                <button class="btn btn-primary no-outline" @click="showPassword()">
                <span :class="{bi: true,
                  'bi-eye-slash': passwordType === 'password',
                  'bi-eye': passwordType !== 'password'}" aria-hidden="true"></span>
                </button>
              </div>
              <span class="invalid-feedback" style="text-align: left">{{ msg.password }}</span>
            </div>
            <div>
              <alert v-if="loginCount===3" class="m-2" id="noMoreAttempts">
                3 incorrect login attempts. No more attempts allowed.
                Click
                <a class="link-text m-0"
                             data-target="#viewPasswordResetModal"
                             data-toggle="modal"
                             @click="viewPasswordReset = true; error = null"
                >here</a>
                to reset password.</alert>
              <a class="link-text"
                 data-target="#viewPasswordResetModal"
                 data-toggle="modal"
                 @click="viewPasswordReset = true; error = null"
              >Forgot password?</a>
            </div>
            <br>


          </div>
          <br>
          <!-- Button for login and link to register-->
          <div class="form-row">
            <button class="btn btn-block btn-primary" style="width: 100%; margin:0 20px" @click="login">Login</button>
            <br>
            <p style="width: 100%; margin:0 20px; text-align: center">Don't have an account?
              <router-link class="link-text" to="/register">Register here</router-link>
            </p>
          </div>
          <!-- Password Reset Modal -->
          <div v-if="viewPasswordReset" id="viewPasswordResetModal" class="modal fade" data-backdrop="static">
            <div class="modal-dialog modal-md">
              <div class="modal-content">
                <div class="modal-body">
                  <button aria-label="Close"
                          class="close"
                          data-dismiss="modal"
                          type="button"
                          @click="resetPasswordResetModal">
                    <span aria-hidden="true">&times;</span>
                  </button>
                  <!-- Show error if something wrong -->
                  <div class="form-row mb-3">
                    <alert v-if="error">
                      {{ error }}
                    </alert>
                  </div>
                  <!-- Email -->
                  <div class="form-row mb-3">
                    <label for="email"><strong>Password Reset Via Email<span class="required">*</span></strong></label>
                    <input id="email" v-model="email" :class="{'form-control': true, 'is-invalid': msg.email}" maxlength="100"
                           placeholder="Enter your Email"
                           required style="width: 100%" type="email">
                    <!--    Error message for the email input    -->
                    <span class="invalid-feedback">{{ msg.email }}</span>
                  </div>
                  <!-- Send Password Reset Button -->
                  <div class="form-row mb-3">
                    <button v-if="!submitting" id="sendPasswordResetButton" class="btn btn-block btn-primary text-center" style="width: 100%; margin:0 20px"
                            v-on:click="sendEmail">Send Password Reset Email
                    </button>
                    <button v-else id="sendingPasswordResetButton" class="btn btn-block btn-primary text-center disabled" style="width: 100%; margin:0 20px"
                            v-on:click="sendEmail">Sending Password Reset Email
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- Password Reset Modal -->
          <div v-if="emailSent" id="viewEmailSentModal" class="modal fade" data-backdrop="static">
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
                    Password Reset email has been sent. <br>
                    Please check your spam folder. This email will expire in 1 hour.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </logout-required>

  </page-wrapper>
</template>

<script>
import LogoutRequired from "./LogoutRequired";
import Alert from "./Alert"
import PageWrapper from "@/components/PageWrapper";
import {User} from "@/Api";
import $ from 'jquery';


export default {
  name: "LoginPage",
  data() {
    return {
      username: '',
      password: '',
      //Used to toggle visibility of password input
      passwordType: 'password',
      email: null,
      emailSent: true,
      viewPasswordReset: false,
      submitting: false,
      error: null,
      msg: {
        'username': null,
        'password': null,
        'email': null,
      },
      valid: true,
      loginCount: 0
    }
  },

  components: {
    PageWrapper,
    LogoutRequired,
    Alert
  },

  methods: {
    /**
     * Method to toggle visibility of the password field
     */
    showPassword() {
      if(this.passwordType === 'password') {
        this.passwordType = 'text'
      } else {
        this.passwordType = 'password'
      }
    },

    /**
     * Validates the user has entered an email
     */
    checkUsername() {
      if (this.username === '') {
        this.msg['username'] = "Please enter an email address"
        this.valid = false
      } else {
        this.msg['username'] = null
      }
    },

    /**
     * Validates the user has entered a password
     */
    checkPassword() {
      if (this.password === '') {
        this.msg['password'] = "Please enter a password"
        this.valid = false
      } else {
        this.msg['password'] = null
        this.submitting = true;
      }
    },

    /**
     * Validates the email variable
     * Checks if the string is of an email format using regex, if not, displays a warning message
     */
    checkEmail() {
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
     * Resets the variables associated with sending the Password Reset when the user closes the modal
     */
    resetPasswordResetModal(){
      this.email = null
      this.submitting = false
      this.viewPasswordReset = false
      this.msg['email'] = null
      this.error = null
    },

    /**
     * Login logic, checks that there are no missing fields, attempts to use login endpoint otherwise
     */
    login() {
      this.checkUsername()
      this.checkPassword()

      if (this.valid && this.loginCount < 3) {
        this.$root.$data.user.login(this.username, this.password)
            .then(() => {
              this.$router.push({name: 'home'})
            })
            .catch((err) => {
              this.loginCount += 1
              this.error = err.response
                  ? err.response.data.slice(err.response.data.indexOf(":") + 2)
                  : err
            })
      } else {
        //Change valid back to true for when the login button is clicked again
        this.valid = true
      }
    },

    /**
     * Password Reset send email logic
     */
    sendEmail(){
      this.checkEmail()
      if (this.valid) {
        this.error = null
        this.submitting = true
        User.sendPasswordResetEmail(this.email)
          .then(() => {
            this.emailSent = true
            this.resetPasswordResetModal()
            $('#viewPasswordResetModal').modal('hide');
            $('#viewEmailSentModal').modal('show');
          })
            .catch((err) => {
              this.error = err.response
                  ? err.response.data.slice(err.response.data.indexOf(":") + 2)
                  : err
              this.submitting = false
            })
      } else {
        //Change valid back to true for when the Password Reset button is clicked again
        this.valid = true
      }
    }
  }
};


</script>

<style scoped>

.link-text {
  cursor: pointer;
  margin-right: 10px;
}

.required {
  color: red;
  display: inline;
}
</style>
