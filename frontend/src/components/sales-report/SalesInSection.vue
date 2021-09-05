<!--Component for displaying an individual sale within a sales report-->
<template>
  <div>
    <table class="table"
           aria-label="Table to show a single sale"
    >
      <tr
          class="bg-secondary text-light"
          v-for="sale in sales"
          :key="sale.productId"
      >
        <td>
          {{formattedDate(sale.dateSold)}}
        </td>
        <td>
          {{sale.productId}}
        </td>
        <td>
          {{sale.productName}}
        </td>
        <td>
          {{sale.quantity}} sold
        </td>
        <td>
          {{formattedPrice(sale)}}
        </td>
        <td>
          No reviews
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
import {formatDateTime} from "@/utils/dateTime";

export default {
  name: "SalesInSection",

  //TODO: add me back in once sales report component gives me sales prop
  // props: {
  //   sales: Object
  // },



  data() {
    return {
      sales: [ //TODO: remove me once sales report component gives me sales prop
        {
          dateSold: "2021-09-04",
          productId: "WATT-BEANS",
          productName: "Watties Baked Beans",
          quantity: 6,
          price: 5.50,
          currencyCountry: "New Zealand",
          currency: {}
        },
        {
          dateSold: "2021-09-05",
          productId: "KID-BEANS",
          productName: "Value Kidney Beans",
          quantity: 4,
          price: 3.50,
          currencyCountry: "Australia",
          currency: {}
        },
      ]
    }
  },
  mounted() {
    this.getCurrencies()
  },
  methods: {
    /**
     * Gets the currencies of the sales so that their prices can be formatted.
     */
    async getCurrencies() {
      for (let sale of this.sales) {
        sale.currency = await this.$root.$data.product.getCurrency(sale.currencyCountry)
      }
    },
    /**
     * Formats the date of the sale
     * @return string date formatted like "DD/MM/YYYY hh:mm"
     */
    formattedDate(dateSold) {
      return formatDateTime(dateSold)

    },
    /**
     * Formats the price of the sale
     * @return string price formatted like `{symbol}{number} {code}`
     */
    formattedPrice(sale) {
      return this.$root.$data.product.formatPrice(sale.currency, sale.price);
    }
  }
}
</script>

<style scoped>

</style>