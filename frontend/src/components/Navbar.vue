<template>
  <nav class="navbar navbar-expand-md navbar-light bg-primary p-0">
    <div class="col-12 col-md-10">

      <!-- Nav Bar -->
      <button aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation" class="navbar-toggler"
              data-target="#navbarText" data-toggle="collapse" type="button">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div id="navbarText" class="collapse navbar-collapse">
        <ul class="navbar-nav align-items-baseline">
          <!-- Profile photo -->
          <li class="nav-item">
            <router-link class="nav-link p-0" :to="imageClickRoute">
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
            <router-link class="nav-link text-white" to="/users/search">Users</router-link>
          </li>
          <!-- Marketplace link -->
          <li v-if="isLoggedIn" class="nav-item">
            <router-link class="nav-link text-white" to="/marketplace">Marketplace</router-link>
          </li>
        </ul>

        <!-- Logged in user links -->
        <ul v-if="isLoggedIn" class="navbar-nav">
          <li class="nav-item">
            <router-link v-if="isLoggedIn" :to="profileRoute" class="nav-link text-white">Profile</router-link>
          </li>
          <li class="nav-item">
            <user-profile-links/>
          </li>
          <li class="nav-item">
            <span class="badge badge-danger"
                  v-if="this.$root.$data.user.isGAA() && this.$root.$data.user.isActingAsUser()">ADMIN</span>
            <span class="badge badge-danger"
                  v-else-if="this.$root.$data.user.isDGAA() && this.$root.$data.user.isActingAsUser()">DGAA</span>
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

    /** Returns the user's profile url if acting as user,
     * or the business profile if acting as a business **/
    profileRoute() {
      if (this.$root.$data.user.state.actingAs.type === "business") {
        return `businesses/${this.$root.$data.user.state.actingAs.id}`;
      } else {
        return `users/${this.$root.$data.user.state.userId}`;
      }
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