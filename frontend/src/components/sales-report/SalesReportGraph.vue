<template>
  <canvas id="myChart" style="width: 200px"></canvas>
</template>

<script>
import {Chart, registerables} from "chart.js";

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
    }
  },
  mounted() {
    this.setGraphInfo() //TODO: details on graph don't change when granularity changes
    this.drawChart()
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
     * Creates the chart
     */
    drawChart() { //TODO: enable toggle between 2 graphs
      Chart.register(...registerables);
      var ctx = document.getElementById('myChart').getContext('2d');
      this.chart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: this.dates,
          datasets: [{
            label: 'Total value',
            data: this.totalValues,
            backgroundColor: [
              'rgba(75, 192, 192, 0.2)'
            ],
            borderColor: [

              'rgba(75, 192, 192, 1)'
            ],
            borderWidth: 1
          },{
            label: 'Total number of sales',
            data: this.totalSales,
            backgroundColor: [
              'rgba(255, 99, 132, 0.2)'
            ],
            borderColor: [
              'rgba(255, 99, 132, 1)'
            ],
            borderWidth: 1
          }
          ]
        },
        options: {
          scales: {
            y: {
              beginAtZero: true
            }
          }
        }
      });
    }
  }
}
</script>

<style scoped>

</style>