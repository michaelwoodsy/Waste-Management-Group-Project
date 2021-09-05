<!--Component for displaying an individual sale within a sales report-->
<template>
  <div>
    <table class="table"
           aria-label="Table to show a single sale"
    >
      <tr class="bg-secondary text-light">
        <td>
          {{formattedDate()}}
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
          {{formattedPrice()}}
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
  name: "IndividualSale",

  //TODO: add me back in once sales report component gives me sale prop
  // props: {
  //   sale: Object
  // },



  data() {
    return {
      sale: { //TODO: remove me once sales report component gives me sale prop
        dateSold: "2021-09-05",
        productId: "WATT-BEANS",
        productName: "Watties Baked Beans",
        quantity: 6,
        price: 5.50,
        currencyCountry: "New Zealand"
      },
      currency: {}
    }
  },
  mounted() {
    this.getCurrency()
  },
  methods: {
    /**
     * Gets the currency of the sale so that the price can be formatted.
     */
    async getCurrency() {
      this.currency = await this.$root.$data.product.getCurrency(this.sale.currencyCountry)
    },
    /**
     * Formats the date of the sale
     * @return string date formatted like "DD/MM/YYYY hh:mm"
     */
    formattedDate() {
      return formatDateTime(this.sale.dateSold)

    },
    /**
     * Formats the price of the sale
     * @return string price formatted like `{symbol}{number} {code}`
     */
    formattedPrice() {
      return this.$root.$data.product.formatPrice(this.currency, this.sale.price);
    }
  }
}
</script>

<style scoped>

</style>