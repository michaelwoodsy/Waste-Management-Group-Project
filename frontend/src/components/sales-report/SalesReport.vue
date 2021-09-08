<template>
  <div id="salesReport" :key="currency" class="accordion">

    <div v-for="(section, index) in data" :key="index" class="card">
      <div :id="`reportHeading${index}`" class="card-header">
        <div class="row align-items-center">
          <div class="col-6">
              <span class="text-muted">
                {{ formattedDate(section.periodStart) }} - {{ formattedDate(section.periodEnd) }}
              </span>
          </div>
          <div class="col">
            {{ section.purchaseCount }} sales
          </div>
          <div class="col">
            {{ formattedValue(section.totalPurchaseValue) }}
          </div>
          <div class="col text-right">
            <button :id="`section${index}Button`"
                    :data-target="`#reportSection${index}`"
                    class="btn btn-secondary btn-sm" data-toggle="collapse"
            >
              View Sales
            </button>
          </div>
        </div>
      </div>
      <div :id="`reportSection${index}`" class="collapse" data-parent="#salesReport">
        <sales-report-section :sales="section.sales"/>
      </div>
    </div>

  </div>
</template>

<script>
import SalesReportSection from "@/components/sales-report/SalesReportSection";
import product from '@/store/modules/product'

export default {
  name: "SalesReport",
  components: {SalesReportSection},
  props: {
    data: Array,
    currency: Object
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

    /**
     * Formats a monetary value to be displayed in a specific currency
     *
     * @param value the monetary value to format
     * @returns {string} the formatted value
     */
    formattedValue(value) {
      return product.formatPrice(this.currency, value)
    }
  }
}
</script>

<style scoped>

</style>