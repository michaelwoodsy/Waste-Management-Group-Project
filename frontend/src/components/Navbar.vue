<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="d-none d-sm-block col-sm-1"></div>
    <div class="col-12 col-sm-10">
      <!-- Nav Bar -->
      <button aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation" class="navbar-toggler"
              data-target="#navbarText" data-toggle="collapse" type="button">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div id="navbarText" class="collapse navbar-collapse">
        <ul class="navbar-nav mr-auto">
          <!-- If user is logged in -->
          <!-- Home link -->
          <li v-if="isLoggedIn" class="nav-item">
            <router-link class="nav-link" to="/home">Home</router-link>
          </li>
          <!-- Users search link -->
          <li v-if="isLoggedIn" class="nav-item">
            <router-link class="nav-link" to="/users/search">Users</router-link>
          </li>
        </ul>
        <span class="navbar-text">

                  <!-- Logged in user links -->
                  <span v-if="isLoggedIn">
                    <ul class="navbar-nav mr-auto">
                      <li class="nav-item">
                        <router-link v-if="isLoggedIn" class="nav-link" :to="profileRoute">Profile</router-link>
                      </li>
                      <li>
                        <user-profile-links/>
                      </li>
                    </ul>
                  </span>

          <!-- If not logged in, Login and register link -->
                    <span v-else class="float-right d-inline">
                        <router-link class="nav-link d-inline" to="/login">Login</router-link> /
                        <router-link class="nav-link d-inline" to="/register">Register</router-link>
                    </span>
                </span>
      </div>

    </div>
    <div class="d-none d-sm-block col-sm-1"></div>

  </nav>

</template>

<script>
import UserProfileLinks from '@/components/UserProfileLinks'

export default {
  name: "Navbar",
  components: {
    UserProfileLinks
  },
  computed: {
    /**
     * Checks if user is logged in
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },
    /** Returns the user's profile url if acting as user,
     * or the business profile if acting as a business **/
    profileRoute() {
      if (this.$root.$data.user.state.actingAs.type === "business") {
        return `businesses/${this.$root.$data.user.state.actingAs.id}`;
      } else {
        return `users/${this.$root.$data.user.state.userId}`;
      }

    }
  }
}
</script>

<style scoped>
</style>
