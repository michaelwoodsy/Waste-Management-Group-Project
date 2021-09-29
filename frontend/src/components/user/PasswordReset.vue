<!--Page for a user to reset their password. Linked to in password reset email -->
<template>
  <page-wrapper>
    <!-- Check if the user is logged in -->
    <logout-required
        v-if="isLoggedIn"
    />

    <!--The link has expired-->
    <div v-else-if="linkExpired">
      <div class="row">
        <div class="col text-center font-weight-bold">
          <p>This link has expired</p>
        </div>
      </div>
    </div>

    <div v-else class="row">
      <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">
        <!--    Reset Password Header    -->
        <div class="row">
          <div class="col text-center">
            <h4>Reset Password</h4>
          </div>
        </div>



        <!--The link has not yet expired-->
        <div>
          <div class="row">
            <div class="col text-center font-weight-bold">
              <p>Reset password for {{userEmail}}</p>
            </div>
          </div>

          <div v-if="!success">
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
              <alert v-if="msg.password" >
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
          <div v-if="success">
            <alert alert-type="alert-success" class="text-center">
              Password changed
            </alert>
          </div>
        </div>
      </div>
    </div>
  </page-wrapper>

</template>

<script>
import PageWrapper from "@/components/PageWrapper";
import LogoutRequired from "@/components/LogoutRequired";
import Alert from "../Alert";
import {User} from "@/Api";

export default {
  name: "PasswordReset.vue",
  components: {
    PageWrapper,
    LogoutRequired,
    Alert
  },
  data() {
    return {
      userEmail: '',
      password: '',
      linkExpired: false,
      //Used to toggle visibility of password input
      passwordType: 'password',
      msg: {
        password: null
      },
      success: false,
      valid: true,
    }
  },
  async mounted() {
    await this.validateToken()
  },
  computed: {
    /** Gets the token that is in the current path **/
    token() {
      return this.$route.params.token;
    },
    /** Checks to see if user is logged in currently **/
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    }
  },
  methods: {
    async validateToken() {
      await User.validateLostPasswordToken(this.token).then((res) => {
        this.userEmail = res.data.email
        this.linkExpired = false
      }).catch(() => {
        this.linkExpired = true
      })
    },

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
    async validatePassword() {
      this.valid = true;//Reset the value

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
        this.success = false
      } else {
        //Send to server here
        await this.changePassword()
      }
    },

    /**
     * Calls the endpoint to change a user's password
     */
    async changePassword() {
      await User.editLostPassword(this.token, this.password).then(() => {
        this.success = true
      }).catch(() => {
        this.msg.password = "Password is not valid, or the forgotten password token has expired"
      })
    }
  }
}
</script>

<style scoped>

</style>