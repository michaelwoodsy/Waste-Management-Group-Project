<template>
  <page-wrapper>

    <login-required
        v-if="!isLoggedIn"
        page="view this business's sale listings"
    />

    <div v-else class="container-fluid">
      <div class="row justify-content-center">
        <div class="col">

          <!-- Sale Listings Header -->
          <div class="row">
            <div class="col"/>
            <div class="col text-center">
              <h4>Sale Listings</h4>
            </div>
            <div class="col text-right">
              <button v-if="isAdminOf" class="btn btn-primary" data-target="#createListing" data-toggle="modal"
                      @click="newListing">
                New Listing
              </button>
              <!-- GAA/DGAA button to add sales listings -->
              <button v-else-if="$root.$data.user.canDoAdminAction()" class="btn btn-danger"
                      data-target="#createListing" data-toggle="modal"
                      @click="newListing">
                New Listing
              </button>
            </div>
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
                  <!-- Product code -->
                  <th class="pointer" scope="col" @click="orderResults('name')">
                    <p class="d-inline">Product Info</p>
                    <p v-if="orderCol === 'name'" class="d-inline">{{ orderDirArrow }}</p>
                  </th>
                  <!-- Quantity of product being sold -->
                  <th class="pointer" scope="col" @click="orderResults('quantity')">
                    <p class="d-inline">Quantity</p>
                    <p v-if="orderCol === 'quantity'" class="d-inline">{{ orderDirArrow }}</p>
                  </th>
                  <!-- Price of listing -->
                  <th class="pointer" scope="col" @click="orderResults('price')">
                    <p class="d-inline">Price</p>
                    <p v-if="orderCol === 'price'" class="d-inline">{{ orderDirArrow }}</p>
                  </th>
                  <!-- Date listing was created -->
                  <th class="pointer" scope="col" @click="orderResults('created')">
                    <p class="d-inline">Created</p>
                    <p v-if="orderCol === 'created'" class="d-inline">{{ orderDirArrow }}</p>
                  </th>
                  <!-- Date listing closes -->
                  <th class="pointer" scope="col" @click="orderResults('closes')">
                    <p class="d-inline">Closes</p>
                    <p v-if="orderCol === 'closes'" class="d-inline">{{ orderDirArrow }}</p>
                  </th>
                </tr>
                </thead>
                <tbody v-if="!loading">
                <tr v-for="item in paginatedListings" v-bind:key="item.id">
                  <td style="word-break: break-word; width: 50%">
                    {{ item.inventoryItem.product.name }}
                    <span v-if="item.moreInfo" style="font-size: small"><br/>{{ item.moreInfo }}</span>
                  </td>
                  <td>{{ item.quantity }}</td>
                  <td>{{ formatPrice(item.price) }}</td>
                  <td>{{ formatDate(item.created) }}</td>
                  <td>{{ formatDate(item.closes) }}</td>
                </tr>
                </tbody>
              </table>
            </div>

          </div>

          <!-- Show loading text until results are obtained -->
          <div v-if="loading" class="row">
            <span class="col text-center text-muted">Loading...</span>
          </div>

          <!--    Result Information    -->
          <div class="row">
            <div class="col">
              <pagination
                  :current-page.sync="page"
                  :items-per-page="resultsPerPage"
                  :total-items="totalCount"
              />
            </div>
          </div>

        </div>
      </div>
    </div>

    <div id="createListing" :key="this.createNewListing" class="modal fade" data-backdrop="static">
      <div ref="createListingWindow" class="modal-dialog modal-open">
        <div class="modal-content">
          <div class="modal-body">
            <create-listing @refresh-listings="refreshListings"></create-listing>
          </div>
        </div>
      </div>
    </div>

  </page-wrapper>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import Alert from "@/components/Alert";
import ShowingResultsText from "@/components/ShowingResultsText";
import Pagination from "@/components/Pagination";
import {Business} from "@/Api";
import CreateListing from "@/components/CreateListing";
import PageWrapper from "@/components/PageWrapper";

export default {
  name: "SaleListings",
  components: {
    PageWrapper,
    CreateListing,
    ShowingResultsText,
    LoginRequired,
    Alert,
    Pagination
  },

  data() {
    return {
      listings: [],
      currency: {
        symbol: "",
        code: ""
      },
      error: null,
      orderCol: null,
      orderDirection: false, // False = Ascending
      resultsPerPage: 10,
      page: 1,
      loading: false,
      createNewListing: false
    }
  },

  mounted() {
    this.getCurrency();
    this.fillTable();
  },

  computed: {
    /**
     * Gets the business ID
     * @returns {any} the business ID for which this listing page corresponds to
     */
    businessId() {
      return parseInt(this.$route.params.businessId);
    },

    /**
     * Returns whether or not the user is logged in.
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn;
    },

    /**
     * Check if the user is an admin of the business and is acting as that business
     */
    isAdminOf() {
      if (this.$root.$data.user.state.actingAs.type !== "business") return false
      return this.$root.$data.user.state.actingAs.id === this.businessId;
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

    /**
     * Returns paginated sale listings.
     */
    paginatedListings() {
      let newListings = this.sortedListings;
      if (newListings.length > 0) {
        const startIndex = this.resultsPerPage * (this.page - 1);
        const endIndex = this.resultsPerPage * this.page;
        newListings = newListings.slice(startIndex, endIndex);
      }
      return newListings;
    },

    /**
     * The total number of listings.
     */
    totalCount() {
      return this.listings.length;
    },
  },

  watch: {
    /**
     * Called when the businessId is changed, this occurs when the path variable for the business id is updated
     */
    businessId() {
      this.fillTable()
    }
  },

  methods: {
    /**
     * Method which sets the column and direction to order by.
     */
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
     * Function which returns the value of a specified column. Used for sorting.
     * @param item the item to get field from.
     * @returns value of the specified column.
     */
    getCol(item) {
      switch (this.orderCol) {
        case 'name':
          return item.inventoryItem.product.name;
        case 'quantity':
          return item.quantity;
        case 'price':
          return item.price;
        case 'created':
          return item.created;
        case 'closes':
          return item.closes;
        default:
          return item.id;
      }
    },

    /**
     * Function that sorts alphabetically by orderCol.
     */
    sortAlpha(a, b) {
      if (this.getCol(a) === null) {
        return -1
      }
      if (this.getCol(b) === null) {
        return 1
      }
      if (this.getCol(a) < this.getCol(b)) {
        return 1;
      }
      if (this.getCol(a) > this.getCol(b)) {
        return -1;
      }
      return 0;
    },

    /**
     * Formats the date fields.
     * @param string string representation of the date.
     * @returns {string} the formatted date.
     */
    formatDate(string) {
      if (string === '') {
        return '';
      } else {
        return new Date(string).toDateString();
      }
    },

    /**
     * Formats the price to correct currency.
     */
    formatPrice(price) {
      return this.$root.$data.product.formatPrice(this.currency, price);
    },

    /**
     * Gets the currency to use for a particular business.
     */
    async getCurrency() {
      const country = (await Business.getBusinessData(this.businessId)).data.address.country;
      this.currency = await this.$root.$data.product.getCurrency(country);
    },

    /**
     * Fills the table with a business's sale listings.
     */
    fillTable() {
      this.listings = [];
      this.loading = true;
      this.page = 1;

      Business.getListings(this.$route.params.businessId)
          .then((res) => {
            this.error = null;
            this.listings = res.data;
            console.log(res.data)
            this.loading = false;
          })
          .catch((err) => {
            this.error = err;
            this.loading = false;
          })
    },

    /**
     * Takes user to page to add a new listing.
     */
    newListing() {
      this.createNewListing = true;
    },
    refreshListings() {
      this.createNewListing = false;
      this.fillTable();
    }
  }
}
</script>

<style scoped>

</style>