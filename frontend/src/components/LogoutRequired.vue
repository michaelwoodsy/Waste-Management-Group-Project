<template>
  <div>
    <!-- User is already logged in -->
    <div v-if="isLoggedIn" class="row">
      <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">
        <p class="text-muted">You are already logged in. Would you like to logout instead?</p>
        <div class="form-row">
          <div class="form-group col-12">
            <!-- Log out Button -->
            <button class="btn btn-block btn-primary" @click="logout">Logout</button>
          </div>
        </div>
      </div>
    </div>

    <div v-else>
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "LogoutRequired",
  computed: {
    /**
     * Checks if user is logged in
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    }
  },

  methods: {
    /**
     * Logs the user out
     */
    logout() {
      this.$root.$data.user.logout()
          .then(() => {
            this.$router.push({path: '/'})
          })
    }
  }
}
</script>

