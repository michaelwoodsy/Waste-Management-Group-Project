<!-- User thumbnail and name for choosing who to act as in navbar -->

<template>
  <div class="btn-group">
    <!-- Image and name -->
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
</template>

<script>
export default {
  name: "UserProfileLinks",
  computed: {
    /** Returns the users profile url **/
    userProfileRoute () {
      return `users/${this.$root.$data.user.state.userId}`;
    },

    /** Returns the current logged in users data **/
    userData () {
      return this.$root.$data.user.state.userData
    }
  },
  methods: {
    /** Logs the user out **/
    logOut() {
      this.$root.$data.user.logout();
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
</style>