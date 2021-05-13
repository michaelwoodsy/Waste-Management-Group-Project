<!--
MarketCard.vue
Displays a single market card.

@prop cardData: The json data (from the api) to display
-->
<template>
  <div class="rounded shadow-sm p-3 bg-white card-size m-3">
    <!-- Card Title -->
    <h5 class="d-inline"> {{ cardData.title }} </h5>

    <!-- Card creators name, a dot and the time created -->
    <p class="text-muted small mb-1">
      {{ location }}
      <b class="">&centerdot;</b>
      {{ timeCreated }}
    </p>

    <!-- Description -->
    <p class="text-muted"> {{ cardData.description }} </p>


  </div>
</template>

<script>
export default {
  name: "MarketCard",
  props: {
    // Data of the card.
    cardData: {
      type: Object,
      required: true
    }
  },
  computed: {
    /** A string representation of when the card was created / renewed **/
    timeCreated() {
      // TODO: Calculate actual time
      return "2h ago"
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
    }
  }
}
</script>

<style scoped>
.card-size {
  min-height: 100px;
}
</style>