<template>
  <div class="container-fluid">

    <login-required
        v-if="!isLoggedIn"
        page="view your profile page"
    />

    <div v-else class="row">

      <!-- Side Bar -->
      <div class="col-2 pt-3  bg-dark shadow min-vh-100" style="width: 250px">
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
      <div class="col-8 p-3 mx-3">
        <div class="text-center">
          <h1><span v-if="isActingAsUser">Hello </span>{{ actorName }}</h1>
          <hr>
        </div>
        <!-- Cards Section -->
        <div v-if="isActingAsUser">
          <h2>My Cards</h2>
          <div class="row row-cols-1 row-cols-md-2">
            <div v-for="card in cards" v-bind:key="card.id" class="col">
              <MarketCard :card-data="card" :hide-image="hideImages" :show-expired="true"></MarketCard>
            </div>
          </div>
        </div>

      </div>

      <div class="col-2 pt-3"></div>

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
      hideImages: true,
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
    }

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
            "id": 100,
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
            "id": 100,
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