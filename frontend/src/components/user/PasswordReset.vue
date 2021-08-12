<!--Page for a user to reset their password. Linked to in password reset email -->
<template>
  <page-wrapper>
    <div class="container-fluid">
      <div class="row justify-content-center">
        <!-- Page Content -->
        <div class="col text-center">

          <!--    Reset Password Header    -->
          <div class="row">
            <div class="col text-center">
              <h4>Reset Password</h4>
            </div>
          </div>

          <!--The link has expired-->
          <div v-if="linkExpired">
            <div class="row">
              <div class="col text-center font-weight-bold">
                <p>This link has expired</p>
              </div>
            </div>
          </div>

          <!--The link has not yet expired-->
          <div v-else>
            <div class="row">
              <div class="col text-center font-weight-bold">
                <p>Reset password for {{userEmail}}</p>
              </div>
            </div>

            <div v-if="!submitting">
              <!-- Password Input-->
              <div class="row">
                <!--    Password    -->
                <label for="password"><strong>New Password<span class="required">*</span></strong></label>
                <div class="input-group">
                  <input id="password" v-model="password"
                         :class="{'form-control': true, 'is-invalid': msg.password}"
                         maxlength="200"
                         placeholder="Enter your Password" :type="passwordType"><br>
                  <div class="input-group-append">
                    <button class="btn btn-primary no-outline" @click="showPassword()">
                <span :class="{bi: true,
                  'bi-eye-slash': passwordType === 'password',
                  'bi-eye': passwordType !== 'password'}" aria-hidden="true"></span>
                    </button>
                  </div>
                </div>
                <p style="font-size: small">Password must be a combination of lowercase and uppercase letters, numbers, and
                  be at least 8 characters long</p>
              </div>

              <div class="login-box text-center">
                <!-- Show error if something wrong -->
                <alert v-if="msg.password">
                  {{ msg.password }}
                </alert>
              </div>

              <!-- Button to set new password-->
              <div class="form-row justify-content-center">
                <button class="btn btn-primary" @click="validatePassword">
                  Reset Password
                </button>
              </div>
            </div>

            <!-- Changes submitted -->
            <div v-else>
              <alert alert-type="primary">
                Password changed
              </alert>
            </div>
          </div>
        </div>
      </div>
    </div>
  </page-wrapper>

</template>

<script>
import PageWrapper from "@/components/PageWrapper";
import Alert from "../Alert";

export default {
  name: "PasswordReset.vue",
  components: {
    PageWrapper,
    Alert
  },
  data() {
    return {
      password: '',
      //Used to toggle visibility of password input
      passwordType: 'password',
      msg: {
        password: null
      },
      submitting: false,
      valid: true,
    }
  },
  computed: {
    linkExpired() {
      return false
    },
    userEmail() {
      //TODO: compute this based on code in the URL
      return "myrtle.t@gmail.com"
    }
  },
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
     * Validates the password
     * then calls endpoint if password valid
     */
    validatePassword() {
      this.valid = true;//Reset the value
      this.submitting = true

      if (this.password === '') {
        this.msg.password = 'Please enter a password';
        this.valid = false;
      } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}/.test(this.password)) {
        this.msg.password = 'New password does not meet the requirements';
        this.valid = false;
      } else {
        this.msg.password = null;
      }

      if (!this.valid) {
        console.log('Please fix the shown errors and try again');
        this.submitting = false
      } else {
        console.log('No Errors');
        //Send to server here
        this.changePassword()
      }
    },

    /**
     * Calls the endpoint to change a user's password
     */
    changePassword() {
      //TODO: call endpoint here
    }
  }
}
</script>

<style scoped>

</style>