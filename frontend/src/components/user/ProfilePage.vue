<template>
  <page-wrapper col-size="10">

    <login-required
        v-if="!user.isLoggedIn()"
        page="view a user's profile page"
    />

    <profile v-else :user-id="userId"/>

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
      user: userState
    }
  },
  computed: {
    /**
     * Gets the users' ID
     * @returns {any} userId from path
     */
    userId() {
      return this.$route.params.userId
    }
  }
}
</script>

<style scoped>

</style>