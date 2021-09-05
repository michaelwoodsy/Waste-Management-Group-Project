<!--Component for displaying an individual sale within a sales report-->
<template>
  <div>
    <table class="table table-hover"
           aria-label="Table to show a single sale"
    >
      <tr class="btn-secondary">
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
        <!--TODO: format price based on country-->
        <td>
          {{sale.price}}
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


  //TODO: remove me once sales report component gives me sale prop
  data() {
    return {
    sale: {
      dateSold: "2021-09-05",
      productId: "WATT-BEANS",
      productName: "Watties Baked Beans",
      quantity: 6,
      price: 5.50,
      currencyCountry: "New Zealand"
      },
    }
  },
  methods: {
    /**
     * Formats the date of the sale
     */
    formattedDate() {
      return formatDateTime(this.sale.dateSold)

    },
    /**
     * Formats the price of the sale
     */
    formattedPrice() {

    }

  }


}
</script>

<style scoped>

</style>