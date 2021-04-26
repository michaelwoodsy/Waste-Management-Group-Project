<template>
  <div>

    <login-required
        v-if="!isLoggedIn"
        page="view this business's sale listings"
    />

    <div v-else class="container-fluid">
      <div class="row justify-content-center">
        <div class="col col-xl-10">

          <!-- Sale Listings Header -->
          <div class="row">
            <div class="col"/>
            <div class="col text-center">
              <h4>Sale Listings</h4>
            </div>
            <div class="col"/>
          </div>

          <!-- Error Alert -->
          <div v-if="error" class="row">
            <div class="col text-center">
              <alert>{{ error }}</alert>
            </div>
          </div>

          <!-- Sale Listing Information -->
          <div>

            <!-- Number of results information -->
            <div class="text-center">
              <showing-results-text
                  :items-per-page="resultsPerPage"
                  :page="page"
                  :total-count="totalCount"
              />
            </div>

            <!-- Table of results -->
            <div class="overflow-auto">
              <table class="table table-hover">
                <thead>
                <tr>
                  <!-- Listing ID -->
                  <th class="pointer">
                    <p>ID</p>
                  </th>
                  <!-- Product code -->
                  <th class="pointer">
                    <p>Product Code</p>
                  </th>
                  <!-- Quantity of product being sold -->
                  <th class="pointer">
                    <p>Quantity</p>
                  </th>
                  <!-- Price of listing -->
                  <th class="pointer">
                    <p>Price</p>
                  </th>
                  <!-- Date listing was created -->
                  <th class="pointer">
                    <p>Created</p>
                  </th>
                  <!-- Date listing closes -->
                  <th class="pointer">
                    <p>Closes</p>
                  </th>
                </tr>
                </thead>
                <tbody v-if="!loading">
                <tr>
                  <td rowspan="2">1</td>
                  <td>NEW-1</td>
                  <td>2</td>
                  <td>$2.99</td>
                  <td>2021-04-26</td>
                  <td>2021-05-26</td>
                </tr>
                </tbody>
              </table>
            </div>

          </div>


        </div>
      </div>
    </div>

  </div>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import Alert from "@/components/Alert";
import ShowingResultsText from "@/components/ShowingResultsText";

export default {
  name: "SaleListings",
  components: {
    ShowingResultsText,
    LoginRequired,
    Alert
  },
  data() {
    return {
      listings: [],
      currency: null,
      error: null,
      orderCol: null,
      orderDirection: false, // False = Ascending
      resultsPerPage: 10,
      page: 1,
      loading: false
    }
  },
  computed: {
    /**
     * Gets the business ID
     * @returns {any} the business ID for which this listing page corresponds to
     */
    businessId() {
      return this.$route.params.businessId;
    },
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn;
    },
    /**
     * Checks which direction (ascending or descending) the order by should be
     * @returns {string}
     */
    orderDirArrow() {
      if (this.orderDirection) {
        return '↓';
      } else {
        return '↑';
      }
    },
    /**
     * Returns sales listings after sorting.
     */
    sortedListings() {
      if (this.orderCol === null) {
        return this.listings;
      }
      let newListings = [...this.listings];
      const orderDir = this.orderDirection ? 1 : -1;
      if (newListings.length > 0) {
        newListings.sort((a, b) => {
          return orderDir * this.sortAlpha(a, b);
        });
      }
      return newListings;
    },
    totalCount() {
      return this.listings.length;
    }
  },
  methods: {
    orderResults(col) {
      if (this.orderCol === col && this.orderDirection) {
        this.orderCol = null;
        this.orderDirection = false;
        return;
      }

      this.orderDirection = this.orderCol === col;
      this.orderCol = col;
    },
    /**
     * Function that sorts alphabetically by orderCol.
     * @param a first item to compare.
     * @param b second item to compare.
     * @returns {number} number representing which item should come first.
     */
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
    }
  }
}
</script>

<style scoped>

</style>