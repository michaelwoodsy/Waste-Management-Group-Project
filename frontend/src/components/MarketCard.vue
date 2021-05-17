<!--
MarketCard.vue
Displays a single market card.

@prop cardData: The json data (from the api) to display
@prop hideImage: Boolean, when true will not display the card image.
-->
<template>
  <div class="rounded shadow-sm p-3 bg-white card-size m-3">
    <!-- Card Title -->
    <h5 class="d-inline"> {{ cardData.title }} </h5>

    <!-- Card creators name, a dot and the time created -->
    <p class="text-muted small mb-1">
      {{ cardCreatorName }}
      <b>&centerdot;</b>
      {{ location }}
      <b>&centerdot;</b>
      {{ timeCreated }}
    </p>

    <!-- Description -->
    <p class="text-muted"> {{ cardData.description }} </p>

    <!-- Card image -->
    <img v-if="!hideImage"
         class="img-fluid"
         :src="imageUrl"
         :alt="cardData.title + ' Image'"
    >
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
    }
  }
}
</script>

<style scoped>
.card-size {
  min-height: 100px;
}
</style>