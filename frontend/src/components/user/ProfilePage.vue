<template>
  <page-wrapper col-size="10">

    <login-required
        v-if="!user.isLoggedIn()"
        page="view a user's profile page"
    />

    <div v-else>

      <div class="row">
        <div class="col-2"/>
        <div class="col text-center">
          <h2 class="mb-0">{{ firstName }} {{ lastName }}
            <span v-if="isGAA && user.canDoAdminAction()" class="badge badge-danger admin-badge">ADMIN</span>
            <span v-else-if="isDGAA && user.canDoAdminAction()" class="badge badge-danger admin-badge">DGAA</span>
          </h2>
        </div>
        <div class="col-2 text-right">
          <!-- Edit button -->
          <router-link v-if="isViewingSelf || user.canDoAdminAction()"
                       :class="{'btn-primary': isViewingSelf, 'btn-danger': !isViewingSelf && user.canDoAdminAction()}"
                       :to="`users/${userId}/edit`"
                       class="btn btn-primary"
          >
            Edit Profile
          </router-link>
        </div>
      </div>
      <hr>

      <profile :user-id="userId"
               @user-data-obtained="setInfo"
      />

    </div>

  </page-wrapper>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import PageWrapper from "@/components/PageWrapper";
import Profile from "@/components/user/Profile";
import userState from "@/store/modules/user"

export default {
  name: "ProfilePage",
  components: {Profile, PageWrapper, LoginRequired},
  data() {
    return {
      user: userState,
      firstName: null,
      lastName: null,
      role: null
    }
  },
  computed: {
    /**
     * Gets the users' ID
     * @returns {any} userId from path
     */
    userId() {
      return this.$route.params.userId
    },

    /**
     * Returns true if the user is currently viewing their profile page
     * @returns {boolean|*}
     */
    isViewingSelf() {
      return this.userId.toString() === this.$root.$data.user.state.userId.toString()
    },

    /**
     * Returns true if user is GAA
     */
    isGAA() {
      return this.role === "globalApplicationAdmin"
    },

    /**
     * Returns true if user is DGAA
     */
    isDGAA() {
      return this.role === "defaultGlobalApplicationAdmin"
    }

  },
  methods: {
    /**
     * Sets the user's first and last name
     *
     * @param userInfo user info from the profile component
     */
    setInfo(userInfo) {
      this.firstName = userInfo.firstName
      this.lastName = userInfo.lastName
      this.role = userInfo.role
    }
  }
}
</script>

<style scoped>

</style>