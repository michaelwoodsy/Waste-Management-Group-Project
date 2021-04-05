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
                  <div v-if="isLoggedIn" class="btn-group">
                    <span class="float-right d-inline pointer"  data-toggle="dropdown">
                      <!-- Profile photo -->
                      <img
                          class="img-fluid profile-image rounded-circle mr-1"
                          alt="profile"
                          src="../../public/profile.png"
                      >
                      <!-- Users name -->
                      {{ userData.firstName }} {{ userData.lastName }}
                    </span>

                    <!-- Dropdown menu when name is clicked -->
                        <div class="dropdown-menu dropdown-menu-left dropdown-menu-sm-right">
                          <!-- Change account menu -->
                          <h6 class="dropdown-header">Associated Businesses</h6>
                          <a class="dropdown-item" href="#">
                            <img class="profile-image-sm rounded-circle mb-1" alt="profile"
                                 src="../../public/profile.png">
                            Business Account
                          </a>

                          <a class="dropdown-item" href="#">
                            <img class="profile-image-sm rounded-circle mb-1" alt="profile"
                                 src="../../public/profile.png">
                            Business Account 2
                          </a>

                          <a class="dropdown-item" href="#">
                            <img class="profile-image-sm rounded-circle mb-1" alt="profile"
                                 src="../../public/profile.png">
                            Business Account 3
                          </a>

                          <!-- Profile and logout section -->
                          <div class="dropdown-divider"></div>
                          <router-link class="dropdown-item" :to="userProfileRoute">My Profile</router-link>
                          <router-link class="dropdown-item" @click.native="logOut()" to="/login">Logout</router-link>
                        </div>
                  </div>

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
    export default {
        name: "Navbar",
        computed: {
          /**
           * Checks if user is logged in
           * @returns {boolean|*}
           */
          isLoggedIn () {
            return this.$root.$data.user.state.loggedIn
          },

          /** Returns the current logged in users data **/
          userData () {
            return this.$root.$data.user.state.userData
          },

          /**
           * Returns the users profile url
           * @returns {string}
           */
          userProfileRoute () {
            return `users/${this.$root.$data.user.state.userId}`;
          }
        },
        methods: {
          /**
           * Logs the user out
           */
          logOut() {
            this.$root.$data.user.logout();
          },

          /** Opens the profile links under user image & name **/
          openProfileLinks () {

          }
        }
    }
</script>

<style scoped>
.profile-image {
  height: 35px;
}

.profile-image-sm {
  height: 20px;
}

/*.dropdown-container {*/
/*  position: relative;*/
/*}*/

/*.dropdown-menu {*/
/*  position: absolute;*/
/*  top: 100%; !* Bottom of button *!*/
/*  right: 0;*/
/*}*/
</style>
