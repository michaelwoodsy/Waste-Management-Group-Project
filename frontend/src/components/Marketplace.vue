<template>
  <div>

    <login-required
        v-if="!isLoggedIn"
        page="view the Marketplace"
    />
    <div v-else>
      <ul class="nav nav-pills nav-fill">
        <li class="nav-item">
          <a class="pointer" :class="{'nav-link': true, 'active': this.tabSelected === 'For Sale'}" @click="changePage('For Sale')">For Sale</a>
        </li>
        <li class="nav-item">
          <a class="pointer" :class="{'nav-link': true, 'active': this.tabSelected === 'Wanted'}" @click="changePage('Wanted')">Wanted</a>
        </li>
        <li class="nav-item">
          <a class="pointer" :class="{'nav-link': true, 'active': this.tabSelected === 'Exchange'}" @click="changePage('Exchange')">Exchange</a>
        </li>
      </ul>

      <div>
        <h1>{{ this.tabSelected }} Tab</h1>
      </div>
    </div>




  </div>
</template>

<script>

//import {User} from '@/Api'
import LoginRequired from "./LoginRequired";

export default {
  name: "Marketplace",
  props: {
    msg: String
  },

  data() {
    return {
      tabSelected: 'For Sale' //Default tab
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
      console.log(tab)

      this.tabSelected = tab
    }
  }
}

</script>

<style scoped>

</style>