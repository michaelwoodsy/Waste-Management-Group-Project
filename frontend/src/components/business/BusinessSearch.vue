<!--
BusinessSearch.vue
Component on Search page for searching businesses
-->

<template>
  <page-wrapper>

    <div v-if="isLoggedIn" class="container-fluid">

      <!--    Search Businesses Header    -->
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h4>Search Businesses</h4>
        </div>
      </div>

      <!--    Error Alert    -->
      <div v-if="error" class="row">
        <div class="col-8 offset-2 text-center mb-2">
          <alert>{{ error }}</alert>
        </div>
      </div>

      <!--    Search Input    -->
      <div class="row mb-2">
        <div class="col-12 col-sm-8 col-lg-6 col-xl-4 col-centered">
          <div class="input-group">
            <input id="search"
                   v-model="searchTerm"
                   class="form-control no-outline"
                   placeholder="business name"
                   type="search"
                   @keyup.enter="search">
            <div class="input-group-append">
              <button class="btn btn-primary no-outline" type="button" @click="search">Search</button>
            </div>
          </div>
        </div>
      </div>


      <!--    Result Information    -->
      <div class="row justify-content-center">
        <div class="col-12">
          <div class="text-center">
            <showing-results-text
                :items-per-page="resultsPerPage"
                :page="page"
                :total-count="totalCount"
            />
          </div>
        </div>
      </div>






    </div>
  </page-wrapper>
</template>

<script>
import ShowingResultsText from "@/components/ShowingResultsText";
import PageWrapper from "@/components/PageWrapper";
import Alert from "@/components/Alert";

export default {
  name: "BusinessSearch",
  components: {
    PageWrapper,
    ShowingResultsText,
    Alert,
  },
  data() {
    return {
      searchTerm: "",
      businesses: [],
      error: null,
      orderCol: null,
      orderDirection: false, // False -> Ascending
      resultsPerPage: 10,
      page: 1,
      loading: false
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
     * Calculates the number of results in businesses array
     * @returns {number}
     */
    totalCount() {
      return this.businesses.length
    },
  },
  methods: {
    search() {
      //TODO: implement me
    }
  }
}
</script>

<style scoped>
.col-centered {
  margin: 0 auto;
  float: none;
}

</style>