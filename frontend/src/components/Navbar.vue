<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="d-none d-sm-block col-sm-1"></div>
        <div class="col-12 col-sm-10">
          <!-- Nav Bar -->
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarText">
                <ul class="navbar-nav mr-auto">
                  <!-- If user is logged in -->
                  <!-- Home link -->
                    <li class="nav-item" v-if="isLoggedIn">
                        <router-link class="nav-link" to="/home">Home</router-link>
                    </li>
                  <!-- Users search link -->
                    <li class="nav-item" v-if="isLoggedIn">
                        <router-link class="nav-link" to="/users/search">Users</router-link>
                    </li>
                </ul>
                <span class="navbar-text">

                  <!-- Logged in user links -->
                  <router-link class="nav-link d-inline" :to="profileRoute">Profile</router-link>
                  <user-profile-links v-if="isLoggedIn"/>

                  <!-- If not logged in, Login and register link -->
                    <span class="float-right d-inline" v-else>
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
          isLoggedIn () {
            return this.$root.$data.user.state.loggedIn
          },
          /** Returns the user's profile url if acting as user,
           * or the business profile if acting as a business **/
          profileRoute () {
            if (this.$root.$data.user.state.actingAs.type === "business") {
              return `businesses/${this.$root.$data.user.state.actingAs.id}`;
            } else {
              return `users/${this.$root.$data.user.state.userId}`;
            }

          },
        }
    }
</script>

<style scoped>
</style>
