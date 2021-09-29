<template>
  <div class="container-fluid">

    <h3 class="text-center">Sales Report</h3>

    <sales-report-controls
        @generate-report="getReport"
    />

    <div class="text-center" v-if="!reportGenerated">
      <span>Click the button above to generate a sales report</span>
    </div>

    <div v-else>
      <div v-if="report != null">
        <sales-report
            v-if="report != null && report.length > 0"
            :data="report"
            :currency="currency"
        />
        <div class="card shadow mt-5">
          <div class="card-body">
            <sales-report-graph
                v-if="report != null"
                :data="report"
                :currency="currency"
                :granularity="options.granularity"
                :business-id="businessId"
                v-bind:key="reportChange"
            />
          </div>
        </div>
      </div>
      <div v-else class="text-center">
        <span>No sales for the selected period</span>
      </div>
    </div>
  </div>
</template>

<script>
import SalesReport from "@/components/sales-report/SalesReport";
import {Business} from "@/Api";
import product from "@/store/modules/product";
import SalesReportControls from "@/components/sales-report/SalesReportControls";
import SalesReportGraph from "./SalesReportGraph";

export default {
  name: "SalesReportPage",
  components: {SalesReportControls, SalesReport, SalesReportGraph},
  props: {
    businessId: Number
  },
  data() {
    return {
      report: null,
      reportChange: null,
      currency: null,
      reportGenerated: false,
      options: {
        startDate: null,
        endDate: null,
        granularity: null
      }
    }
  },
  async mounted() {
    await this.getCurrency()
  },
  methods: {
    /**
     * Gets the currency of the business to use for the report
     */
    async getCurrency() {
      try {
        const business = (await Business.getBusinessData(this.businessId)).data
        this.currency = await product.getCurrency(business.address.country)
      } catch (error) {
        console.error(error)
      }
    },
    /**
     * Sends request to get a sales report from backend
     *
     * @param options
     */
    async getReport(options) {
      try {
        this.reportGenerated = true
        const res = await Business.getSalesReport(this.businessId, options)
        this.options.granularity = options.granularity
        this.$set(this, "report", res.data)
        this.reportChange = this.report[0].periodStart + this.report[0].periodEnd
      } catch (error) {
        this.reportGenerated = false
        console.log(error)
      }
    }
  }
}
</script>
