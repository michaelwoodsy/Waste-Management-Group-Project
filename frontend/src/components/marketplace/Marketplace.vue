<!--
Marketplace.vue
Page for displaying the marketplace.
-->

<template>
  <page-wrapper col-size="10">

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
                 @click="changeSection('ForSale')">
                For Sale
              </a>
            </li>
            <li class="nav-item">
              <a id="wanted-link"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'Wanted'}"
                 class="pointer"
                 @click="changeSection('Wanted')">
                Wanted
              </a>
            </li>
            <li class="nav-item">
              <a id="exchange-link"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'Exchange'}"
                 class="pointer"
                 @click="changeSection('Exchange')">
                Exchange
              </a>
            </li>
          </ul>
        </div>
        <div class="col">
          <button v-if="actingAsUser" class="btn btn-primary float-right" data-target="#createCard" data-toggle="modal"
                  @click="newCard">
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
        <div class="col-6 text-center">
          <div class="form-group">
            <div class="input-group">
              <!-- Combobox and label for ordering -->
              <div class="input-group-prepend">
                <span class="input-group-text">Order By</span>
              </div>
              <select id="order-select"
                      v-model="order"
                      class="form-control"
                      @change="searchCards"
              >
                <option value="newest">Newest</option>
                <option value="oldest">Oldest</option>
                <option value="title">Title</option>
                <option value="location">Location</option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <div class="input-group dropdown">
              <div class="input-group-prepend">
                <label class="input-group-text">Filter By</label>
              </div>
              <!-- Autocomplete dropdown -->
              <div id="autocompleteDropdown" class="dropdown-menu overflow-auto">
                <!-- If no user input -->
                <p v-if="keywordValue.length === 0"
                   class="text-muted dropdown-item left-padding mb-0 disabled"
                >
                  Start typing...
                </p>
                <!-- If no matches -->
                <p v-else-if="filteredKeywords.length === 0 && keywordValue.length > 0"
                   class="text-muted dropdown-item left-padding mb-0 disabled"
                >
                  No results found.
                </p>
                <!-- If there are matches -->
                <a v-for="keyword in filteredKeywords" v-else
                   :key="keyword.id"
                   class="dropdown-item pointer left-padding"
                   href="#"
                   @click="setKeyword(keyword)">
                  <span>{{ keyword.name }}</span>
                </a>
              </div>
              <!-- Keyword Input -->
              <input id="keywordSearchValue" v-model="keywordValue"
                     autocomplete="off"
                     class="form-control"
                     data-toggle="dropdown" maxlength="25"
                     placeholder="Enter Keywords"
                     required
                     type="text"
                     @click="showAutocomplete"
                     @input="searchKeywords"
                     @keyup.enter="setKeyword"/>
              <div v-if="keywords.length > 0" class="input-group-append">
                <!-- Button to clear keyword filter -->
                <button class="btn btn-danger" @click="clearFilter">
                  Clear
                </button>
              </div>
            </div>
          </div>
          <!-- Keyword Bubbles -->
          <div class="form-group row ml-0">
            <div class="float-left mr-2 my-1" v-for="(keyword, index) in keywords" :key="'keyword' + index">
              <div class="badge badge-primary align-items-center" style="font-size: medium; cursor: default">
                {{ keyword.name }}
                <span style="cursor: pointer" @click="removeKeyword(index)"><em class="bi bi-x"/></span>
              </div>
            </div>
          </div>
          <div v-if="keywords.length > 0" class="form-group row custom-control custom-switch m-2">
            <input id="any-all-keyword-switch" v-model="keywordUnion" class="custom-control-input"
                   type="checkbox">
            <label class="custom-control-label" for="any-all-keyword-switch">Match All</label>
          </div>
        </div>
      </div>

      <!-- Div with cards -->
      <div class="row row-cols-1 row-cols-lg-2">
        <div v-for="card in filteredCards" v-bind:key="card.id" class="col">
          <MarketCard :card-data="card" :hide-image="hideImages" :show-expired="false"
                      @card-deleted="deleteCard"
                      @refresh-cards="refreshCards()"></MarketCard>
        </div>
      </div>

      <div class="text-center">
        <!-- Text for "showing x of x results -->
        <showing-results-text
            :items-per-page="resultsPerPage"
            :page="page"
            :total-count="totalCount - expiredCardsLength"
        />
        <!-- Pagination Links -->
        <pagination
            :current-page.sync="page"
            :items-per-page="resultsPerPage"
            :total-items="totalCount - expiredCardsLength"
            class="mx-auto mt-2"
            @change-page="changePage"
        />
      </div>

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
import {Card, Keyword} from "@/Api";

export default {
  name: "Marketplace",

  data() {
    return {
      tabSelected: 'ForSale', //Default tab
      createNewCard: false,
      cards: [],
      hideImages: true,
      error: "",
      order: 'newest',
      resultsPerPage: 10,
      page: 1,
      totalCount: 1,
      keywordValue: '',
      keywordUnion: false,
      keywords: [],
      filteredKeywords: [],
    }
  },

  mounted() {
    this.changeSection(this.tabSelected)
  },

  watch: {
    /**
     * When keywords are added/removed, search for cards again
     */
    keywords() {
      this.searchCards()
    },
    /**
     * When keyword union is changed, search for cards again
     */
    keywordUnion() {
      this.searchCards()
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
    /**
     * Returns the number of expired cards
     *
     * @returns {number} Number of expired cards
     */
    expiredCardsLength() {
      return this.cards.length - this.filteredCards.length
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
    async changeSection(tab) {
      this.tabSelected = tab
      this.page = 1 // Reset the page number
      //Call Api to get new cards for tab here
      await this.searchCards()
    },

    /**
     * Method to change the currently viewed page
     * @param page the page to go to
     */
    async changePage(page) {
      this.page = page
      await this.searchCards()
    },

    /**
     * Deletes a card with the corresponding id from the list of cards
     */
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
      this.searchCards()
    },

    /**
     * Gets all the cards for a particular section
     */
    getCards(tab) {
      Card.getCardsSection(tab, this.page - 1, this.order)
          .then((res) => {
            this.error = "";
            this.cards = res.data["cards"]
            this.totalCount = res.data["totalCards"]
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
      if ((this.keywordValue === '' || this.keywordValue === ' ')
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
      //If enter was pressed on the input box to automatically select the first keyword in the list
      if (keyword === undefined && this.filteredKeywords.length > 0) {
        keyword = this.filteredKeywords[0]
      }
      const filterKeywords = this.keywords.filter(function (indKeyword) {
        return indKeyword.name === keyword.name;
      })
      if (filterKeywords.length === 0) {
        this.keywordValue = keyword.name
        this.addKeyword(keyword)
      } else {
        this.keywordValue = ''
      }
    },

    /**
     * Searches for cards by calling backend api endpoint and displaying the cards returned
     */
    async searchCards() {
      // Return if there are no keywords to search for
      if (this.keywords.length <= 0) return this.getCards(this.tabSelected)

      let apiParams = '?'
      for (const keyword of this.keywords) {
        apiParams += `keywordIds=${keyword.id}&`
      }
      apiParams += `section=${this.tabSelected}&`
      //union=false is match ALL, union=true is match ANY
      apiParams += `union=${!this.keywordUnion}&`
      apiParams += `page=${this.page - 1}&`
      apiParams += `sortBy=${this.order}`

      await Card.searchCards(apiParams)
          .then((res) => {
            this.error = "";
            this.cards = res.data['cards']
            this.totalCount = res.data['totalCards']
          })
          .catch((err) => {
            this.error = err;
          })
    },

    /**
     * Clears all the keywords from the list of filtered keywords and returns all the cards
     * for the selected tab
     */
    async clearFilter() {
      this.keywords = []
      this.getCards(this.tabSelected)
    },

    /**
     * Helper function to make sure autocomplete dropdown is not hidden when clicked on
     */
    showAutocomplete() {
      const dropdown = document.getElementById('autocompleteDropdown')
      if (dropdown.classList.contains('show')) {
        document.getElementById('keywordSearchValue').click()
      }
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