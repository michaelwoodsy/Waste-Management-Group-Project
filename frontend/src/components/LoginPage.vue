<template>
  <div class="container-fluid">
    <br><br>
    <div class="row">
      <div class="col-12 text-center mb-2">
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
            <label for="username" style="margin-top:20px"><b>Username<span class="required">*</span></b></label><br/>
            <input type="text"
                   :class="inputClass(username)"
                   class="form-control"
                   style="width: 100%"
                   v-model="username"
                   placeholder="Enter Username"
                   id="username"
                   @keyup.enter="login"
                   required>
          </div><br>
          <!-- Password -->
          <div class="form-row">
            <label for="password"><b>Password<span class="required">*</span></b></label><br/>
            <input type="password"
                   :class="inputClass(password)"
                   class="form-control"
                   style="width: 100%"
                   v-model="password"
                   placeholder="Enter Password"
                   id="password"
                   @keyup.enter="login"
                   required><br/><br/>
          </div>
          <!-- Username error (empty) -->
          <div class="form-row">
            <p class="red-text" v-if="isIncorrectField(username)">A username must be entered!</p>
          </div>
          <!-- Password error (empty) -->
          <div class="form-row">
            <p class="red-text" v-if="isIncorrectField(password)">A password must be entered!</p><br><br>
          </div>
          <!-- Button for login and link to register-->
          <div class="form-row">
            <button class="btn btn-block btn-primary" style="width: 100%; margin:0 20px" @click="login">Login</button><br>
            <p  style="width: 100%; margin:0 20px; text-align: center">Dont have an account? <router-link class="link-text" to="/register">Register here</router-link></p>
          </div>


        </div>
      </div>
    </logout-required>
  </div>
</template>

<script>
  import LogoutRequired from "./LogoutRequired";
  import Alert from "./Alert"

  const LoginPage = {
    name: "LoginPage",
    data () {
      return {
        username: '',
        password: '',
        error: null,
        showMissingFields: false
      }
    },
    components: {
      LogoutRequired,
      Alert
    },
    methods: {
      /**
       * Login logic, checks that there are no missing fields, attempts to use login endpoint otherwise
       */
      login () {
        if (this.username === '' || this.password === '') {
          this.showMissingFields = true
        } else {
          this.$root.$data.user.login(this.username, this.password)
                  .then(() => {
                    this.$router.push({name: 'user'})
                  })
                  .catch((err) => {
                    this.error = err;
                    this.showMissingFields = false
                  })
        }
      },
      /**
       * Checks to see if fields are missing
       * @param field
       * @returns {boolean}
       */
      isIncorrectField (field) {
        return this.showMissingFields && (field === null || field === '')
      },
      //WARNING 'input-red' never used
      /**
       * Checks to see if field is incorrect
       * @param field
       * @returns {string}
       */
      inputClass (field) {
        return (this.isIncorrectField(field) ? 'input-red' : 'input')
      }
    }
  };

  export default LoginPage;

</script>

<style scoped>

  .link-text {
    color: blue;
    cursor: pointer;
    margin-right: 10px;
  }

  .required {
    color: red;
    display: inline;
  }

  .input-red {
    border:1px solid #f00;
  }

  .red-text {
    color: red;
    margin: 0;
  }
</style>
