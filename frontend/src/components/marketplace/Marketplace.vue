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
          <label class="d-inline-block option-label" for="order-select">Order By</label>
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

      <!-- Div above results for filtering -->
      <div class="row form justify-content-center">
        <!-- Combobox for filtering by keyword -->
        <div class="col form-group text-center">
          <!-- Collapsible because there will be a lot of keywords -->
          <label class="d-inline-block option-label" for="order-select"></label>
          <div id="keywords" class="collapse">
            <label
                v-for="keyword in keywordOptions"
                :key="keyword.id"
                class="ml-2 keyword"
            >
              <input type="checkbox" :id="keyword.id" @click="setKeywordSelect(keyword)" />
              {{keyword.name}}
            </label>
            <br>
            <button class="btn btn-primary ml-2">
              Apply
            </button>
          </div>
          <button data-target="#keywords" class="btn btn-outline-secondary m-2"
                  data-toggle="collapse" @click="toggleKeywordList">
            <span v-if="!showKeywords">Filter By Keywords <em class="bi bi-arrow-down"/></span>
            <span v-else>Hide Keywords <em class="bi bi-arrow-up"/></span>
          </button>

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
      page: 1,
      showKeywords: false,
      keywordOptions: [],
      selectedKeywords: []
    }
  },

  mounted() {
    this.changePage(this.tabSelected)
    this.populateKeywordOptions()
    this.sortKeywordOptions()
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
     * Populates the keyword options shown to filter by
     * Currently uses hard coded test data
     */
    populateKeywordOptions() {

      //TODO: actually get keywords from backend
      this.keywordOptions = [
        {
          id: 1,
          name: "Fruit"
        },
        {
          id: 2,
          name: "Apples"
        },
        {
          id: 3,
          name: "Bananas"
        },
        {
          id: 4,
          name: "Peaches"
        },
        {
          id: 5,
          name: "Vegetables"
        },
        {
          id: 6,
          name: "Carrots"
        },
        {
          id: 7,
          name: "Plums"
        },
        {
          id: 8,
          name: "Beans"
        },
        {
          id: 9,
          name: "Potatoes"
        },
        {
          id: 10,
          name: "Chips"
        },
        {
          id: 11,
          name: "Pies"
        },
        {
          id: 12,
          name: "Oranges"
        },
        {
          id: 13,
          name: "Celery"
        },
        {
          id: 14,
          name: "Pumpkins"
        },
        {
          id: 15,
          name: "Apricots"
        },
        {
          id: 16,
          name: "Cherries"
        },
        {
          id: 17,
          name: "Pears"
        },
        {
          id: 18,
          name: "Strawberries"
        }
      ]
    },
    /**
     * Sorts keywordOptions alphabetically by name
     */
    sortKeywordOptions() {
      this.keywordOptions.sort((a, b) => {
            if (a.name < b.name) {
              return -1
            }
            if ((a.name > b.name)) {
              return 1
            }
            return 0
          }
      )
    },
    /**
     * Toggles whether a keyword is selected to be filtered by
     */
    setKeywordSelect(keyword) {
      if (!this.selectedKeywords.includes(keyword.id)) {
        this.selectedKeywords.push(keyword.id)
      } else {
        this.selectedKeywords = this.selectedKeywords.filter((keywordId) => {
          return keywordId !== keyword.id
        })
      }
    },

    /**
     * Toggles the showKeywords field
     */
    toggleKeywordList() {
      this.showKeywords = !this.showKeywords
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
.option-label {
  font-size: 18px;
}
.keyword {
  font-size: 16px;
}

</style>