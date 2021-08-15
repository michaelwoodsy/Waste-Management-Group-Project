<template>
  <div :class="{'pointer': unread}">

    <!-- Notification Toast -->
    <div aria-atomic="true"
         aria-live="assertive" class="toast hide mb-2" data-autohide="false"
         role="alert" @click="readNotification"
    >
      <div :class="{'bg-danger text-light': isAdminNotification}" class="toast-header">
        <span v-if="unread" class="badge badge-pill badge-dark">NEW</span>
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

        <div v-if="data.type === 'purchase'">
          <p><strong>Pickup from:</strong>
            <br>
            {{formattedAddress}}</p>

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
    },
    unread: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      deletingKeyword: false
    }
  },
  computed: {
    /**
     * Returns the formatted date-time of when the notification was created
     */
    formattedDateTime() {
      return formatDateTime(this.data.created)
    },
    formattedAddress() {
      return this.$root.$data.address.formatAddressWithStreet(this.data.address)
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
     * Sends request to read notification and emits event to parent component to update notification
     */
    async readNotification() {
      if (this.unread) {
        try {
          if (this.isAdminNotification) {
            await User.readAdminNotification(this.data.id, true)
          } else {
            await User.readNotification(user.actingUserId(), this.data.id, true)
          }
          this.$emit('read-notification')
        } catch (error) {
          console.log(error)
        }
      }
    },
    /**
     * Sends request to delete a notification and emits an event to delete the notification
     */
    async removeNotification() {
      try {
        let request;

        // create request function
        if (this.isAdminNotification) {
          if (this.deletingKeyword) {
            request = async () => {await User.deleteAdminNotification(this.data.id).then(async () => {
              await Keyword.deleteKeyword(this.data.keyword.id)
            })}
          }else {
            request = async () => {await User.deleteAdminNotification(this.data.id)}
          }
        } else {
          request = async () => {await User.deleteNotification(user.actingUserId(), this.data.id)}
        }

        // register the request in the undo queue
        undo.queueNotificationDelete(request, this.data)
        this.$emit('remove-notification');

      } catch (error) {
        console.log(error)
      }
    },
    /**
     * Deletes the keyword and notification
     */
    async deleteKeyword() {
      this.deletingKeyword = true
      try {
        await this.removeNotification()
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