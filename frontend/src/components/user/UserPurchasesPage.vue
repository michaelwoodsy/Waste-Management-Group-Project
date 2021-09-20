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
          <th scope="col">Date Purchased</th>
          <th scope="col">Product Name</th>
          <th scope="col">Quantity</th>
          <th scope="col">Price</th>
          <th scope="col">Business</th>
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
      purchases: [
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
    }
  },
  components: {
    PageWrapper,
    LoginRequired
  },
  async mounted() {
    await this.getCurrencies()
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
    }
  },
  methods: {
    /**
     * Gets the currencies of the purchases so that their prices can be formatted.
     */
    async getCurrencies() {
      for (let purchase of this.purchases) {
        purchase.currency = await this.$root.$data.product.getCurrency(purchase.currencyCountry)
      }
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