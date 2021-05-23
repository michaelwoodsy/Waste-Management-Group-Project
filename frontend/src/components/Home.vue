<template>
  <div>

    <login-required
        v-if="!isLoggedIn"
        page="view your profile page"
    />

    <!-- Greeting User -->
    <div v-else-if="this.actor.type === 'user'">
      <div class="row">
        <div class="col text-center mb-2">
          <h2>Hello {{ actorName }}</h2>
          <router-link class="btn btn-outline-primary mx-2" to="/marketplace">View Marketplace</router-link>
        </div>
      </div>
    </div>

    <!-- Greeting Business -->
    <div v-else>
      <div class="row">
        <div class="col text-center mb-2">
          <h2>{{ actorName }}</h2>
          <router-link :to="productCatalogueRoute" class="btn btn-outline-primary mx-2">View Product Catalogue
          </router-link>
          <router-link :to="inventoryRoute" class="btn btn-outline-primary mx-2">View Inventory</router-link>
          <router-link :to="saleListingRoute" class="btn btn-outline-primary mx-2">View Sale Listings</router-link>
        </div>
      </div>
    </div>

    <br>

    <div class="row">
      <div class="col text-left mb-2">
        <h2>My Cards</h2>
      </div>
    </div>

    <!-- Cards -->
    <div class="row row-cols-1 row-cols-lg-2 mb-3">
      <div v-for="card in cards" v-bind:key="card.id" class="col">
        <MarketCard :card-data="card" :hide-image="hideImages" :show-expired="true"></MarketCard>
      </div>
    </div>
  </div>
</template>

<script>

import LoginRequired from "./LoginRequired";
import MarketCard from "@/components/MarketCard";

export default {
  name: "Home",
  props: {
    msg: String
  },
  mounted() {
    this.cards = this.getCardData();
  },
  data() {
    return {
      cards: [],
      hideImages: false,
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

  },
  components: {
    LoginRequired,
    MarketCard
  },
  methods: {
    /**
     * Gets the user's cards so we can check for ones about to expire
     */
    getCardData() {
      //TODO: change to get the user's cards from backend
      return [
        {
          "id": 500,
          "creator": {
            "id": 1,
            "firstName": "John",
            "lastName": "Smith",
            "homeAddress": {
              "streetNumber": "3/24",
              "streetName": "Ilam Road",
              "city": "Christchurch",
              "region": "Canterbury",
              "country": "New Zealand",
              "postcode": "90210"
            },
          },
          "section": "ForSale",
          "created": "2021-05-03T05:10:00Z",
          "displayPeriodEnd": "2021-05-17T05:10:00Z",
          "title": "1982 Lada Samara",
          "description": "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
          "keywords": [
            {
              "id": 600,
              "name": "Vehicle",
              "created": "2021-04-15T05:10:00Z"
            }
          ]
        },
        {
          "id": 503,
          "creator": {
            "id": 1,
            "firstName": "John",
            "lastName": "Smith",
            "homeAddress": {
              "streetNumber": "3/24",
              "streetName": "Ilam Road",
              "city": "Christchurch",
              "region": "Canterbury",
              "country": "New Zealand",
              "postcode": "90210"
            },
          },
          "section": "Wanted",
          "created": "2021-05-02T05:10:00Z",
          "displayPeriodEnd": "2021-05-16T05:10:00Z",
          "title": "To pass SENG302",
          "description": "Please can I just pass SENG302",
          "keywords": [
            {
              "id": 602,
              "name": "University",
              "created": "2021-04-15T05:10:00Z"
            }
          ]
        },
        {
          "id": 502,
          "creator": {
            "id": 101,
            "firstName": "John",
            "lastName": "Smith",
            "homeAddress": {
              "streetNumber": "3/24",
              "streetName": "Ilam Road",
              "city": "Christchurch",
              "region": "Canterbury",
              "country": "New Zealand",
              "postcode": "90210"
            },
          },
          "section": "ForSale",
          "created": "2021-06-10T05:10:00Z",
          "displayPeriodEnd": "2021-06-24T05:10:00Z",
          "title": "Bag of chips",
          "description": "Just a good ol bag of chips, nothing special, will trade for a pebble",
          "keywords": [
            {
              "id": 601,
              "name": "Food",
              "created": "2021-04-15T05:10:00Z"
            }
          ]
        }
      ]
    },
    expired(card) {
      const now = new Date();
      if (now >= new Date(card.displayPeriodEnd)) {
        return true;
      }
    }
  }
}

</script>

<style scoped>

</style>