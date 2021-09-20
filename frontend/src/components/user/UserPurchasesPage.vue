<!--Page for a user to view sale listings they have purchased -->
<template>
  <page-wrapper>
    <login-required
        v-if="!isLoggedIn"
        page="view your purchases"
    />
    <!--Page heading-->
    <div class="text-center">
      <h1>My Purchases</h1>
      <hr>
    </div>
    <!--Page content-->
    <div>
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
            No review
          </td>
        </tr>
      </table>
    </div>
  </page-wrapper>
</template>

<script>
import PageWrapper from "@/components/PageWrapper";
import LoginRequired from "@/components/LoginRequired";
import {formatDateTime} from "@/utils/dateTime";

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
      loading: false
    }
  },
  components: {
    PageWrapper,
    LoginRequired
  },
  async mounted() {
    this.purchases = await this.fillTable()
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
     * Fills the table with the users purchases
     */
    async fillTable() {
      let data = [
        {
          price: 15.00,
          dateSold: '2021-09-09',
          quantity: 10,
          productName: "Tinned Kidney Beans",
          currencyCountry: "New Zealand",
          business: {
            name: "Tinned Food Mart"
          }
        },
        {
          price: 6.50,
          dateSold: '2021-09-11',
          quantity: 3,
          productName: "Banana muffins",
          currencyCountry: "New Zealand",
          business: {
            name: "Myrtle's Muffins"
          }
        },
        {
          price: 12.00,
          dateSold: '2021-09-14',
          quantity: 10,
          productName: "Canned Lychees",
          currencyCountry: "Australia",
          business: {
            name: "Camilla's Canned Foods"
          }
        },
      ]
      for (let purchase of data) {
        purchase.currency = await this.$root.$data.product.getCurrency(purchase.currencyCountry)
      }
      return data
    },

    /**
     * Function called when you click on one of the columns to order the results
     * Makes another call to the backend to get the correct businesses when ordered
     */
    async orderSearch(sortBy) {
      this.loading = true;
      console.log(sortBy)

      if (this.orderCol !== sortBy) {
        this.orderDirection = false
      } else {
        this.orderDirection = !this.orderDirection
      }

      this.orderCol = sortBy
      if (!this.orderDirection) {
        sortBy += "ASC"
      } else {
        sortBy += "DESC"
      }

      // try {
      //   const res = await Business.getBusinesses(this.searchTerm, this.businessType, this.page - 1, sortBy)
      //   this.error = null;
      //   this.businesses = res.data[0];
      //   this.totalCount = res.data[1];
      //   this.loading = false;
      // } catch (error) {
      //   this.error = error;
      //   this.loading = false;
      // }
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
    }
  }
}
</script>

<style scoped>

</style>