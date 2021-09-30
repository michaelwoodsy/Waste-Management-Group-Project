<!--
Search.vue
Page for searching users or businesses
-->

<template>
  <page-wrapper>
    <!-- Check the user is logged in -->
    <login-required
        v-if="!isLoggedIn"
        page="search for Users or Businesses"
    />

    <div v-else>
      <br>
      <!--    Div for search tabs    -->
      <div class="row justify-content-center mb-3">
        <div class="col col-sm-8 col-lg-5">
          <ul class="nav nav-pills nav-fill">
            <li class="nav-item">
              <a id="for-sale-link"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'Users'}"
                 class="pointer"
                 @click="changeTab('Users')">
                Users
              </a>
            </li>
            <li class="nav-item">
              <a id="wanted-link"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'Businesses'}"
                 class="pointer"
                 @click="changeTab('Businesses')">
                Businesses
              </a>
            </li>
          </ul>
        </div>
      </div>

      <UserSearch v-if="tabSelected === 'Users'"></UserSearch>
      <BusinessSearch v-if="tabSelected === 'Businesses'"></BusinessSearch>

    </div>

  </page-wrapper>
</template>

<script>
import UserSearch from "@/components/user/UserSearch";
import BusinessSearch from "@/components/business/BusinessSearch";
import LoginRequired from "@/components/LoginRequired";
import PageWrapper from "@/components/PageWrapper";
import $ from "jquery";

export default {
  name: "Search.vue",
  components: {
    PageWrapper,
    LoginRequired,
    BusinessSearch,
    UserSearch
  },
  data() {
    return {
      tabSelected: 'Users', //Default tab
    }
  },

  mounted() {
    this.checkTab()
  },

  computed: {
    /**
     * Check if user is logged in
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    }
  },

  methods: {
    /**
     *
     * @param tab what to search through e.g.
     * 'Users' or 'Businesses'
     */
    changeTab(tab) {
      this.tabSelected = tab;
      if (tab === 'Businesses') {
        // add mode=business to route query
        this.$router.push({name: "search", query: {mode: 'business', ...this.$route.query}})
      } else {
        // remove route query
        let query = this.$route.query
        delete query.mode
        this.$router.push({name: "search", query})
      }
    },

    /**
     * Checks if the correct search tab is shown based on the query parameters.
     * If the mode query param is set to business, the business search will be shown.
     */
    checkTab() {
      if (this.$route.query.mode === "business") {
        this.tabSelected = "Businesses";
      }
    }
  },
  beforeRouteLeave: (nextRoute, curRoute, next) => {
    // close all modals when leaving this page
    // this.viewListingModal = false
    $('.modal').modal('hide');
    next()
  }
}

</script>
