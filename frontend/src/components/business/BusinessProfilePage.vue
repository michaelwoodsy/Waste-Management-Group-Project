<template>
  <page-wrapper col-size="10">

    <login-required
        v-if="!isLoggedIn"
        page="view a business's profile page"
    />

    <div v-else-if="business">

      <div class="row mb-2">
        <div class="col-2"/>
        <div class="col text-center">
          <h2 class="mb-0">{{ business.name }}</h2>
        </div>
        <div class="col-2 text-right">
          <router-link v-if="isBusinessAdmin || user.canDoAdminAction()"
                       :class="{'btn-primary': isBusinessAdmin, 'btn-danger': !isBusinessAdmin && user.canDoAdminAction()}"
                       :to="`businesses/${business.id}/edit`" class="btn btn-primary"
          >
            Edit Business
          </router-link>
        </div>
      </div>
      <hr>

      <business-profile :business="business"
                        :is-business-admin="isBusinessAdmin"
                        :is-primary-admin="isPrimaryAdmin"
                        :read-only="false"
                        @update-data="getBusiness"
      />

    </div>

  </page-wrapper>
</template>

<script>

import {Business} from '@/Api';
import userState from "@/store/modules/user"
import LoginRequired from "../LoginRequired";
import PageWrapper from "@/components/PageWrapper";
import BusinessProfile from "@/components/business/BusinessProfile";

export default {
  name: "BusinessProfilePage",
  data() {
    return {
      user: userState,
      business: null,
    }
  },
  async mounted() {
    await this.getBusiness()
  },
  computed: {
    /**
     * Gets the business ID
     * @returns {any}
     */
    businessId() {
      return this.$route.params.businessId;
    },

    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    /**
     * Returns true if the logged in user is a business admin
     */
    isBusinessAdmin() {
      for (const user of this.business.administrators) {
        if (Number(user.id) === Number(this.user.state.userId)) {
          return true
        }
      }
      return false
    },

    /**
     * Returns true if the currently logged in user is the primary admin of the business
     */
    isPrimaryAdmin() {
      return this.isBusinessAdmin &&
          Number(this.business.primaryAdministratorId) === Number(this.user.state.userId)
    }
  },

  watch: {
    /**
     * Called when the businessId is changed, this occurs when the path variable for the business id is updated
     */
    async businessId(value) {
      if (value !== undefined) {
        await this.getBusiness()
      }
    }
  },

  components: {
    BusinessProfile,
    PageWrapper,
    LoginRequired
  },
  methods: {

    /**
     * Gets business data from the backend
     */
    async getBusiness() {
      try {
        const response = await Business.getBusinessData(this.businessId)
        this.$set(this.$data, 'business', response.data)
      } catch (error) {
        console.error(error)
      }
    }

  }
}

</script>
