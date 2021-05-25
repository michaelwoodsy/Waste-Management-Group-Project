<!--
MarketCard.vue
Displays a single market card.

Emits a 'card-deleted' event with a payload containing the card id when the card is deleted.
Eg, <market-card @card-deleted="someMethod" ... />

@prop cardData: The json data (from the api) to display
@prop hideImage: Boolean, when true will not display the card image.
-->
<template>
  <div :class="{'border-danger': expired}" class="card shadow card-size">

    <div v-if="expired && showExpired" class="card-header">
      <p class="text-danger d-inline">This card is about to expire</p>
      <button class="btn btn-outline-danger d-inline float-right mx-1"
              data-toggle="modal"
              :data-target="'#deleteModal' + cardData.id">
        Delete
      </button>
      <!--TODO: Hook extend button up to API calls-->
      <button class="btn btn-outline-primary d-inline float-right mx-1" @click="extendCard">Extend</button>
    </div>

    <!-- Card image -->
    <img v-if="!hideImage"
         :alt="cardData.title + ' Image'"
         :src="imageUrl"
         class="img-fluid card-img-top"
    >

    <div class="card-body">

      <div v-if="isCardCreator">
        <!-- Shows expiry time of a particular card -->
        <p v-if="daysToExpire > 0 || hoursToExpire > 0 || minutesToExpire > 0 || secondsToExpire > 0" class="text-danger float-right small mb-1">
          Card expires in:
          <span v-if="daysToExpire > 0">{{ daysToExpire }}d </span>
          <span v-if="hoursToExpire > 0">{{ hoursToExpire }}h </span>
          <span v-if="minutesToExpire > 0">{{ minutesToExpire }}m </span>
          <span v-if="secondsToExpire > 0">{{ secondsToExpire }}s </span>
        </p>
        <!-- If card has expired, card will have been deleted -->
        <p v-else class="text-danger float-right small mb-1">
          Card has expired
        </p>
      </div>

      <!-- Card Title -->
      <h5 class="card-title d-inline"> {{ cardData.title }} </h5>


      <!-- Card creators name, a dot and the time created -->
      <p class="card-text text-muted small mb-1">
        {{ cardCreatorName }}
        <b>&centerdot;</b>
        {{ location }}
        <b>&centerdot;</b>
        {{ timeCreated }}
      </p>

      <div :id="'cardDetails' + cardData.id" class="collapse">
        <hr/>
        <!-- Description -->
        <p class="card-text">{{ cardData.description }}</p>
        <hr/>
      </div>

      <!-- Delete button -->
      <button
          v-if="canDeleteCard && !expired"
          :data-target="'#deleteModal' + cardData.id"
          style="margin-left: 10px"
          class="btn btn-outline-danger d-inline float-right"
          data-toggle="modal"
      >
        Delete
      </button>

      <button :data-target="'#cardDetails' + cardData.id" class="btn btn-outline-secondary float-right"
              data-toggle="collapse" @click="toggleDetails">
        <span v-if="!showDetails">View Details <i class="bi bi-arrow-down"/></span>
        <span v-else>Hide Details <i class="bi bi-arrow-up"/></span>
      </button>

    </div>

    <!-- Delete modal -->
    <div :id="'deleteModal' + cardData.id" class="modal fade" role="dialog" tabindex="-1">
      <div class="modal-dialog" role="document">
        <div class="modal-content">

          <!-- Title section of modal -->
          <div class="modal-header">
            <h5 class="modal-title">Delete Card: {{ cardData.title }}</h5>
            <button aria-label="Close" class="close" data-dismiss="modal" type="button">
              <span ref="close" aria-hidden="true">&times;</span>
            </button>
          </div>

          <!-- Body section of modal -->
          <div class="modal-body">
            <p>Do you really want to permanently delete this card?</p>

            <!-- Error message -->
            <alert v-if="error">{{ error }}</alert>
          </div>

          <!-- Footer / button section of modal -->
          <div class="modal-footer">
            <button class="btn btn-danger" type="button" @click="deleteCard">Delete</button>
            <button class="btn btn-secondary" data-dismiss="modal" type="button">Cancel</button>
          </div>

        </div>
      </div>
    </div>

  </div>
</template>

<script>
import {getTimeDiffStr} from "@/utils/dateTime";
import {Marketplace} from "@/Api";
import Alert from "@/components/Alert";

export default {
  name: "MarketCard",
  components: {
    Alert
  },
  props: {
    // Data of the card.
    cardData: {
      type: Object,
      required: true
    },
    // Optional flag to disable images.
    hideImage: {
      type: Boolean,
      required: false,
      default: false
    },
    showExpired: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      showDetails: false,
      error: null,
      daysToExpire: '',
      hoursToExpire: '',
      minutesToExpire: '',
      secondsToExpire: '',
    }
  },

  /** Initialises the timer for displaying the expiry of a card */
  mounted() {
    this.daysToExpire = this.timeUntilExpiry().days
    this.hoursToExpire = this.timeUntilExpiry().hours
    this.minutesToExpire = this.timeUntilExpiry().minutes
    this.secondsToExpire = this.timeUntilExpiry().seconds
    this.updateTimer()
  },

  computed: {
    /** A string representation of how long ago the card was created or renewed **/
    timeCreated() {
      return getTimeDiffStr(this.cardData.created)
    },

    /** Returns the current user ID **/
    userId() {
      return Number(this.$root.$data.user.state.actingAs.id)
    },

    /** The name of the creator of the card **/
    cardCreatorName() {
      return `${this.cardData.creator.firstName} ${this.cardData.creator.lastName}`
    },

    /** The rough location of the listing.
     * Will be the city, region or country, whichever is available.
     **/
    location() {
      const address = this.cardData.creator.homeAddress
      return address.city || address.region || address.country
    },

    /** Returns the image url for the card.
     * Currently just returns a template image **/
    imageUrl() {
      return "https://toitoi.nz/wp-content/uploads/2020/04/placeholder.png"
    },

    /** True if the logged in user is the creator of the card and acting as themself **/
    isCardCreator() {
      return this.$root.$data.user.isUser(this.cardData.creator.id) || this.canDoAdminAction
    },

    /** True if the logged in user is the creator of the card or an admin **/
    canDeleteCard() {
      return this.isCardCreator || this.canDoAdminAction
    },

    /** True if the logged in user is a GAA or DGAA **/
    canDoAdminAction() {
      return this.$root.$data.user.canDoAdminAction()
    },

    /** Returns whether the card is about to expire or not **/
    expired() {
      const now = new Date();
      return now >= new Date(this.cardData.displayPeriodEnd);
    }
  },
  methods: {
    /**
     * Toggles the showDetails field
     */
    toggleDetails() {
      this.showDetails = !this.showDetails
    },
    /**
     * Sends API request to extend the time of a card
     */
    extendCard() {
      // TODO: Make API request to extend card once implemented.
      const currentDate = new Date(this.cardData.displayPeriodEnd)
      const newDate = new Date(this.cardData.displayPeriodEnd)
      newDate.setDate(currentDate.getDate() + 14)

      console.log(currentDate)
      console.log(newDate)

      this.$emit('card-extended', this.cardData.id, newDate.toDateString())
    },
    /** Deletes this card, emitting an event on success **/
    deleteCard() {
      // Reset error flag
      this.error = null;

      // Make request
      Marketplace.deleteCard(this.cardData.id)
          .then(() => {
            // Emit the card-deleted event once the api call is successful
            this.$emit('card-deleted', this.cardData.id)

            // Close the modal by simulating a click on the close button
            this.$refs.close.click();
          })
          .catch((err) => {
            // Set error message
            this.error = err.response
                ? err.response.data
                : err.message
          })


      // Close the modal by simulating a click on the close button
      this.$refs.close.click();
    },

    /** Calculates the time remaining before a card expires in days, hours, minutes and seconds **/
    timeUntilExpiry() {
      const now = new Date()
      const displayEnd = new Date(this.cardData.displayPeriodEnd)
      const timeLeft = displayEnd.getTime() - now.getTime()
      const seconds = Math.floor((timeLeft / 1000) % 60);
      const minutes = Math.floor((timeLeft / 1000 / 60) % 60);
      const hours = Math.floor((timeLeft / (1000 * 60 * 60)) % 24);
      const days = Math.floor(timeLeft / (1000 * 60 * 60 * 24));

      return {
        timeLeft,
        days,
        hours,
        minutes,
        seconds
      }
    },


    /** Updates the timer for counting down the expiry of a card */
    updateTimer() {
      this.daysToExpire = this.timeUntilExpiry().days
      this.hoursToExpire = this.timeUntilExpiry().hours
      this.minutesToExpire = this.timeUntilExpiry().minutes
      this.secondsToExpire = this.timeUntilExpiry().seconds
      if (this.timeUntilExpiry().timeLeft > 0) {
        requestAnimationFrame(this.updateTimer)
      }
    }
  }
}
</script>

<style scoped>

.card-size {
  min-height: 100px;
  min-width: 150px;
  margin-bottom: 40px;
}

</style>