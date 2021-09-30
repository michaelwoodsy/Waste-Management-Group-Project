<template>
  <div id="viewUserModal" class="modal fade" data-backdrop="static">
    <div class="modal-dialog modal-xl">
      <div class="modal-content">

        <div class="modal-header">
          <div class="col-2"/>
          <div class="col text-center">
            <h2 class="mb-0">{{ firstName }} {{ lastName }}
              <span v-if="isGAA && userState.canDoAdminAction()" class="badge badge-danger admin-badge">ADMIN</span>
              <span v-else-if="isDGAA && userState.canDoAdminAction()"
                    class="badge badge-danger admin-badge">DGAA</span>
            </h2>
          </div>
          <div class="col-2">
            <button aria-label="Close" class="close" data-dismiss="modal" type="button" @click="$emit('close-profile')">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
        </div>

        <div class="modal-body">
          <profile :show-edit-on-cards="false"
                   :user-id="id"
                   @user-data-obtained="setInfo"
          />
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import Profile from "@/components/user/Profile";
import userState from "@/store/modules/user"

export default {
  name: "ProfilePageModal",
  components: {Profile},
  props: {
    id: Number
  },
  data() {
    return {
      userState: userState,
      firstName: null,
      lastName: null,
      role: null
    }
  },
  computed: {

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
    },

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
