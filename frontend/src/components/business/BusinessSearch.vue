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

          <!--    Order By   -->
          <div class="overflow-auto">
            <table class="table table-hover"
                   aria-label="Table showing business search results">
              <thead>
              <tr>
                <!--    ID    -->
                <th class="pointer" scope="col" @click="orderResults('id')">
                  <p class="d-inline">Id</p>
                  <p v-if="orderCol === 'id'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    First Name    -->
                <th class="pointer" scope="col" @click="orderResults('name')">
                  <p class="d-inline">Name</p>
                  <p v-if="orderCol === 'name'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Business Type    -->
                <th class="pointer" scope="col" @click="orderResults('businessType')">
                  <p class="d-inline">Type</p>
                  <p v-if="orderCol === 'businessType'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Address    -->
                <th class="pointer" scope="col" @click="orderResults('address')">
                  <p class="d-inline">Address</p>
                  <p v-if="orderCol === 'address'" class="d-inline">{{ orderDirArrow }}</p>
                </th>
              </tr>
              </thead>

              <!--    Business Information    -->
              <tbody v-if="!loading">
              <tr v-for="business in paginatedBusinesses"
                  v-bind:key="business.id"
                  class="pointer"
                  @click="viewBusiness(business.id)"
              >
                <th scope="row">
                  {{ business.id }}
                </th>
                <td>{{ business.name }}</td>
                <td>{{ business.businessType }}</td>
                <td>{{ formattedAddress(business.address) }}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div v-if="loading" class="row">
        <div class="col-12 text-center">
          <p class="text-muted">Loading...</p>
        </div>
      </div>

      <!--    Result Information    -->
      <div class="row">
        <div class="col-12">
          <pagination
              :current-page.sync="page"
              :items-per-page="resultsPerPage"
              :total-items="totalCount"
              class="mx-auto"
          />
        </div>
      </div>
    </div>

  </page-wrapper>
</template>

<script>
import ShowingResultsText from "@/components/ShowingResultsText";
import PageWrapper from "@/components/PageWrapper";
import Alert from "@/components/Alert";
import Pagination from "@/components/Pagination";
import {Business} from "@/Api";

export default {
  name: "BusinessSearch",
  components: {
    PageWrapper,
    ShowingResultsText,
    Alert,
    Pagination
  },
  data() {
    return {
      searchTerm: "",
      businessType: "",
      businesses: [],
      viewBusinessModal: false,
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
     * Checks which direction (ascending or descending) the order by should be
     * @returns {string}
     */
    orderDirArrow() {
      if (this.orderDirection) {
        return '↓'
      }
      return '↑'
    },

    /**
     * Sort Businesses Logic
     * @returns {[]|*[]}
     */
    sortedBusinesses() {
      if (this.orderCol === null) {
        return this.businesses
      }

      // Create new businesses array and sort
      let newBusinesses = [...this.businesses];
      // Order direction multiplier for sorting
      const orderDir = (this.orderDirection ? 1 : -1);

      // Sort businesses if there are any
      if (newBusinesses.length > 0) {
        // Sort businesses
        newBusinesses.sort((a, b) => {
          return orderDir * this.sortAlpha(a, b)
        });
      }

      return newBusinesses
    },

    /**
     * Paginate the businesses
     * @returns {*[]|*[]}
     */
    paginatedBusinesses() {
      let newBusinesses = this.sortedBusinesses;

      // Sort businesses if there are any
      if (newBusinesses.length > 0) {
        // Splice the results to showing size
        const startIndex = this.resultsPerPage * (this.page - 1);
        const endIndex = this.resultsPerPage * this.page;
        newBusinesses = newBusinesses.slice(startIndex, endIndex)
      }

      return newBusinesses
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
     * Search Logic
     */
    search() {
      this.blurSearch()
      this.businesses = []
      this.loading = true;
      this.page = 1

      if (this.businessType === 'Any type' || this.businessType === '') {
        Business.getBusinesses(this.searchTerm)
            .then((res) => {
              this.error = null;
              this.businesses = res.data;
              this.loading = false;
            })
            .catch((err) => {
              this.error = err;
              this.loading = false;
            })
      } else {
        Business.getBusinesses(this.searchTerm, this.businessType)
            .then((res) => {
              this.error = null;
              this.businesses = res.data;
              this.loading = false;
            })
            .catch((err) => {
              this.error = err;
              this.loading = false;
            })
      }
    },

    /**
     * Blurs the search.
     */
    blurSearch() {
      document.getElementById('search').blur()
    },

    /**
     * Function to order search results by specific column
     * @param col column to be sorted by
     */
    orderResults(col) {
      // Remove the ordering if the column is clicked and the arrow is down
      if (this.orderCol === col && this.orderDirection) {
        this.orderCol = null;
        this.orderDirection = false;
        return
      }

      // Updated order direction if the new column is the same as what is currently clicked
      this.orderDirection = this.orderCol === col;
      this.orderCol = col;
    },

    // Function for sorting a list by orderCol alphabetically
    sortAlpha(a, b) {
      if (a[this.orderCol] === null) {
        return -1
      }
      if (b[this.orderCol] === null) {
        return 1
      }
      if (a[this.orderCol] < b[[this.orderCol]]) {
        return 1;
      }
      if (a[this.orderCol] > b[[this.orderCol]]) {
        return -1;
      }
      return 0;
    },

    /**
     * Formats address of business by using their address object
     * @param address object that stores the business' home address
     * @returns {string}
     */
    formattedAddress(address) {
      return this.$root.$data.address.formatAddress(address)
    },

    /**
     * Router link to the clicked business' profile page
     * @param id
     */
    viewBusiness(id) {
      this.$router.push({name: 'viewBusiness', params: {businessId: id}})
    },
  }
}
</script>

<style scoped>

.pointer {
  cursor: pointer;
}

</style>