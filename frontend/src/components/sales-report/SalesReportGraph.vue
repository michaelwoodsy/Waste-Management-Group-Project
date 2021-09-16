<template>
  <div>
    <canvas id="myChart" style="width: 200px"></canvas>
    <button class="btn btn-primary"
            @click="toggleGraph()"
    >
      {{buttonText}}
    </button>
  </div>

</template>

<script>
import {Chart, registerables} from "chart.js";
import $ from "jquery";

export default {
  name: "SalesReportGraph",
  props: {
    data: Array
  },
  data(){
    return {
      chart: null,
      dates: [],
      totalValues: [],
      totalSales: [],
      dataLabel: "Total value",
      buttonText: "Show number of sales"
    }
  },
  mounted() {
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
      for (const period of this.data) {
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
              beginAtZero: true
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
      if (this.buttonText === "Show number of sales") {
        this.dataLabel = "Total number of sales"
        this.chart.destroy()
        this.drawGraph(this.dataLabel, this.totalSales)
        $("html, body").animate({ scrollTop: $(document).height() }, 0);
        this.buttonText = "Show total values"
      } else {
        this.dataLabel = "Total value"
        this.chart.destroy()
        this.drawGraph(this.dataLabel, this.totalValues)
        $("html, body").animate({ scrollTop: $(document).height() }, 0);
        this.buttonText = "Show number of sales"
      }

    }
  }
}
</script>

<style scoped>

</style>