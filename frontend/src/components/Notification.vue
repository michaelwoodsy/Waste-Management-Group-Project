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

        <!-- Purchase Notification Body -->
        <div v-if="data.type === 'purchase'">
          <p><strong>Price:</strong>
            <br>
            {{formattedPrice}}</p>

          <p><strong>Pickup from:</strong>
            <br>
            {{formattedAddress}}</p>
        </div>

        <!-- Business Review Notification Body -->
        <div v-if="data.type === 'review'">
          <p class="mb-2">Review left on sale: {{data.review.sale.inventoryItem.product.name}}</p>
          <em v-for="num in [1, 2, 3, 4, 5]" :key="num"
              :class="{'bi-star-fill': data.review.rating >= num, 'bi-star': data.review.rating < num}"
              class="icon bi mr-1"
          />
          <p v-if="data.review.reviewMessage" class="mt-2 mb-0"><strong>Their message:</strong>
            <br>
            <em>{{data.review.reviewMessage}}</em></p>
        </div>

        <!-- User Review Reply Notification Body -->
        <div v-if="data.type === 'reviewReply'">
          <p class="mb-2">Reply left on sale: {{data.review.sale.inventoryItem.product.name}}</p>
          <em v-for="num in [1, 2, 3, 4, 5]" :key="num"
              :class="{'bi-star-fill': data.review.rating >= num, 'bi-star': data.review.rating < num}"
              class="icon bi mr-1"
          />
          <p class="mt-2"><strong>Your message:</strong>
            <br>
            <em>{{data.review.reviewMessage}}</em></p>
          <p class="mb-0"><strong>Their reply:</strong>
            <br>
            <em>{{data.review.reviewReply}}</em></p>
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
import {Keyword, User, Business} from "@/Api";
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
      deletingKeyword: false,
      currency: { //Prefilled so that not null when the page loads,
        // updates when actual currency received
        symbol: "$",
        code: "USD"
      },
    }
  },
  mounted() {
    if (this.data.type === 'purchase') {
      this.getCurrency()
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
     * Formats the address of a business for picking up a listing
     */
    formattedAddress() {
      return this.$root.$data.address.formatAddressWithStreet(this.data.address)
    },
    /**
     * Returns formatted price of a listing, in the correct currency
     */
    formattedPrice() {
      return this.$root.$data.product.formatPrice(this.currency, this.data.price);
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
     * Gets the currency for a notification about a bought listing
     */
    async getCurrency() {
      this.currency = await this.$root.$data.product.getCurrency(this.data.currencyCountry)
    },
    /**
     * Sends request to read notification and emits event to parent component to update notification
     */
    async readNotification() {
      if (this.unread) {
        try {
          if (this.isAdminNotification) {
            await User.readAdminNotification(this.data.id, true)
          } else if (this.data.type === "review") {
            await Business.readNotification(user.actor().id, this.data.id, true)
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
        } else if (this.data.type === "review") {
          await Business.deleteNotification(user.actor().id, this.data.id)
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