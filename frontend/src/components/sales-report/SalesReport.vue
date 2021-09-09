<template>
  <div>

    <div class="row mb-2 text-center">
      <div class="col">
        <h5>Total Sales: {{ totalSales }}</h5>
      </div>
      <div class="col">
        <h5>Total Revenue: {{ formattedValue(totalValue) }}</h5>
      </div>
    </div>

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
  computed: {
    totalSales() {
      let sales = 0
      for (const section of this.data) {
        sales += section.purchaseCount
      }
      return sales
    },
    totalValue() {
      let value = 0
      for (const section of this.data) {
        value += section.totalPurchaseValue
      }
      return value
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