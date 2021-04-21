<template>
  <nav class="navbar navbar-expand-md navbar-light bg-light">
    <div class="col-12 col-md-10">

      <!-- Nav Bar -->
      <button aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation" class="navbar-toggler"
              data-target="#navbarText" data-toggle="collapse" type="button">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div id="navbarText" class="collapse navbar-collapse">
        <ul class="navbar-nav align-items-baseline">
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

        <!-- Logged in user links -->
        <ul v-if="isLoggedIn" class="navbar-nav">
          <li class="nav-item">
            <router-link v-if="isLoggedIn" :to="profileRoute" class="nav-link">Profile</router-link>
          </li>
          <li class="nav-item">
            <user-profile-links/>
          </li>
        </ul>
        <!-- If not logged in, Login and register link -->
        <ul v-else class="navbar-nav">
          <li class="nav-item">
            <router-link class="nav-link" to="/login">Login</router-link>
          </li>
          <li class="nav-item">/</li>
          <li class="nav-item">
            <router-link class="nav-link" to="/register">Register</router-link>
          </li>
        </ul>
      </div>

    </div>
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

.navbar {
  justify-content: center;
}

.navbar-collapse {
  justify-content: space-between;
  align-items: baseline;
}

.navbar-nav {
  align-items: baseline;
}

</style>