<template>
  <div>

    <!-- Message Card -->
    <div class="card mb-2">
      <div class="card-body px-3 py-2">
        <strong>{{ message.sender.firstName }} {{ message.sender.lastName }}</strong><br/>
        <span class="small">Re: {{ message.card.title }}</span><br/>
        <span class="small">Sent: {{ formattedDateTime }}</span>
      </div>
      <div class="card-footer p-2 text-right">
        <button id="openMessage" :data-target="`#fullMessage${message.id}`"
                class="btn btn-sm btn-primary" data-toggle="modal">
          View
        </button>
        <button id="deleteMessageButton" class="btn btn-sm btn-danger ml-2" data-toggle="modal" :data-target="'#deleteMessage' + message.id">
          <em class="bi bi-trash"/>
        </button>
      </div>
    </div>

    <!-- Delete Message Modal -->
    <confirmation-modal :modal-header="'Delete Message From: ' + message.sender.firstName + ' ' + message.sender.lastName"
                        :modal-id="'deleteMessage' + message.id"
                        modal-confirm-colour="btn-danger"
                        modal-confirm-text="Delete"
                        modal-dismiss-text="Cancel"
                        modal-message="Are you sure you wish to delete this message?"
                        @confirm="removeMessage(message.id)"/>

    <!-- Full Message Modal -->
    <div :id="`fullMessage${message.id}`" class="modal fade" role="dialog" tabindex="-1">
      <div class="modal-dialog" role="document">
        <div class="modal-content">

          <div class="modal-header">
            <div>
              <h5 class="modal-title p-0">Message from: {{ message.sender.firstName }} {{
                  message.sender.lastName
                }}</h5>
              <span class="text-muted">Re: {{ message.card.title }}</span> <br>
              <span class="text-muted">Sent: {{ formattedDateTime }}</span>
            </div>
            <button aria-label="Close" class="close" data-dismiss="modal" type="button">
              <span ref="close" aria-hidden="true">&times;</span>
            </button>
          </div>

          <div class="modal-body">
            {{ message.text }}
          </div>

          <!-- Footer with reply area -->
          <div class="modal-footer">
            <div class="input-group mb-2 text-right">
              <textarea v-model="reply" :class="{'is-invalid': replyError, 'is-valid': replySent}"
                        class="form-control" maxlength="255" placeholder="Enter a reply"/>
              <span v-if="replyError" class="invalid-feedback">Please enter a reply to send</span>
              <span v-if="replySent" class="valid-feedback">Reply sent!</span>
            </div>
            <button :disabled="!reply"
                    class="btn btn-sm btn-primary"
                    @click="sendReply">
              Send Reply
            </button>
          </div>

        </div>
      </div>
    </div>

  </div>
</template>

<script>
import {User} from "@/Api";
import $ from 'jquery'
import user from "@/store/modules/user";
import ConfirmationModal from "@/components/ConfirmationModal";
import {formatDateTime} from "@/utils/dateTime";

export default {
  name: "Message",
  components: {ConfirmationModal},
  props: {
    message: Object
  },
  computed: {
    /**
     * Returns the formatted date-time of when the message was created
     */
    formattedDateTime() {
      return formatDateTime(this.message.created)
    }
  },
  mounted() {
    $(`#fullMessage${this.message.id}`).on('hidden.bs.modal', () => {
      this.clearReply()
    })
  },
  data() {
    return {
      reply: null,
      replyError: false,
      replySent: false
    }
  },
  methods: {
    /**
     * Sends a reply to the sender of the original message
     */
    async sendReply() {
      if (!this.reply) {
        this.replyError = true
      } else {
        try {
          await User.sendCardMessage(this.message.sender.id, this.message.card.id, this.reply)
          this.replyError = false
          this.replySent = true
          this.reply = null
        } catch (error) {
          console.error(error)
        }
      }
    },

    /**
     * Clears the reply when modal is closed
     */
    clearReply() {
      this.reply = null
      this.replyError = false
      this.replySent = false
    },

    /**
     * Emits an event to delete the message
     */
    async removeMessage(messageId) {
      try {
        await User.deleteMessage(user.actingUserId(), messageId)
        this.$emit('remove-message');

      } catch (error) {
        console.log(error)
      }
    },
  }
}
</script>

<style scoped>

</style>