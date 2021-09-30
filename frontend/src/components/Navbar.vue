<template>
  <nav class="navbar navbar-expand-md navbar-light bg-primary p-0">
    <div class="col">

      <!-- Nav Bar -->
      <button aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation" class="navbar-toggler"
              data-target="#navbarText" data-toggle="collapse" type="button">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div id="navbarText" class="collapse navbar-collapse">
        <ul class="navbar-nav align-items-baseline">
          <!-- Profile photo -->
          <li class="nav-item">
            <router-link :to="imageClickRoute" class="nav-link p-0">
              <img
                  alt="profile"
                  class="logo-image"
                  src="../../public/logo.png"
              />
            </router-link>

          </li>

          <!-- If user is logged in -->
          <!-- Home link -->
          <li v-if="isLoggedIn" class="nav-item">
            <router-link class="nav-link text-white" to="/home">Home</router-link>
          </li>
          <!-- Users search link -->
          <li v-if="isLoggedIn" class="nav-item">
            <router-link class="nav-link text-white ml-2" to="/search">Search</router-link>
          </li>
          <!-- Marketplace link -->
          <li v-if="isLoggedIn" class="nav-item">
            <router-link class="nav-link text-white ml-2" to="/marketplace">Marketplace</router-link>
          </li>
          <!-- Browse sale listings link -->
          <li v-if="isLoggedIn" class="nav-item">
            <router-link class="nav-link text-white ml-2" to="/listings">Browse Sale Listings</router-link>
          </li>
        </ul>

        <!-- Logged in user links -->
        <ul v-if="isLoggedIn" class="navbar-nav">
          <li class="nav-item">
            <user-profile-links/>
          </li>
          <li class="nav-item">
            <span v-if="this.$root.$data.user.isGAA() && this.$root.$data.user.isActingAsUser()"
                  class="badge badge-danger">ADMIN</span>
            <span v-else-if="this.$root.$data.user.isDGAA() && this.$root.$data.user.isActingAsUser()"
                  class="badge badge-danger">DGAA</span>
          </li>
        </ul>
        <!-- If not logged in, Login and register link -->
        <ul v-else class="navbar-nav text-white">
          <li class="nav-item">
            <router-link class="nav-link text-white" to="/login">Login</router-link>
          </li>
          <li class="nav-item">/</li>
          <li class="nav-item">
            <router-link class="nav-link text-white" to="/register">Register</router-link>
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

    /**
     * Gets the users' ID
     * @returns {any}
     */
    userId() {
      return this.$root.$data.user.state.userId
    },

    /**
     * Checks if user is acting as a user
     * @returns {boolean|*}
     */
    isActingAsUser() {
      return this.$root.$data.user.isActingAsUser()
    },

    /** Returns the landing page url if the user is not logged in and the users home page if they are logged in **/
    imageClickRoute() {
      if (this.$root.$data.user.state.loggedIn) {
        return "home";
      } else {
        return "/";
      }
    }
  }
}
</script>

<style scoped>

.logo-image {
  height: 60px;
  margin-right: 5px;
}

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