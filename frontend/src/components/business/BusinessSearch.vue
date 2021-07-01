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
        <div class="col-sm-3"></div>
        <div class="col-sm-5">
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
        <!--    Select business type    -->
        <div class="col-sm-4">
          <select id="businessType" v-model="businessType" class="form-control"
                  required style="width:100%" type="text">
            <option disabled hidden selected value>Filter by business type</option>
            <!--    'Any type' option to choose not to filter by business type    -->
            <option>Any type</option>
            <option>Accommodation and Food Services</option>
            <option>Retail Trade</option>
            <option>Charitable organisation</option>
            <option>Non-profit organisation</option>
          </select>
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
import {Business} from "@/Api";

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
      businessType: "",
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
    /**
     * This is the search logic, that handles a call with or without businessType
     */
    search() {
      this.loading = true;
      this.page = 1;

      if(this.businessType === 'Any type' || this.businessType === '') {
        this.businessType = null;
      }

      Business.getBusinesses(this.searchTerm, this.businessType)
          .then((res) => {
            this.loadSearch(res)
          })
          .catch((err) => {
            this.error = err;
            this.loading = false;
          })

    },

    /**
     * Takes the response from the search request and saves them into a variable
     * @param res This is the response from the business search
     */
    loadSearch(res){
      this.error = null;
      this.businesses = res.data;
      this.loading = false;
      //TODO: Remove this when you are able to view businesses in table
      for(let business of this.businesses){
        console.log(business.name)
      }
    }

  }
}
</script>

<style scoped>
</style>