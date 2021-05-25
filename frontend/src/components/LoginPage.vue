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
            <input id="password"
                   v-model="password"
                   :class="{'form-control': true, 'is-invalid': msg.password}"
                   placeholder="Enter Password"
                   required
                   style="width: 100%"
                   type="password"
                   @keyup.enter="login"><br>
            <span class="invalid-feedback" style="text-align: left">{{ msg.password }}</span>
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


        </div>
      </div>
    </logout-required>

  </page-wrapper>
</template>

<script>
import LogoutRequired from "./LogoutRequired";
import Alert from "./Alert"
import PageWrapper from "@/components/PageWrapper";

const LoginPage = {
  name: "LoginPage",
  data() {
    return {
      username: '',
      password: '',
      error: null,
      msg: {
        'username': null,
        'password': null
      },
      valid: true
    }
  },

  components: {
    PageWrapper,
    LogoutRequired,
    Alert
  },

  methods: {
    checkUsername() {
      if (this.username === '') {
        this.msg['username'] = "Please enter a username"
        this.valid = false
      } else {
        this.msg['username'] = null
      }
    },

    checkPassword() {
      if (this.password === '') {
        this.msg['password'] = "Please enter a password"
        this.valid = false
      } else {
        this.msg['password'] = null
      }
    },

    /**
     * Login logic, checks that there are no missing fields, attempts to use login endpoint otherwise
     */
    login() {
      this.checkUsername()
      this.checkPassword()

      if (this.valid) {
        this.$root.$data.user.login(this.username, this.password)
            .then(() => {
              this.$router.push({name: 'home'})
            })
            .catch((err) => {
              this.error = err.response
                  ? err.response.data.slice(err.response.data.indexOf(":") + 2)
                  : err
            })
      } else {
        //Change valid back to true for when the login button is clicked again
        this.valid = true
      }
    },
  }
};

export default LoginPage;

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
