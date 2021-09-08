<!--Component for displaying an individual sale within a sales report-->
<template>
  <div>
    <table aria-label="Table to show a single sale"
           class="table mb-0"
    >
      <thead>
      <tr>
        <th scope="col">Date Sold</th>
        <th scope="col">Product ID</th>
        <th scope="col">Product Name</th>
        <th scope="col">Quantity Sold</th>
        <th scope="col">Sale Price</th>
        <th scope="col">Review</th>
      </tr>
      </thead>
      <tr
          v-for="[index, sale] of sales.entries()"
          :key="index"
      >
        <td>
          {{ formattedDate(sale.dateSold) }}
        </td>
        <td>
          {{ sale.productId }}
        </td>
        <td>
          {{ sale.productName }}
        </td>
        <td>
          {{ sale.quantity }} sold
        </td>
        <td>
          {{ formattedPrice(sale) }}
        </td>
        <td>
          No review
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
import {formatDateTime} from "@/utils/dateTime";

export default {
  name: "SalesReportSection",
  props: {
    sales: Array
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