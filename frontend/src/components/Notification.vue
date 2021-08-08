<template>
  <div>

    <!-- Notification Toast -->
    <div aria-atomic="true" aria-live="assertive" class="toast hide mb-2" data-autohide="false" role="alert">
      <div :class="{'bg-danger text-light': isAdminNotification}" class="toast-header">
        <small class="ml-auto">{{ formattedDateTime }}</small>
        <button id="closeNotification"
                aria-label="Close"
                class="ml-2 close"
                data-dismiss="toast"
                type="button"
                @click="removeNotification">
          <span :class="{'text-light': isAdminNotification}" aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="toast-body">
        <div class="mb-2">{{ data.message }}</div>

        <!-- Card Expiry Notification Body -->
        <div v-if="data.type === 'cardExpiry'">
          Card: {{ data.cardTitle }}
        </div>

        <!-- New Keyword Notification Body -->
        <div v-if="data.type === 'newKeyword'" class="text-right">
          <button :data-target="'#deleteKeyword' + data.keyword.id"
                  class="btn btn-sm btn-outline-danger"
                  data-toggle="modal"
          >
            Delete Keyword
          </button>
        </div>

      </div>
    </div>

    <!-- Delete Keyword Modal -->
    <div v-if="data.type === 'newKeyword'">
      <confirmation-modal :modal-header="'Delete Keyword: ' + data.keyword.name"
                          :modal-id="'deleteKeyword' + data.keyword.id"
                          modal-confirm-colour="btn-danger"
                          modal-confirm-text="Delete"
                          modal-dismiss-text="Cancel"
                          modal-message="Are you sure you wish to delete this keyword?"
                          @confirm="deleteKeyword"/>
    </div>

  </div>
</template>

<script>
import {formatDateTime} from "@/utils/dateTime";
import ConfirmationModal from "@/components/ConfirmationModal";
import {Keyword, User} from "@/Api";
import user from "@/store/modules/user"
import undo from "@/utils/undo"

const adminTypes = ['admin', 'newKeyword']

export default {
  name: "Notification",
  components: {ConfirmationModal},
  props: {
    data: {
      type: Object,
      required: true
    }
  },
  computed: {
    /**
     * Returns the formatted date-time of when the notification was created
     */
    formattedDateTime() {
      return formatDateTime(this.data.created)
    },
    /**
     * Returns true if the notification is an admin notification, false otherwise
     */
    isAdminNotification() {
      return adminTypes.includes(this.data.type)
    }
  },
  methods: {
    /**
     * Emits an event to delete the notification
     */
    async removeNotification() {
      try {
        let request;

        // create request function
        if (this.isAdminNotification) {
          request = async () => {await User.deleteAdminNotification(this.data.id)}
        } else {
          request = async () => {await User.deleteNotification(user.actingUserId(), this.data.id)}
        }

        // register the request in the undo queue
        let undoHandle = () => {
          // Nothing for now
        }
        undo.queueNotificationDelete(request, this.data, undoHandle)
        this.$emit('remove-notification');

      } catch (error) {
        console.log(error)
      }
    },
    /**
     * Deletes the keyword and notification
     */
    async deleteKeyword() {
      try {
        await this.removeNotification()
        await Keyword.deleteKeyword(this.data.keyword.id)
      } catch (error) {
        console.log(error)
      }
    }
  }
}
</script>

<style scoped>

.toast {
  max-width: 100%;
}

</style>