<template>
  <div class="container-fluid">

    <login-required
        v-if="!isLoggedIn"
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
          </ul>
        </div>
        <!-- Links to display if acting as business -->
        <div v-if="!isActingAsUser">
          <br>
          <h4 class="text-light">My Business</h4>
          <ul class="nav flex-column">
            <li class="nav-item mb-2">
              <router-link :to="productCatalogueRoute" class="btn btn-block btn-primary">Product Catalogue</router-link>
            </li>
            <li class="nav-item mb-2">
              <router-link :to="inventoryRoute" class="btn btn-block btn-primary">Inventory</router-link>
            </li>
            <li class="nav-item mb-2">
              <router-link :to="saleListingRoute" class="btn btn-block btn-primary">Sale Listings</router-link>
            </li>
          </ul>
        </div>
      </div>

      <!-- Page Content -->
      <div class="col-12 col-md-8 p-3">
        <div class="text-center">
          <h1><span v-if="isActingAsUser">Hello </span>{{ actorName }}</h1>
          <hr>
        </div>
        <!-- Cards Section -->
        <div v-if="isActingAsUser">
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
          <!-- Toggle Notifications Button -->
          <button class="btn btn-block btn-primary" type="button" @click="toggleNotifications()">
            <em v-if="notifications.length < 10" class="bi bi-bell">{{ notifications.length }}</em>
            <em v-else class="bi bi-bell">9+</em>
          </button>
        </div>
        <br>
        <!-- Notifications -->
        <!--  TODO:  Add different versions for each notification type      -->
        <notification v-for="notification in notifications"
                      :key="notification.id"
                      :data="notification"
                      @remove-notification="removeNotification(notification.id)"/>
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
import $ from 'jquery';

export default {
  name: "Home",
  components: {
    Alert,
    LoginRequired,
    MarketCard,
    Notification
  },
  props: {
    msg: String
  },
  async mounted() {
    await this.getCardData()
    await this.getNotificationData()
    if (this.$root.$data.user.canDoAdminAction()) {
      await this.getAdminNotifications();
    }
  },
  data() {
    return {
      cards: [],
      hideImages: true,
      hideNotifications: true,
      //Test data
      notifications: [],
      adminNotifications: [],
      error: ""
    }
  },
  computed: {
    /**
     * Check if user is logged in
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    /** Returns the product catalogue url **/
    productCatalogueRoute() {
      return `businesses/${this.actor.id}/products`;
    },

    /** Returns the inventory url **/
    inventoryRoute() {
      return `businesses/${this.actor.id}/inventory`;
    },

    /** Returns the sale listing url **/
    saleListingRoute() {
      return `businesses/${this.actor.id}/listings`;
    },

    /**
     * Current actor
     * Returns {name, id, type}
     **/
    actor() {
      return this.$root.$data.user.state.actingAs
    },

    /** Returns the current logged in users data **/
    actorName() {
      return this.actor.name
    },

    /**
     * Returns true if the user is currently acting as a user
     */
    isActingAsUser() {
      return this.actor.type === 'user'
    },

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
    }

  },
  methods: {
    toggleNotifications() {
      if (this.hideNotifications) {
        $('.toast').toast('show')
        this.hideNotifications = false
      } else {
        $('.toast').toast('hide')
        this.hideNotifications = true
      }
    },

    /**
     * Gets the user's cards so we can check for ones about to expire
     */
    async getCardData() {
      try {
        const response = await User.getCards(this.actor.id)
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
        const response = await User.getNotifications(this.actor.id)
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
        this.notifications.sort((a, b) =>
            (new Date(a.created) > new Date(b.created)) ? 1 : -1)
      } catch (error) {
        console.error(error)
        this.error = error
      }
    },

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
    async removeNotification(notificationId) {
      try {
        // Remove the notification from the list that is shown
        for (const [index, notification] of this.notifications.entries()) {
          if (notification.id === notificationId) {
            console.log(index)
            this.notifications.splice(index, 1)
          }
        }
      } catch (error) {
        console.error(error)
        this.error = error
      }
    }
  }
}

</script>

<style scoped>

</style>