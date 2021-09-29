<!--Page for a user to view sale listings they have purchased -->
<template>
  <page-wrapper>
    <login-required
        v-if="!isLoggedIn"
        page="view your purchases"
    />

    <div v-else>
    <!--Page heading-->
    <div class="text-center">
      <h1>My Purchases</h1>
      <hr>
    </div>
    <!--Page content-->
    <div>
      <alert v-if="error">{{ error }}</alert>
      <div class="overflow-auto">
        <table aria-label="Table to view a user's purchases"
               class="table mb-0"
        >
          <thead>
          <tr>
            <th class="pointer" scope="col" @click="orderSearch('datePurchased')">
              <p class="d-inline">Date Purchased</p>
              <p v-if="orderCol === 'datePurchased'" class="d-inline" style="margin-left: 5px">{{ orderDirArrow }}</p>
            </th>
            <th class="pointer" scope="col" @click="orderSearch('productName')">
              <p class="d-inline">Product Name</p>
              <p v-if="orderCol === 'productName'" class="d-inline" style="margin-left: 5px">{{ orderDirArrow }}</p>
            </th>
            <th class="pointer" scope="col" @click="orderSearch('quantity')">
              <p class="d-inline">Quantity</p>
              <p v-if="orderCol === 'quantity'" class="d-inline" style="margin-left: 5px">{{ orderDirArrow }}</p>
            </th>
            <th class="pointer" scope="col" @click="orderSearch('price')">
              <p class="d-inline">Price</p>
              <p v-if="orderCol === 'price'" class="d-inline" style="margin-left: 5px">{{ orderDirArrow }}</p>
            </th>
            <th class="pointer" scope="col" @click="orderSearch('business')">
              <p class="d-inline">Business</p>
              <p v-if="orderCol === 'business'" class="d-inline" style="margin-left: 5px">{{ orderDirArrow }}</p>
            </th>
            <th scope="col">Review</th>
          </tr>
          </thead>
          <tbody v-if="!loading">
          <tr
              v-for="[index, purchase] of purchases.entries()"
              :key="index"
          >
            <td>
              {{ formattedDate(purchase.dateSold) }}
            </td>
            <td>
              {{ purchase.productName }}
            </td>
            <td>
              {{ purchase.quantity }}
            </td>
            <td>
              {{ formattedPrice(purchase) }}
            </td>
            <td>
              {{ purchase.business.name }}
            </td>
            <td>
              <button
                  class="btn btn-sm btn-primary"
                  data-toggle="modal"
                  data-target="#reviewModal"
                  @click="viewReview(purchase)"
              >
                <span v-if="!purchase.review">Leave Review</span>
                <span v-else>View Review</span>
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <!-- Show loading text until results are obtained -->
      <div v-if="loading" class="row">
        <span class="col text-center text-muted">Loading...</span>
      </div>

      <!--    Result Information    -->
      <div class="row">
        <div class="col">
          <div class="mb-2 text-center">
            <showing-results-text
                :items-per-page="resultsPerPage"
                :page="page"
                :total-count="totalCount"
            />
          </div>
          <div>
            <pagination
                :current-page.sync="page"
                :items-per-page="resultsPerPage"
                :total-items="totalCount"
                @change-page="changePage"
            />
          </div>
        </div>
      </div>
    </div>

    <ReviewModal v-if="showModal" :sale="purchaseToView" @update-data="fillTable" @close-modal="showModal = false"/>
    </div>
  </page-wrapper>
</template>

<script>
import PageWrapper from "@/components/PageWrapper";
import Pagination from "@/components/Pagination";
import ShowingResultsText from "@/components/ShowingResultsText";
import LoginRequired from "@/components/LoginRequired";
import Alert from "@/components/Alert";
import {formatDateTime} from "@/utils/dateTime";
import {User} from "@/Api";
import ReviewModal from "@/components/ReviewModal";

export default {
  name: "UserPurchasesPage",
  data() {
    return {
      purchases: [],
      error: null,
      orderCol: null,
      orderDirection: false, // False -> Ascending
      resultsPerPage: 10,
      totalCount: 0,
      page: 1,
      loading: false,
      purchaseToView: null,
      showModal: false
    }
  },
  components: {
    ReviewModal,
    PageWrapper,
    Pagination,
    ShowingResultsText,
    LoginRequired,
    Alert
  },
  async mounted() {
    await this.fillTable()
  },
  computed: {
    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    /**
     * Gets the users' ID
     * @returns {any}
     */
    userId() {
      return this.$root.$data.user.state.userId
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
    }
  },
  methods: {
    /**
     * Function is called by pagination component to make another call to the backend
     * to update the list of users that should be displayed
     */
    async changePage(page) {
      this.page = page
      this.loading = true;
      await this.fillTable()
    },
    /**
     * Fills the table with the users purchases
     */
    async fillTable() {
      this.loading = true;

      let sortBy = ""
      if (this.orderCol !== null) {
        sortBy = this.orderCol
        if (!this.orderDirection) {
          sortBy += "ASC"
        } else {
          sortBy += "DESC"
        }
      }
      await User.getPurchases(this.userId, {
        pageNumber: this.page - 1,
        sortBy: sortBy
      }).then(async (res) => {
        this.error = null;
        this.purchases = await this.$root.$data.product.addPurchasesCurrencies(res.data[0])
        this.totalCount = res.data[1]
        this.loading = false;
      }).catch((err) => {
        this.error = "Error: " + (err.response ? err.response.data.slice(err.response.data.indexOf(":") + 2) : err)
        this.loading = false;
      })
    },

    /**
     * Function called when you click on one of the columns to order the results
     * Makes another call to the backend to get the correct businesses when ordered
     */
    async orderSearch(sortBy) {
      if (this.orderCol !== sortBy) {
        this.orderDirection = false
      } else {
        this.orderDirection = !this.orderDirection
      }

      this.orderCol = sortBy

      await this.fillTable()
    },

    /**
     * Formats the date of the purchase
     * @return string date formatted like "DD/MM/YYYY hh:mm"
     */
    formattedDate(dateSold) {
      return formatDateTime(dateSold)
    },
    /**
     * Formats the price of the purchase
     * @return string price formatted like `{symbol}{number} {code}`
     */
    formattedPrice(purchase) {
      return this.$root.$data.product.formatPrice(purchase.currency, purchase.price);
    },
    /**
     * Updates the purchase to show in the review modal
     * @param purchase purchase to view
     */
    viewReview(purchase) {
      this.purchaseToView = purchase
      this.showModal = true
    }
  }
}
</script>
