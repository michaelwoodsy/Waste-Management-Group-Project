<!--
Marketplace.vue
Page for displaying the marketplace.
-->

<template>
  <div class="container-fluid">

    <!-- Check the user is logged in -->
    <login-required
        v-if="!isLoggedIn"
        page="view the Marketplace"
    />

    <div v-else>

      <!--    Div for marketplace tabs    -->
      <div class="row justify-content-center">
        <div class="col-6">
          <ul class="nav nav-pills nav-fill">
            <li class="nav-item">
              <a id="for-sale-link"
                 class="pointer"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'ForSale'}"
                 @click="changePage('ForSale')">
                For Sale
              </a>
            </li>
            <li class="nav-item">
              <a id="wanted-link"
                 class="pointer"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'Wanted'}"
                 @click="changePage('Wanted')">
                Wanted
              </a>
            </li>
            <li class="nav-item">
              <a id="exchange-link"
                 class="pointer"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'Exchange'}"
                 @click="changePage('Exchange')">
                Exchange
              </a>
            </li>
          </ul>
        </div>
      </div>
      <br>

      <!-- Combobox for ordering -->
      <div class="row form justify-content-center">
        <div class="col-9 form-group text-center">
          <label for="order-select" class="d-inline-block">Order By</label>
          <select id="order-select"
                  v-model="order"
                  class="form-control ml-1 d-inline-block w-auto">
            <option value="created-asc">Newest</option>
            <option value="created-desc">Oldest</option>
            <option value="title">Title</option>
            <option value="location">Location</option>
          </select>
        </div>
      </div>

      <!-- Div with cards -->
      <div class="row justify-content-center">
        <div class="col-9">
          <div v-for="card in orderedCards" v-bind:key="card.id">
            <div v-if="hideImages">
              <MarketCard :card-data="card" hide-image></MarketCard>
            </div>
            <div v-else>
              <MarketCard :card-data="card"></MarketCard>
            </div>
          </div>
        </div>
      </div>


    </div>
  </div>
</template>

<script>

import LoginRequired from "./LoginRequired";
import MarketCard from "./MarketCard";

export default {
  name: "Marketplace",

  data() {
    return {
      tabSelected: 'ForSale', //Default tab
      cards: [],
      hideImages: false,
      error: "",
      order: 'created-asc'
    }
  },

  mounted() {
    this.changePage(this.tabSelected)
  },

  computed: {
    /**
     * Check if user is logged in
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
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

    /** List of cards, ordered by the selected ordering **/
    orderedCards() {
      // Create new card array
      let newCards = [...this.cards];

      // Order it appropriately
      if (this.order === 'created-asc') {
        newCards.sort((a, b) => -this.sortCreatedDate(a, b))
      }
      else if (this.order === 'created-desc') {
        newCards.sort((a, b) => this.sortCreatedDate(a, b))
      }
      else if (this.order === 'title') {
        newCards.sort((a, b) => this.sortTitle(a, b))
      }
      else if (this.order === 'location') {
        newCards.sort((a, b) => {this.sortLocation(a, b)})
      }

      return newCards
    }
  },
  components: {
    LoginRequired,
    MarketCard
  },
  methods: {
    /**
     * Method to change contents of the page when tab is selected.
     * Gets called when a tab is selected and updates contents
     * @param tab selected. is a string
     */
    changePage(tab) {
      this.tabSelected = tab
      //Call Api to get new cards for tab here
      //Change to call from api when available
      this.cards = this.getFakeCards(tab)
    },

    /** Function for sorting a list of cards by created date **/
    sortCreatedDate(a, b) {
      return (a.created < b.created) ? -1 : ((a.created > b.created) ? 1 : 0)
    },

    /** Function for sorting a list by title alphabetically **/
    sortTitle(a, b) {
      return (a.title < b.title) ? -1 : ((a.title > b.title) ? 1 : 0)
    },

    /** Function for sorting a list by location alphabetically **/
    sortLocation(a, b) {
      const aTerm = a.creator.homeAddress.city;
      const bTerm = b.creator.homeAddress.city;

      if (aTerm === null) {
        return -1
      }
      if (bTerm === null) {
        return 1
      }
      if (aTerm < bTerm) {
        return 1;
      }
      if (aTerm > bTerm) {
        return -1;
      }
      return 0;
    },

    getFakeCards(tab) {
      if (tab === 'ForSale') {
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
            "created": "2021-04-15T05:10:00Z",
            "displayPeriodEnd": "2021-07-29T05:10:00Z",
            "title": "1982 Lada Samara",
            "description": "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
            "keywords": [
              {
                "id": 600,
                "name": "Vehicle",
                "created": "2021-07-15T05:10:00Z"
              }
            ]
          },
          {
            "id": 501,
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
            "created": "2021-05-15T05:10:00Z",
            "displayPeriodEnd": "2021-07-29T05:10:00Z",
            "title": "2005 Honda Fit",
            "description": "Teal, Good ol car. Perfect Condition. As is, where is. Will swap for Lamborghini, nothing else.",
            "keywords": [
              {
                "id": 600,
                "name": "Vehicle",
                "created": "2021-07-15T05:10:00Z"
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
            "created": "2021-05-10T05:10:00Z",
            "displayPeriodEnd": "2021-07-29T05:10:00Z",
            "title": "Bag of chips",
            "description": "Just a good ol bag of chips, nothing special, will trade for a pebble",
            "keywords": [
              {
                "id": 601,
                "name": "Food",
                "created": "2021-07-15T05:10:00Z"
              }
            ]
          }

        ]
      }
      else if (tab === 'Wanted') {
        return [
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
            "created": "2021-04-15T05:10:00Z",
            "displayPeriodEnd": "2021-07-29T05:10:00Z",
            "title": "To pass SENG302",
            "description": "Please can I just pass SENG302",
            "keywords": [
              {
                "id": 602,
                "name": "University",
                "created": "2021-07-15T05:10:00Z"
              }
            ]
          },
          {
            "id": 504,
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
            "section": "Wanted",
            "created": "2021-05-15T05:10:00Z",
            "displayPeriodEnd": "2021-07-29T05:10:00Z",
            "title": "Money",
            "description": "Who doesn't want money",
            "keywords": [
              {
                "id": 603,
                "name": "Money",
                "created": "2021-07-15T05:10:00Z"
              }
            ]
          }
        ]
      }
      else if (tab === 'Exchange') {
        return [
          {
            "id": 504,
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
            "section": "Wanted",
            "created": "2021-05-15T05:10:00Z",
            "displayPeriodEnd": "2021-07-29T05:10:00Z",
            "title": "Money Exchange",
            "description": "I will exchange my $3 for your $20",
            "keywords": [
              {
                "id": 603,
                "name": "Money",
                "created": "2021-07-15T05:10:00Z"
              }
            ]
          }
        ]
      }
    }
  }
}

</script>

<style scoped>
.nav-item {
  font-size: 20px;
}
</style>