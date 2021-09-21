<template>
  <div>
    <alert> <!--TODO: v-if this and include actual date & currency values -->
      <strong>Totals include unconverted data in multiple currencies.</strong>
      <br>
      Data up to 01/09/2021 is in CNY and data after 01/09/2021 is in NZD.
      Please convert manually.
    </alert>

    <div id="salesReport" :key="currency" class="accordion shadow">

      <div class="card">
        <div class="card-header bg-secondary text-light">
          <div class="row align-items-center">
            <div class="col-6">
              <h5 class="mb-0">Sales Summary</h5>
            </div>
            <div class="col">
              <h5 class="mb-0">{{ totalSales }} Sales</h5>
            </div>
            <div class="col-4">
              <h5 class="mb-0">{{ formattedValue(totalValue) }}</h5>
            </div>
          </div>
        </div>
      </div>

      <div v-for="(section, index) in data" :key="section.periodStart" class="card">
        <div :id="`reportHeading${index}`" class="card-header">
          <div class="row align-items-center">
            <div class="col-6">
              <span class="text-muted">
                {{ formattedDate(section.periodStart) }}
                <span v-if="formattedDate(section.periodStart) !== formattedDate(section.periodEnd)">
                  - {{ formattedDate(section.periodEnd) }}
                </span>
              </span>
            </div>
            <div class="col">
              {{ section.purchaseCount }} Sales
            </div>
            <div class="col">
              {{ formattedValue(section.totalPurchaseValue) }}
            </div>
            <div class="col text-right">
              <button
                  v-if="section.sales.length > 0"
                  :id="`section${index}Button`"
                  :data-target="`#reportSection${index}`"
                  class="btn btn-secondary btn-sm" data-toggle="collapse"
              >
                View Sales
              </button>
            </div>
          </div>
        </div>
        <div :id="`reportSection${index}`" class="collapse" data-parent="#salesReport">
          <sales-report-section
              :business-currency="currency"
              :sales="section.sales"
          />
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import SalesReportSection from "@/components/sales-report/SalesReportSection";
import product from '@/store/modules/product'
import Alert from "@/components/Alert";

export default {
  name: "SalesReport",
  components: {Alert, SalesReportSection},
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