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
            <li v-if="user.isActingAsUser()" class="nav-item mb-2">
              <router-link class="btn btn-block btn-primary"
                           :to="`users/${user.state.userId}/purchases`"
              >
                My Purchases
              </router-link>
            </li>
            <li v-if="user.isActingAsUser()" class="nav-item mb-2">
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
            <li class="nav-item mb-2">
              <router-link :to="`businesses/${user.actor().id}/edit`"
                           class="btn btn-block btn-primary">Edit Business
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

        <div v-if="user.isActingAsUser()" class="row row-cols-1 row-cols-lg-2">
          <!-- Cards Section -->
          <div class="col">
            <h2>My Cards</h2>
            <div v-if="cards.length > 0">
              <alert v-if="hasExpiredCards" class="text-center">
                You have cards that have recently expired and will be deleted within 24 hours if not extended!
              </alert>
              <div v-if="hasExpiredCards">
                <h5>Recently Expired Cards</h5>
                <div class="row row-cols-1">
                  <div v-for="card in expiredCards" v-bind:key="card.id" class="col">
                    <market-card :card-data="card" :hide-image="hideImages" :show-expired="true"
                                 @card-deleted="deleteCard" @card-extended="extendCard"
                                 @refresh-cards="getCardData"></market-card>
                  </div>
                </div>
              </div>
              <h5 v-if="hasExpiredCards">Active Cards</h5>
              <div class="row row-cols-1">
                <div v-for="card in activeCards" v-bind:key="card.id" class="col">
                  <market-card :card-data="card" :hide-image="hideImages" :show-expired="true"
                               @card-deleted="deleteCard" @card-extended="extendCard"
                               @refresh-cards="getCardData"></market-card>
                </div>
              </div>
            </div>
            <div v-else>You have no cards.</div>
          </div>

          <!-- Liked Listing Section -->
          <div class="col">
            <h2>My Liked Listings</h2>
            <div v-if="likedListings.length > 0">
              <div class="input-group mb-4">
                <div class="input-group-prepend">
                  <span class="input-group-text">Filter By Tag</span>
                </div>
                <div class="form-control text-center tag-filters">
                  <div v-for="tag in tags" :key="tag.name" class="d-inline">
                    <em v-if="tag.name !== 'None'"
                        :class="{'bi-tag-fill': tagged(tag.name), 'bi-tag': !tagged(tag.name)}"
                        :style="`color: ${tag.colour};`"
                        class="tag bi bi-tag-fill pointer mx-2"
                        @click="toggleTagFilter(tag.name)"
                    />
                  </div>
                </div>
                <div class="input-group-append">
                  <button :class="{'btn-secondary': tagFilters.length === 0, 'btn-danger': tagFilters.length > 0}"
                          :disabled="tagFilters.length === 0"
                          class="btn"
                          @click="tagFilters = []"
                  >
                    <em class="bi bi-x-circle-fill"/>
                  </button>
                </div>
              </div>
              <div class="row row-cols-1">
                <div v-for="listing in taggedListings" v-bind:key="listing.id" class="col">
                  <liked-listing :data="listing"
                                 :tags="tags"
                                 @update-data="updateData"
                                 @update-tag="updateTag"
                                 @update-star="updateStar"
                  />
                </div>
              </div>
            </div>
            <div v-else>You have no liked sale listings.</div>
          </div>
        </div>

        <div v-if="user.isActingAsBusiness()" class="row">
          <!-- Sales Report Section -->
          <div class="col">
            <sales-report-page :business-id="user.actor().id"/>
          </div>
        </div>

      </div>

      <!-- Side Bar Right-->
      <div class="col-md-3 col-lg-2 p-3 bg-dark shadow">
        <div class="mb-3">
          <h4 class="text-light">Notifications</h4>

          <!-- Toggle Notifications/Messages Buttons -->
          <div class="btn-group btn-block">
            <button id="showNotificationsButton"
                    :class="{'btn-primary': notificationsShown, 'btn-outline-primary': !notificationsShown}"
                    class="btn" style="width: 50%" type="button" @click="showNotifications">
              <em class="bi bi-bell"/>
              <span v-if="newNotifications.length > 0" class="badge badge-pill badge-light ml-1">
                <span v-if="newNotifications.length < 10">{{ newNotifications.length }}</span>
                <span v-else>9+</span>
              </span>
            </button>
            <button id="showMessagesButton"
                    :class="{'btn-primary': !notificationsShown, 'btn-outline-primary': notificationsShown}"
                    class="btn" style="width: 50%" type="button" @click="showMessages">
              <em class="bi bi-envelope"/>
              <span v-if="newMessages.length > 0" class="badge badge-pill badge-light ml-1">
                <span v-if="newMessages.length < 10">{{ newMessages.length }}</span>
                <span v-else>9+</span>
              </span>
            </button>
          </div>
        </div>

        <!-- Undo link -->
        <div v-if="canUndo">
          <button class="btn btn-primary w-100" @click="undoDelete()">
            Undo Deletion <em class="bi bi-arrow-counterclockwise"/> ({{ countDown }})
          </button>
        </div>

        <!-- Notifications -->
        <div v-if="notificationsShown">
          <div v-if="notifications.length === 0">
            <p class="text-light">You have no notifications</p>
          </div>
          <div v-else>
            <span v-if="newNotifications.length > 0" class="text-light mt-2">New</span>
            <notification v-for="notification in newNotifications"
                          :key="notification.id"
                          :data="notification"
                          :unread="true"
                          @remove-notification="removeNotification(notification.id)"
                          @read-notification="readNotification(notification.id)"
            />

            <span v-if="readNotifications.length > 0" class="text-light mt-2">Older</span>
            <notification v-for="notification in readNotifications"
                          :key="notification.id"
                          :data="notification"
                          :unread="false"
                          @remove-notification="removeNotification(notification.id)"
                          @read-notification="readNotification(notification.id)"
            />

          </div>
        </div>

        <div v-else>
          <div v-if="messages.length === 0">
            <p class="text-light">You have no messages</p>
          </div>
          <div v-else>
            <span v-if="newMessages.length > 0" class="text-light mt-2">New</span>
            <message v-for="message in newMessages"
                     :key="message.id"
                     :message="message"
                     :unread="true"
                     @remove-message="removeMessage(message.id)"
                     @read-message="readMessage(message.id)"
            />

            <span v-if="readMessages.length > 0" class="text-light mt-2">Older</span>
            <message v-for="message in readMessages"
                     :key="message.id"
                     :message="message"
                     :unread="false"
                     @remove-message="removeMessage(message.id)"
                     @read-message="readMessage(message.id)"
            />
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
import {User, Business} from "@/Api";
import userState from "@/store/modules/user"
import $ from 'jquery';
import Message from "@/components/marketplace/Message";
import LikedListing from "@/components/sale-listing/LikedListing";
import undo from "@/utils/undo"
import SalesReportPage from "@/components/sales-report/SalesReportPage";

const tags = {
  RED: {
    name: "Red",
    colour: "Red"
  },
  ORANGE: {
    name: "Orange",
    colour: "DarkOrange"
  },
  YELLOW: {
    name: "Yellow",
    colour: "Gold"
  },
  GREEN: {
    name: "Green",
    colour: "ForestGreen"
  },
  BLUE: {
    name: "Blue",
    colour: "DodgerBlue"
  },
  PURPLE: {
    name: "Purple",
    colour: "DarkViolet"
  },
  NONE: {
    name: "None",
    colour: "DimGrey"
  }
}

export default {
  name: "Home",
  components: {
    SalesReportPage,
    LikedListing,
    Message,
    Alert,
    LoginRequired,
    MarketCard,
    Notification
  },
  async mounted() {
    try {
      await this.getData()
    }
    catch (err) {
      // do nothing
    }
  },
  watch: {
    async actingAs() {
      await this.getData()
    }
  },
  data() {
    return {
      user: userState,
      cards: [],
      likedListings: [],
      hideImages: true,
      notificationsShown: true,
      notifications: [],
      messages: [],
      error: "",
      tags: tags,
      tagFilters: [],
      countDown: 10,
      alreadyCountingDown: false
    }
  },
  computed: {
    /**
     * Computed property that returns all expired cards.
     */
    expiredCards() {
      const cards = []
      for (const card of this.cards) {
        if (this.expired(card)) {
          cards.push(card)
        }
      }
      return cards
    },

    /**
     * Returns true if a user has expired cards
     */
    hasExpiredCards() {
      return this.expiredCards.length > 0
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
     * Returns new notifications sorted by most recent.
     */
    newNotifications() {
      const newNotifications = []
      for (const notification of this.notifications) {
        if (!notification.read) {
          newNotifications.push(notification)
        }
      }
      newNotifications.sort((a, b) => (new Date(a.created) > new Date(b.created)) ? -1 : 1)
      return newNotifications
    },

    /**
     * Returns read notifications sorted by most recent.
     */
    readNotifications() {
      const readNotifications = []
      for (const notification of this.notifications) {
        if (notification.read) {
          readNotifications.push(notification)
        }
      }
      readNotifications.sort((a, b) => (new Date(a.created) > new Date(b.created)) ? -1 : 1)
      return readNotifications
    },

    /**
     * Returns new messages sorted by most recent.
     */
    newMessages() {
      const newMessages = []
      for (const message of this.messages) {
        if (!message.read) {
          newMessages.push(message)
        }
      }
      newMessages.sort((a, b) => (new Date(a.created) > new Date(b.created)) ? -1 : 1)
      return newMessages
    },

    /**
     * Returns read messages sorted by most recent.
     */
    sortedMessages() {
      let sortedMessages = [...this.messages]
      sortedMessages.sort((a, b) => (new Date(a.created) > new Date(b.created)) ? -1 : 1)
      return sortedMessages
    },

    /**
     * True if there is an operation that can be undone
     */
    canUndo() {
      return undo.state.toDelete !== null

    },

    /**
     * Returns read messages sorted by most recent.
     */
    readMessages() {
      const readMessages = []
      for (const message of this.messages) {
        if (message.read) {
          readMessages.push(message)
        }
      }
      readMessages.sort((a, b) => (new Date(a.created) > new Date(b.created)) ? -1 : 1)
      return readMessages
    },

    /**
     * Returns listings that are tagged by one of the filter tags
     */
    taggedListings() {
      if (this.tagFilters.length === 0) {
        return this.sortedLikedListings
      } else {
        const listings = []
        for (const listing of this.sortedLikedListings) {
          if (this.tagFilters.includes(listing.tag)) {
            listings.push(listing)
          }
        }
        return listings
      }
    },

    /**
     * Returns the likedListings sorted by starred first.
     */
    sortedLikedListings() {
      let sortFunc = (x, y) => {
        if (x.starred === y.starred) {
          return 0
        } else if (x.starred) {
          return -1
        }
        return 1
      }
      return [...this.likedListings].sort(sortFunc)
    },

    /**
     * the currently acting as user or business
     */
    actingAs() {
      return this.$root.$data.user.state.actingAs
    }
  },
  methods: {

    /**
     * Updates the users data in the store
     */
    async updateData() {
      await this.user.updateData()
      await this.getData()
    },

    /**
     * Gets the user or businesses notifications, cards and messages
     */
    async getData() {
      this.notifications = []
      if (this.user.actor().type === "user") {
        await this.user.updateData()
        await this.getCardData()
        await this.getNotificationData()
        if (this.user.canDoAdminAction()) {
          await this.getAdminNotifications();
        }
        for (const [index, notification] of this.notifications.entries()) {
          if (!('read' in notification)) {
            this.notifications[index].read = false
          }
        }

        await this.getMessages()
        for (const [index, message] of this.messages.entries()) {
          if (!('read' in message)) {
            this.messages[index].read = false
          }
        }
        await this.getLikedListings()
      } else {
        await this.getBusinessNotificationData()
        for (const [index, notification] of this.notifications.entries()) {
          if (!('read' in notification)) {
            this.notifications[index].read = false
          }
        }
        this.cards = []
        this.messages = []
        this.likedListings = []
      }
      this.showToasts()
    },

    /**
     * Displays the notifications section
     */
    async showNotifications() {
      this.notificationsShown = true
      await this.$nextTick()
      this.showToasts()
    },

    /**
     * Displays the messages section
     */
    async showMessages() {
      this.notificationsShown = false
      await this.$nextTick()
      this.showToasts()
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
     * Gets the user's liked listings
     */
    async getLikedListings() {
      this.likedListings = []
      for (let likedListing of this.user.state.userData.likedSaleListings) {
        likedListing.listing.currency = await this.$root.$data.product.getCurrency(likedListing.listing.business.address.country)
        this.likedListings.push(likedListing)
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
      } catch (error) {
        console.error(error)
        this.error = error
      }
    },

    /**
     * Gets the business' notifications
     */
    async getBusinessNotificationData() {
      try {
        const response = await Business.getNotifications(this.user.actor().id)
        this.notifications.push(...response.data)
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
     * Sets the specified notification to read
     * @param notificationId the ID of the notification to set to read
     */
    readNotification(notificationId) {
      for (const [index, notification] of this.notifications.entries()) {
        if (notification.id === notificationId) {
          notification.read = true
          this.$set(this.notifications, index, notification)
        }
      }
    },

    /**
     * Remove a notification from the list of visible notifications
     * @param notificationId the id of the notification that is to be removed
     */
    removeNotification(notificationId) {
      this.countDown = 9
      if (!this.alreadyCountingDown) {
        this.alreadyCountingDown = true
        this.countDownTimer()
      }
      // Remove the notification from the list that is shown
      for (const [index, notification] of this.notifications.entries()) {
        if (notification.id === notificationId) {
          this.notifications.splice(index, 1)
        }
      }
    },

    /**
     * Sets the specified message to read
     * @param messageId the ID of the message to set to read
     */
    readMessage(messageId) {
      for (const [index, message] of this.messages.entries()) {
        if (message.id === messageId) {
          message.read = true
          this.$set(this.messages, index, message)
        }
      }
    },

    /**
     * Adds a notification back to the list of notifications.
     * Run after an undo operation is performed.
     */
    async addNotification(data) {
      this.notifications.push(data);

      // these lines are required to render the notification just added
      await this.$nextTick()
      this.showToasts()
    },

    /**
     * Adds a recently deleted message back to the list of messages.
     * Run after an undo operation is performed.
     */
    async addMessage(data) {
      this.messages.push(data);
      // these lines are required to render the notification just added
      await this.$nextTick()
      this.showToasts()
    },

    /**
     * Shows all elements with the toast css class
     */
    showToasts() {
      $('.toast').toast('show')
    },

    /**
     * Remove a message from the list of visible messages
     * @param messageId the id of the message that is to be removed
     */
    removeMessage(messageId) {
      this.countDown = 9
      if (!this.alreadyCountingDown) {
        this.alreadyCountingDown = true
        this.countDownTimer()
      }
      // Remove the message from the list that is shown
      for (const [index, message] of this.messages.entries()) {
        if (message.id === messageId) {
          this.messages.splice(index, 1)
        }
      }
    },

    /**
     * Updates a liked listing's tag after sending API request
     *
     * @param listingId ID of the liked listing to update
     * @param name tag name to set
     */
    updateTag(listingId, name) {
      for (const [index, listing] of this.user.state.userData.likedSaleListings.entries()) {
        if (listing.id === listingId) {
          listing.tag = name
          this.$set(this.user.state.userData.likedSaleListings, index, listing)
        }
      }
    },

    /**
     * Updates a liked listing's starred value
     *
     * @param listingId ID of the liked listing to update
     * @param star Boolean for whether it should be starred
     */
    updateStar(listingId, star) {
      for (const [index, listing] of this.user.state.userData.likedSaleListings.entries()) {
        if (listing.id === listingId) {
          listing.starred = star
          this.$set(this.user.state.userData.likedSaleListings, index, listing)
        }
      }
    },

    /**
     * Adds or removes a tag from the filter list
     */
    toggleTagFilter(name) {
      name = name.toUpperCase()
      if (this.tagged(name)) {
        for (const [index, tag] of this.tagFilters.entries()) {
          if (tag === name) {
            this.tagFilters.splice(index, 1)
            return
          }
        }
      } else {
        this.tagFilters.push(name)
      }
    },

    /**
     * Returns true if sale listings are currently being filtered by a particular tag
     */
    tagged(name) {
      return this.tagFilters.includes(name.toUpperCase())
    },

    /**
     * Undoes the last delete operation.
     */
    undoDelete() {
      if (undo.isMessageRequest()) {
        this.addMessage(undo.state.toDelete.data)
      } else {
        this.addNotification(undo.state.toDelete.data)
      }
      undo.cancelDelete()
    },

    /**
     * Decrements countdown timer to zero
     */
    countDownTimer() {
      if (this.countDown > 0) {
        setTimeout(() => {
          this.countDown -= 1
          this.countDownTimer()
        }, 1000)
      } else {
        this.alreadyCountingDown = false
      }
    }
  }
}

</script>

<style scoped>

.tag-filters {
  font-size: 20px;
}

.tag {
  transition: 0.3s;
}

.tag:hover {
  text-shadow: currentColor 0 0 5px;
}
</style>