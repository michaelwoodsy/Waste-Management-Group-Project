<!--
Marketplace.vue
Page for displaying the marketplace.
-->

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
    </div>




  </div>
</template>

<script>

import LoginRequired from "./LoginRequired";

export default {
  name: "Marketplace",

  data() {
    return {
      tabSelected: 'for sale', //Default tab
      cards: ['Card 1', 'Card 2', 'card 3', 'card 4', 'card 5'],
      error: ""
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
    }
  }
}

</script>

<style scoped>
.nav-item {
  font-size: 20px;
}
</style>