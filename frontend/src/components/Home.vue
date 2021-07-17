<template>
  <div class="container-fluid">

    <login-required
        v-if="!user.isLoggedIn()"
        page="view your profile page"
    />

    <div v-else class="row justify-content-between min-vh-100">

      <!-- Side Bar Left-->
      <div class="col-md-3 col-lg-2 p-3 bg-dark shadow">
        <div>
          <h4 class="text-light">Quick Links</h4>
          <ul class="nav flex-column">
            <li class="nav-item mb-2">
              <router-link class="btn btn-block btn-primary" to="/marketplace">Marketplace</router-link>
            </li>
            <li class="nav-item mb-2">
              <router-link :to="`users/${user.state.userId}/edit`"
                           class="btn btn-block btn-primary">Edit Profile
              </router-link>
            </li>

          </ul>
        </div>
        <!-- Links to display if acting as business -->
        <div v-if="user.isActingAsBusiness()">
          <br>
          <h4 class="text-light">My Business</h4>
          <ul class="nav flex-column">
            <li class="nav-item mb-2">
              <router-link :to="`businesses/${user.actor().id}/products`"
                           class="btn btn-block btn-primary">Product Catalogue
              </router-link>
            </li>
            <li class="nav-item mb-2">
              <router-link :to="`businesses/${user.actor().id}/inventory`"
                           class="btn btn-block btn-primary">Inventory
              </router-link>
            </li>
            <li class="nav-item mb-2">
              <router-link :to="`businesses/${user.actor().id}/listings`"
                           class="btn btn-block btn-primary">Sale Listings
              </router-link>
            </li>
          </ul>
        </div>
      </div>

      <!-- Page Content -->
      <div class="col-12 col-md-8 p-3">
        <div class="text-center">
          <h1><span v-if="user.isActingAsUser()">Hello </span>{{ user.actor().name }}</h1>
          <hr>
        </div>
        <!-- Cards Section -->
        <div v-if="user.isActingAsUser()">
          <h2>My Cards</h2>
          <alert v-if="hasExpiredCards" class="text-center">
            You have cards that have recently expired and will be deleted within 24 hours if not extended!
          </alert>
          <div v-if="hasExpiredCards">
            <h5>Recently Expired Cards</h5>
            <div class="row row-cols-1 row-cols-lg-2">
              <div v-for="card in expiredCards" v-bind:key="card.id" class="col">
                <market-card :card-data="card" :hide-image="hideImages" :show-expired="true"
                             @card-deleted="deleteCard" @card-extended="extendCard"></market-card>
              </div>
            </div>
          </div>
          <h5 v-if="hasExpiredCards">Active Cards</h5>
          <div class="row row-cols-1 row-cols-lg-2">
            <div v-for="card in activeCards" v-bind:key="card.id" class="col">
              <market-card :card-data="card" :hide-image="hideImages" :show-expired="true"
                           @card-deleted="deleteCard" @card-extended="extendCard"></market-card>
            </div>
          </div>
        </div>
      </div>

      <!-- Side Bar Right-->
      <div class="col-md-3 col-lg-2 p-3 bg-dark shadow">
        <div>
          <h4 class="text-light">Notifications</h4>

          <!-- Toggle Notifications/Messages Buttons -->
          <div class="btn-group btn-block">
            <button id="showNotificationsButton"
                    :class="{'btn-primary': notificationsShown, 'btn-outline-primary': !notificationsShown}"
                    class="btn" style="width: 50%" type="button" @click="showNotifications">
              <em class="bi bi-bell"/>
              <span v-if="notifications.length > 0" class="badge badge-pill badge-light ml-1">
                <span v-if="notifications.length < 10">{{ notifications.length }}</span>
                <span v-else>9+</span>
              </span>
            </button>
            <button id="showMessagesButton"
                    :class="{'btn-primary': !notificationsShown, 'btn-outline-primary': notificationsShown}"
                    class="btn" style="width: 50%" type="button" @click="showMessages">
              <em class="bi bi-envelope"/>
              <span v-if="messages.length > 0" class="badge badge-pill badge-light ml-1">
                <span v-if="messages.length < 10">{{ messages.length }}</span>
                <span v-else>9+</span>
              </span>
            </button>
          </div>
        </div>
        <br>

        <!-- Notifications -->
        <div v-if="notificationsShown">
          <div v-if="notifications.length === 0">
            <p class="text-light">You have no notifications</p>
          </div>
          <div v-else>
            <notification v-for="notification in sortedNotifications"
                          :key="notification.id"
                          :data="notification"
                          @remove-notification="removeNotification(notification.id)"/>

          </div>
        </div>

        <div v-else>
          <div v-if="messages.length === 0">
            <p class="text-light">You have no messages</p>
          </div>
          <div v-else>
            <message v-for="message in sortedMessages"
                     :key="message.id"
                     :message="message"
                      @remove-message="removeMessage(message.id)"/>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>
<script>
import LoginRequired from "./LoginRequired";
import MarketCard from "@/components/marketplace/MarketCard";
import Alert from "@/components/Alert";
import Notification from "@/components/Notification";
import {User} from "@/Api";
import userState from "@/store/modules/user"
import $ from 'jquery';
import Message from "@/components/marketplace/Message";

export default {
  name: "Home",
  components: {
    Message,
    Alert,
    LoginRequired,
    MarketCard,
    Notification
  },
  async mounted() {
    await this.getCardData()
    await this.getNotificationData()
    if (this.user.canDoAdminAction()) {
      await this.getAdminNotifications();
    }
    await this.getMessages()
    $('.toast').toast('show')
  },
  data() {
    return {
      user: userState,
      cards: [],
      hideImages: true,
      notificationsShown: true,
      //Test data
      notifications: [],
      adminNotifications: [],
      messages: [],
      error: ""
    }
  },
  computed: {
    /**
     * Returns true if a user has expired cards
     */
    hasExpiredCards() {
      for (const card of this.cards) {
        if (this.expired(card)) {
          return true
        }
      }
      return false
    },

    /**
     * Computed property that returns all expired cards.
     */
    expiredCards() {
      const cards = []
      for (const card of this.cards) {
        const cardExpiryDate = new Date(card.displayPeriodEnd)
        if (cardExpiryDate < Date.now()) {
          cards.push(card)
        }
      }
      return cards
    },

    /**
     * Computed property that returns all active cards.
     */
    activeCards() {
      const cards = []
      for (const card of this.cards) {
        const cardExpiryDate = new Date(card.displayPeriodEnd)
        if (cardExpiryDate > Date.now()) {
          cards.push(card)
        }
      }
      return cards
    },

    /**
     * Returns notifications sorted by most recent.
     */
    sortedNotifications() {
      let sortedNotifications = [...this.notifications]
      sortedNotifications.sort((a, b) => (new Date(a.created) > new Date(b.created)) ? -1 : 1)
      return sortedNotifications
    },

    /**
     * Returns messages sorted by most recent.
     */
    sortedMessages() {
      let sortedMessages = [...this.messages]
      sortedMessages.sort((a, b) => (new Date(a.created) > new Date(b.created)) ? -1 : 1)
      return sortedMessages
    }

  },
  methods: {
    /**
     * Displays the notifications section
     */
    async showNotifications() {
      this.notificationsShown = true
      await this.$nextTick()
      $('.toast').toast('show')
    },

    /**
     * Displays the messages section
     */
    async showMessages() {
      this.notificationsShown = false
      await this.$nextTick()
      $('.toast').toast('show')
    },

    /**
     * Gets the user's cards so we can check for ones about to expire
     */
    async getCardData() {
      try {
        const response = await User.getCards(this.user.actor().id)
        this.cards = response.data
      } catch (error) {
        console.error(error)
        this.error = error
      }
    },

    /**
     * Gets the user's notifications
     */
    async getNotificationData() {
      try {
        const response = await User.getNotifications(this.user.actor().id)
        this.notifications = response.data
      } catch (error) {
        console.error(error)
        this.error = error
      }
    },

    /**
     * Gets all admin notifications
     */
    async getAdminNotifications() {
      try {
        const response = await User.getAdminNotifications()
        this.notifications.push(...response.data)
      } catch (error) {
        console.error(error)
        this.error = error
      }
    },

    /**
     * Gets the currently logged in user's messages
     */
    async getMessages() {
      try {
        const response = await User.getMessages(this.user.actor().id)
        this.messages = response.data
        console.log(this.messages)
      } catch (error) {
        console.error(error)
        this.error = error
      }
    },

    /**
     * Returns true if a card has expired
     */
    expired(card) {
      const now = new Date();
      if (now >= new Date(card.displayPeriodEnd)) {
        return true;
      }
    },
    /**
     * Deletes a card from the list of cards once it has been deleted from the server
     * @param id
     */
    deleteCard(id) {
      for (let index = 0; index < this.cards.length; index++) {
        if (this.cards[index].id === id) {
          this.cards.splice(index, 1)
        }
      }
      //Refreshes messages as when deleting a card messages about that card would have been deleted.
      this.getMessages()
    },
    /**
     * Updates an extended cards information.
     * @param id ID of card to update
     * @param newDate the new date to set on the card
     */
    extendCard(id, newDate) {
      for (const [index, card] of this.cards.entries()) {
        if (card.id === id) {
          this.cards[index].displayPeriodEnd = newDate
        }
      }
    },
    /**
     * Remove a notification from the list of visible notifications
     * @param notificationId the id of the notification that is to be removed
     */
    removeNotification(notificationId) {
      // Remove the notification from the list that is shown
      for (const [index, notification] of this.notifications.entries()) {
        if (notification.id === notificationId) {
          this.notifications.splice(index, 1)
        }
      }
    },

    /**
     * Remove a message from the list of visible messages
     * @param messageId the id of the message that is to be removed
     */
    removeMessage(messageId) {
      // Remove the message from the list that is shown
      for (const [index, message] of this.messages.entries()) {
        if (message.id === messageId) {
          this.messages.splice(index, 1)
        }
      }
    }
  }
}

</script>

<style scoped>

</style>