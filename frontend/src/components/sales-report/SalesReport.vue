<template>
  <div id="salesReport" class="accordion">

    <div v-for="[index, section] of data.entries()" :key="index" class="card">
      <div :id="`salesReportHeading${index}`" class="card-header">
        <div class="row align-items-center">
          <div class="col-6">
              <span class="text-muted">
                {{ formattedDate(section.dateStart) }} - {{ formattedDate(section.dateEnd) }}
              </span>
          </div>
          <div class="col">
            {{ section.totalSales }} sales
          </div>
          <div class="col">
            {{ formattedSaleAmount(section.total) }}
          </div>
          <div class="col text-right">
            <button :id="`section${index}Button`"
                    :data-target="`#salesReportSection${index}`"
                    class="btn btn-secondary btn-sm" data-toggle="collapse"
            >
              View Sales
            </button>
          </div>
        </div>
      </div>
      <div :id="`salesReportSection${index}`" class="collapse" data-parent="#salesReport">
        <sales-report-section :sales="section.sales"/>
      </div>
    </div>

  </div>
</template>

<script>
import SalesReportSection from "@/components/sales-report/SalesReportSection";
import product from '@/store/modules/product'
import user from "@/store/modules/user"

export default {
  name: "SalesReport",
  components: {SalesReportSection},
  props: {
    data: Array
  },
  data() {
    return {
      currency: null
    }
  },
  async mounted() {
    try {
      this.currency = await product.getCurrency(user.actor().businessData.address.country)
    } catch (error) {
      console.error(error)
    }
  },
  methods: {
    /**
     * Formats a date string for displaying
     *
     * @param date date string to format
     * @returns {string} formatted date string
     */
    formattedDate(date) {
      return new Date(date).toDateString()
    },
    formattedSaleAmount(saleAmount) {
      return product.formatPrice(this.currency, saleAmount)
    }
  }
}
</script>

<style scoped>

</style>