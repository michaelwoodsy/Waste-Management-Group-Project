<template>
  <div class="modal-content">

    <div class="modal-header">
      <div class="col-2 text-left">
        <button v-if="showBack" class="btn btn-secondary" @click="$emit('back')">Back</button>
      </div>
      <div class="col text-center">
        <h2 class="mb-0">{{ business.name }}</h2>
      </div>
      <div class="col-2 text-right">
        <button id="closeModalButton" aria-label="Close" class="close" data-dismiss="modal" type="button" @click="$emit('close-modal')">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
    </div>

    <div class="modal-body">
      <business-profile @close-modal="closeModal"
                        :business="business"
                        :is-business-admin="isBusinessAdmin"
                        :is-primary-admin="isPrimaryAdmin"
                        :read-only="false"/>
    </div>

  </div>
</template>

<script>
import BusinessProfile from "@/components/business/BusinessProfile";
import userState from "@/store/modules/user"

export default {
  name: "BusinessProfilePageModal",
  components: {BusinessProfile},
  props: {
    business: {
      type: Object,
      required: true
    },
    showBack: {
      type: Boolean,
      required: false,
      default: false
    },
  },
  data() {
    return {
      user: userState,
    }
  },
  computed: {
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
      return (this.isBusinessAdmin &&
          Number(this.business.primaryAdministratorId) === Number(this.user.state.userId)) || this.$root.$data.user.canDoAdminAction()
    }
  },
  methods: {
    /**
     * Closes the business profile modal by simulating a click on the close button.
     */
    closeModal() {
      document.getElementById("closeModalButton").click()
    }
  }
}
</script>

<style scoped>

</style>