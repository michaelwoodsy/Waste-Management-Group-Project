<template>
  <div class="col-12">

    <login-required
        v-if="!isLoggedIn"
        page="view the Marketplace"
    />
    <div v-else>
      <div class="row justify-content-center">

        <!--    Div for marketplace tabs    -->
        <div class="col-6">
          <ul class="nav nav-pills nav-fill">
            <li class="nav-item">
              <a id="for-sale-link"
                 class="pointer"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'for sale'}"
                 @click="changePage('for sale')">
                For Sale
              </a>
            </li>
            <li class="nav-item">
              <a id="wanted-link"
                 class="pointer"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'wanted'}"
                 @click="changePage('wanted')">
                Wanted
              </a>
            </li>
            <li class="nav-item">
              <a id="exchange-link"
                 class="pointer"
                 :class="{'nav-link': true, 'active': this.tabSelected === 'exchange'}"
                 @click="changePage('exchange')">
                Exchange
              </a>
            </li>
          </ul>
        </div>
        <div class="col text-right">
          <button class="btn btn-primary" data-target="#createCard" data-toggle="modal" @click="newCard">
            New Card
          </button>
        </div>
      </div>

      <br>
      <div class="row justify-content-center">
        <div class="col-9">
          <div v-for="card in cards" v-bind:key="card.id">
            <div>
              <h1>{{tabSelected}} {{ card }}</h1>
            </div>

          </div>
        </div>
      </div>
      <div id="createCard" :key="this.createNewCard" class="modal fade" data-backdrop="static">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-body">
              <create-card-page @refresh-cards="refreshCards"></create-card-page>
            </div>
          </div>
        </div>
      </div>
    </div>




  </div>
</template>

<script>

import LoginRequired from "./LoginRequired";
import CreateCardPage from "@/components/CreateCardPage";

export default {
  name: "Marketplace",

  data() {
    return {
      tabSelected: 'for sale', //Default tab
      cards: ['Card 1', 'Card 2', 'card 3', 'card 4', 'card 5'],
      error: "",
      createNewCard: false
    }
  },

  mounted() {

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
  },
  components: {
    CreateCardPage,
    LoginRequired
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
    },
    /**
     * Takes user to page to create new card.
     */
    newCard() {
      this.createNewCard = true;
    },
    /**
     * Refreshes the cards
     */
    refreshCards() {
      this.createNewCard = false;
    }
  }
}

</script>

<style scoped>
.nav-item {
  font-size: 20px;
}
</style>