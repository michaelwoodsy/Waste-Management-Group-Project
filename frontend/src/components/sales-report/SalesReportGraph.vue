<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div class="form-group row" style="padding-top: 15px">
        <div class="input-group">
          <div class="input-group-prepend">
            <span class="input-group-text">Granularity</span>
          </div>
          <select v-model="options.granularity" class="custom-select" ref="select" >
            <option v-for="(option,index) of granularityOptions" :key="index" :value="option.toLowerCase()">
              {{ option }}
            </option>
          </select>
          <div class="input-group-append">
            <button class="btn btn-primary" @click="updateGraph">Update Graph</button>
          </div>
        </div>
      </div>
    </div>

    <canvas id="myChart" style="width: 200px"></canvas>
    <button class="btn btm-sm btn-primary"
            @click="toggleGraph()"
    >
      {{buttonText}}
    </button>
  </div>

</template>

<script>
import {Chart, registerables} from "chart.js";
import $ from "jquery";
import {Business} from "@/Api";

export default {
  name: "SalesReportGraph",
  props: {
    data: Array,
    currency: Object,
    granularity: String,
    businessId: Number
  },
  data(){
    return {
      chart: null,
      graphData: [],
      dates: [],
      totalValues: [],
      totalSales: [],
      dataLabel: "Total Value",
      buttonText: "Show Number of Sales",
      options: {
        periodStart: null,
        periodEnd: null,
        granularity: null
      },
      granularityOptions: [
        'All',
        'Yearly',
        'Monthly',
        'Weekly',
        'Daily'
      ],
    }
  },
  mounted() {
    this.options.granularity = this.granularity
    this.graphData = this.data
    this.options.periodStart = this.graphData[0].periodStart
    this.options.periodEnd = this.graphData[this.graphData.length-1].periodEnd
    this.setGraphInfo()
    this.drawGraph(this.dataLabel, this.totalValues)
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
     * Uses the report data to set:
     * - the list of dates shown as the x labels on the graph
     * - the list of total monetary values for each bar
     * - the list of total numbers of sales
     */
    setGraphInfo() {
      this.dates = []
      this.totalValues = []
      this.totalSales = []
      for (const period of this.graphData) {
        const startDate = this.formattedDate(period.periodStart)
        const endDate = this.formattedDate(period.periodEnd)
        let date = `${startDate}`
        if (startDate !== endDate) {
          date += ` - ${endDate}`
        }
        this.dates.push(date)
        this.totalValues.push(period.totalPurchaseValue)
        this.totalSales.push(period.purchaseCount)
      }
    },

    /**
     * Creates the graph
     */
    drawGraph(datasetLabel, data) {
      Chart.register(...registerables);
      const ctx = document.getElementById('myChart').getContext('2d');
      this.chart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: this.dates,
          datasets: [{
            label: datasetLabel, //dataset 0
            data: data,
            backgroundColor: [
              'rgba(66, 185, 131, 0.2)'
            ],
            borderColor: [

              'rgba(66, 185, 131, 1)'
            ],
            borderWidth: 1
          }]
        },
        options: {
          scales: {
            y: {
              beginAtZero: true,
              title: {
                display: true,
                text: this.buttonText === "Show Number of Sales" ?
                    `${this.currency.symbol}${this.currency.code}`
                    : "Sales"
              }
            }
          }
        }
      });
    },
    /**
     * Toggles between type of graph
     * from values to number of sales
     * and vice versa
     */
    toggleGraph() {
      if (this.buttonText === "Show Number of Sales") {
        this.dataLabel = "Total Number of Sales"
        this.chart.destroy()
        this.buttonText = "Show Total Values"
        this.drawGraph(this.dataLabel, this.totalSales)
      } else {
        this.dataLabel = "Total Value"
        this.chart.destroy()
        this.buttonText = "Show Number of Sales"
        this.drawGraph(this.dataLabel, this.totalValues)
      }
      $("html, body").animate({ scrollTop: $(document).height() }, 0);
    },

    /**
     * Updates the graph if there is a change of granularity
     * @returns {Promise<void>} this is the updated data with different granularity
     */
    async updateGraph() {
      try {
        const res = await Business.getSalesReport(this.businessId, this.options)
        this.graphData = res.data
        this.chart.destroy()
        this.setGraphInfo()
        this.drawGraph(this.dataLabel, this.totalValues)
        $("html, body").animate({ scrollTop: $(document).height() }, 0);
      } catch (error) {
        console.log(error)
      }
    }
  }
}
</script>

<style scoped>

</style>