<!--
Marketplace.vue
Page for displaying the marketplace.
-->

<template>
  <page-wrapper>

    <!-- Check the user is logged in -->
    <login-required
        v-if="!isLoggedIn"
        page="view the Marketplace"
    />

    <div v-else>
      <!--    Div for marketplace tabs    -->
      <div class="row justify-content-center">
        <div class="col"/>
        <div class="col-6">
          <ul class="nav nav-pills nav-fill">
            <li class="nav-item">
              <a id="for-sale-link"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'ForSale'}"
                 class="pointer"
                 @click="changePage('ForSale')">
                For Sale
              </a>
            </li>
            <li class="nav-item">
              <a id="wanted-link"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'Wanted'}"
                 class="pointer"
                 @click="changePage('Wanted')">
                Wanted
              </a>
            </li>
            <li class="nav-item">
              <a id="exchange-link"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'Exchange'}"
                 class="pointer"
                 @click="changePage('Exchange')">
                Exchange
              </a>
            </li>
          </ul>
        </div>
        <div class="col">
          <button v-if="actingAsUser" class="btn btn-primary float-right" data-target="#createCard" data-toggle="modal" @click="newCard">
            New Card
          </button>
        </div>
      </div>

      <div id="createCard" :key="this.createNewCard" class="modal fade" data-backdrop="static">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-body">
              <create-card-page @refresh-cards="refreshCards()"></create-card-page>
            </div>
          </div>
        </div>
      </div>

      <!-- Div above results -->
      <div class="row form justify-content-center">
        <div class="col form-group text-center">
          <!-- Combobox and label for ordering -->
          <label class="d-inline-block" for="order-select">Order By</label>
          <select id="order-select"
                  v-model="order"
                  class="form-control ml-2 d-inline-block w-auto">
            <option value="created-asc">Newest</option>
            <option value="created-desc">Oldest</option>
            <option value="title">Title</option>
            <option value="location">Location</option>
          </select>

          <!-- Text for "showing x of x results -->
          <showing-results-text
              :items-per-page="resultsPerPage"
              :page="page"
              :total-count="totalCardCount"
              class="ml-4"
          />
        </div>
      </div>

      <!-- Div with cards -->
      <div class="row row-cols-1 row-cols-lg-2">
        <div v-for="card in orderedCards" v-bind:key="card.id" class="col">
          <MarketCard :card-data="card" :hide-image="hideImages" :show-expired="false" @card-deleted="deleteCard"></MarketCard>
        </div>
      </div>

      <!-- Pagination Links -->
      <pagination
          :current-page.sync="page"
          :items-per-page="resultsPerPage"
          :total-items="totalCardCount"
      />

    </div>

  </page-wrapper>
</template>

<script>

import LoginRequired from "./LoginRequired";
import MarketCard from "./MarketCard";
import ShowingResultsText from "@/components/ShowingResultsText";
import Pagination from "@/components/Pagination";
import CreateCardPage from "@/components/CreateCardPage";
import PageWrapper from "@/components/PageWrapper";
import { User } from "@/Api";

export default {
  name: "Marketplace",

  data() {
    return {
      tabSelected: 'ForSale', //Default tab
      createNewCard: false,
      cards: [],
      hideImages: true,
      error: "",
      order: 'created-asc',
      resultsPerPage: 10,
      page: 1
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

    /**
     * Returns true if the logged in account is acting as a user
     **/
    actingAsUser() {
      return this.actor.type === 'user'
    },

    /** Returns the current logged in users data **/
    actorName() {
      return this.actor.name
    },

    /**
     * Filters the cards to show only active cards.
     */
    filteredCards() {
      const newCards = []
      for (const card of this.cards) {
        const displayPeriodEnd = new Date(card.displayPeriodEnd)
        if (displayPeriodEnd > Date.now()) {
          newCards.push(card)
        }
      }
      return newCards
    },

    /** List of cards, ordered by the selected ordering **/
    orderedCards() {
      // Create new card array
      let newCards = [...this.filteredCards];

      // Order it appropriately
      if (this.order === 'created-asc') {
        newCards.sort((a, b) => -this.sortCreatedDate(a, b))
      } else if (this.order === 'created-desc') {
        newCards.sort((a, b) => this.sortCreatedDate(a, b))
      } else if (this.order === 'title') {
        newCards.sort((a, b) => this.sortTitle(a, b))
      } else if (this.order === 'location') {
        newCards.sort((a, b) => {
          this.sortLocation(a, b)
        })
      }

      return newCards
    },

    /** Total number of cards on the current tab **/
    totalCardCount() {
      return this.orderedCards.length
    }
  },
  components: {
    PageWrapper,
    LoginRequired,
    MarketCard,
    ShowingResultsText,
    Pagination,
    CreateCardPage
  },
  methods: {
    /**
     * Method to change contents of the page when tab is selected.
     * Gets called when a tab is selected and updates contents
     * @param tab selected. is a string
     */
    changePage(tab) {
      this.tabSelected = tab
      this.page = 1 // Reset the page number
      //Call Api to get new cards for tab here
      this.getCards(tab)
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

    /** Deletes a card with the corresponding id from the list of cards **/
    deleteCard(id) {
      const index = this.cards.findIndex((a) => a.id === id)
      if (index > -1) {
        this.cards.splice(index, 1)
      }
    },

    /**
     * Takes user to modal to create a new card
     */
    newCard() {
      this.createNewCard = true;
    },

    /**
     * Refreshes the card modal
     */
    refreshCards() {
      this.createNewCard = false;
      this.getCards(this.tabSelected)
    },

    /**
     * Gets all the cards for a particular section
     */
    getCards(tab){
      User.getCardsSection(tab)
          .then((res) => {
            this.error = "";
            this.cards = res.data
          })
          .catch((err) => {
            this.error = err;
          })
    },
  }
}

</script>

<style scoped>

.nav-item {
  font-size: 20px;
}

.row {
  margin-bottom: 20px;
}

</style>