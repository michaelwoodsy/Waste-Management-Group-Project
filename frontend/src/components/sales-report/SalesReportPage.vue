<template>
  <div class="container-fluid">

    <h3 class="text-center">Sales Report</h3>

    <sales-report-controls
        @generate-report="getReport"
    />

    <sales-report
        v-if="report != null"
        :data="report"
        :currency="currency"
    />

    <SalesReportGraph></SalesReportGraph>

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
      currency: null,
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
        const res = await Business.getSalesReport(this.businessId, options)
        this.report = res.data
      } catch (error) {
        console.log(error)
      }
    }
  }
}
</script>

<style scoped>

</style>