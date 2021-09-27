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
          <span v-if="sale.currency">{{ formattedPrice(sale) }}</span>
        </td>
        <td>
          <div v-if="!sale.review">No review</div>
          <div v-else>
            <button
                class="btn btn-sm btn-primary"
                data-toggle="modal"
                data-target="#reviewModal"
                @click="viewReview(sale)"
            >
              View
            </button>
          </div>
        </td>
      </tr>
    </table>

    <ReviewModal v-if="viewModal" :sale="saleToView" @close-modal="viewModal = false"/>

  </div>
</template>

<script>
import {formatDateTime} from "@/utils/dateTime";
import ReviewModal from "@/components/ReviewModal";

export default {
  name: "SalesReportSection",
  components: {ReviewModal},
  props: {
    sales: Array,
  },
  data() {
    return {
      saleToView: null,
      viewModal: false
    }
  },
  methods: {
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
    },
    /**
     * Toggles the sale review modal
     *
     * @param sale the sale to display in the review modal
     */
    viewReview(sale) {
      this.saleToView = sale
      this.viewModal = true
    }
  }
}
</script>

<style scoped>

</style>