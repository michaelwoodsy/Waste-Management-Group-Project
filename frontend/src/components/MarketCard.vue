<!--
MarketCard.vue
Displays a single market card.

Emits a 'cardDeleted' event with a payload containing the card id when the card is deleted.
Eg, <market-card @cardDeleted="someMethod" ... />

@prop cardData: The json data (from the api) to display
@prop hideImage: Boolean, when true will not display the card image.
-->
<template>
  <div class="rounded shadow-sm p-3 bg-white card-size m-3">
    <!-- Card Title -->
    <h5 class="d-inline"> {{ cardData.title }} </h5>

    <!-- Delete button -->
    <button
        class="btn btn-outline-danger d-inline-block float-right"
        v-if="canDeleteCard"
        data-toggle="modal"
        :data-target="'#deleteModal' + cardData.id"
    >
      Delete
    </button>

    <!-- Card creators name, a dot and the time created -->
    <p class="text-muted small mb-1">
      {{ cardCreatorName }}
      <b>&centerdot;</b>
      {{ location }}
      <b>&centerdot;</b>
      {{ timeCreated }}
    </p>

    <!-- Gives time left until card expiry -->
    <p id="countdown" class="text-danger small mb-1">
      Card expires in: {{ daysToExpire }}d {{ hoursToExpire }}h {{ minutesToExpire }}m {{ secondsToExpire }}s
    </p>

    <!-- Description -->
    <p class="text-muted"> {{ cardData.description }} </p>

    <!-- Card image -->
    <img v-if="!hideImage"
         class="img-fluid"
         :src="imageUrl"
         :alt="cardData.title + ' Image'"
    >

    <!-- Delete modal -->
    <div class="modal" tabindex="-1" role="dialog" :id="'deleteModal' + cardData.id">
      <div class="modal-dialog" role="document">
        <div class="modal-content">

          <!-- Title section of modal -->
          <div class="modal-header">
            <h5 class="modal-title">Delete Card: {{ cardData.title }}</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span ref="close" aria-hidden="true">&times;</span>
            </button>
          </div>

          <!-- Body section of modal -->
          <div class="modal-body">
            <p>Do you really want to permanently delete this card?</p>
          </div>

          <!-- Footer / button section of modal -->
          <div class="modal-footer">
            <button type="button" class="btn btn-danger" @click="deleteCard">Delete</button>
            <button type="button" class="btn btn-primary" data-dismiss="modal">Cancel</button>
          </div>

        </div>
      </div>
    </div>

  </div>
</template>

<script>
import {getTimeDiffStr} from "@/utils/dateTime";

export default {
  name: "MarketCard",
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
    }
  },

  data() {
    return{
      daysToExpire: '',
      hoursToExpire: '',
      minutesToExpire: '',
      secondsToExpire: '',
      timeInterval: ''
    }
  },

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

    /** The name of the creator of the card **/
    cardCreatorName() {
      return `${this.cardData.creator.firstName} ${this.cardData.creator.lastName}`
    },

    /** The rough location of the listing.
     * Will be the city, region or country, whichever is available.
     * **/
    location() {
      const address = this.cardData.creator.homeAddress
      return  address.city || address.region || address.country
    },

    /** Returns the image url for the card.
     * Currently just returns a template image **/
    imageUrl() {
      return "https://toitoi.nz/wp-content/uploads/2020/04/placeholder.png"
    },

    /** True if the logged in user is the creator of the card and acting as themself **/
    isCardCreator() {
      return this.$root.$data.user.isUser(this.cardData.creator.id)
    },

    /** True if the logged in user is the creator of the card or an admin **/
    canDeleteCard() {
      return this.isCardCreator || this.$root.$data.user.canDoAdminAction()
    }
  },
  methods: {
    /** Deletes this card, emitting an event on success **/
    deleteCard() {
      // TODO: Make delete api request here.
      // TODO: Display error if the request fails.

      // Emit the cardDeleted event once the api call is successful
      this.$emit('cardDeleted', this.cardData.id)

      // Close the modal by simulating a click on the close button
      this.$refs.close.click();
    },

    /** Calculates the time remaining before a card expires in days, hours, minutes and seconds **/
    timeUntilExpiry() {
      const now = new Date()
      const created = new Date(this.cardData.created)
      const twoWeeksAfter = new Date(created.setDate(created.getDate() + 14))
      const timeLeft = twoWeeksAfter.getTime() - now.getTime()
      const seconds = Math.floor( (timeLeft/1000) % 60 );
      const minutes = Math.floor( (timeLeft/1000/60) % 60 );
      const hours = Math.floor( (timeLeft/(1000*60*60)) % 24 );
      const days = Math.floor( timeLeft/(1000*60*60*24) );

      return {
        timeLeft,
        days,
        hours,
        minutes,
        seconds
      }
    },


    /** Initialises the timer for counting down the expiry of a card
     *
     */
    updateTimer(){
        this.daysToExpire = this.timeUntilExpiry().days
        this.hoursToExpire = this.timeUntilExpiry().hours
        this.minutesToExpire = this.timeUntilExpiry().minutes
        this.secondsToExpire = this.timeUntilExpiry().seconds
        if (this.timeUntilExpiry().timeLeft <= 0) {
          clearInterval(this.timeInterval);
        }
        setInterval(this.updateTimer,1000)
      }
    }
  }
</script>

<style scoped>
.card-size {
  min-height: 100px;
}
</style>