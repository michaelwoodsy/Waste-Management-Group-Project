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

      <!-- Div above results for ordering -->
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

      <!-- Div above results for filtering by keywords -->
      <div class="row form justify-content-center">
        <div class="col form-group text-center">
          <label class="d-inline-block" for="order-select">Filter By Keywords</label>
          <!-- Keyword Input -->
          <input id="keywordSearchValue" v-model="keywordValue"
                 class="form-control ml-2 d-inline-block w-auto"
                 placeholder="Enter Keywords"
                 required maxlength="25" type="text"
                 style="margin-bottom: 2px"
                 autocomplete="off"
                 data-toggle="dropdown"
                 @input="searchKeywords"/>
          <button
              :class="{disabled: keywords.length <= 0}"
              class="btn btn-primary ml-2" @click="searchCards">
            Apply
          </button>
          <button
              v-if="keywords.length > 0"
              class="btn btn-danger ml-2" @click="clearFilter">
            Clear
          </button>
          <span class="custom-control custom-switch m-2">
            <input v-model="keywordUnion" type="checkbox" class="custom-control-input" id="any-all-keyword-switch">
            <label class="custom-control-label" for="any-all-keyword-switch">Match all</label>
          </span>
          <!-- Autocomplete dropdown -->
          <div class="dropdown-menu overflow-auto" id="dropdown">
            <!-- If no user input -->
            <p class="text-muted dropdown-item left-padding mb-0 disabled"
               v-if="keywordValue.length === 0"
            >
              Start typing...
            </p>
            <!-- If no matches -->
            <p class="text-muted dropdown-item left-padding mb-0 disabled"
               v-else-if="filteredKeywords.length === 0 && keywordValue.length > 0"
            >
              No results found.
            </p>
            <!-- If there are matches -->
            <a class="dropdown-item pointer left-padding"
               v-for="keyword in filteredKeywords"
               v-else
               :key="keyword.id"
               @click="setKeyword(keyword)">
              <span>{{ keyword.name }}</span>
            </a>
          </div>
          <!-- Keyword Bubbles -->
          <div class="keyword">
            <button
                class="btn btn-primary d-inline-block m-2"
                v-for="(keyword, index) in keywords"
                :key="'keyword' + index">
              <span>{{  keyword.name  }}</span>
              <span @click="removeKeyword(index)"><em class="bi bi-x"></em></span>
            </button>
          </div>
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

import LoginRequired from "../LoginRequired";
import MarketCard from "./MarketCard";
import ShowingResultsText from "@/components/ShowingResultsText";
import Pagination from "@/components/Pagination";
import CreateCardPage from "@/components/marketplace/CreateCardPage";
import PageWrapper from "@/components/PageWrapper";
import {Keyword, User, Card} from "@/Api";

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
      page: 1,
      keywordValue: '',
      keywordUnion: false,
      keywords: [],
      filteredKeywords: []
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
      if (a.created < b.created) {
        return -1
      }
      if ((a.created > b.created)) {
        return 1
      }
      return 0
    },

    /** Function for sorting a list by title alphabetically **/
    sortTitle(a, b) {
        if (a.title < b.title) {
          return -1
        }
        if ((a.title > b.title)) {
          return 1
        }
        return 0
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
    /**
     * Adds a keyword to the list of keywords
     */
    addKeyword(keyword) {
      this.keywordValue = this.keywordValue.trim()
      if((this.keywordValue === '' || this.keywordValue === ' ')
          || this.keywords.includes(this.keywordValue)) {
        this.keywordValue = '';
      }
      if (this.keywordValue.length > 2) {
        this.keywords.push(
            {
              id: keyword.id,
              name: this.keywordValue
            });
        this.keywordValue = '';
      }
    },

    /**
     * Removes a keyword from the list of keywords
     * @param index Index of the keyword in the keyword list
     */
    removeKeyword(index) {
      this.keywords.splice(index, 1)
    },

    /**
     * Filters autocomplete options based on the user's input for a keyword.
     */
    async searchKeywords() {
      if (this.keywordValue.length > 2) {
        await Keyword.searchKeywords(this.keywordValue)
            .then((response) => {
              this.filteredKeywords = response.data;
            })
            .catch((err) => {
              console.log(err)
            })
      } else {
        this.filteredKeywords = []
      }
    },

    /**
     * Adds a keyword to the list of keywords if the keyword was selecting from the
     * autocomplete list rather than by pressing the spacebar
     * @param keyword Keyword to be added to keyword list
     */
    setKeyword(keyword) {
      this.keywordValue = keyword.name
      this.addKeyword(keyword)
    },

    /**
     * Searches for cards by calling backend api endpoint and displaying the cards returned
     */
    async searchCards() {
      //return if there are no keywords to search for
      if (this.keywords.length <= 0) return

      let apiParams = '?'
      for (const keyword of this.keywords) {
        apiParams += `keywordIds=${keyword.id}&`
      }
      apiParams += `section=${this.tabSelected}&`
      //union=false is match ALL, union=true is match ANY
      apiParams += `union=${!this.keywordUnion}`

      await Card.searchCards(apiParams)
          .then((res) => {
            this.error = "";
            this.cards = res.data
          })
          .catch((err) => {
            this.error = err;
          })
    },

    async clearFilter() {
      this.keywords = []
      this.getCards(this.tabSelected)
    }

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
.keyword {
  font-size: 16px;
}

</style>