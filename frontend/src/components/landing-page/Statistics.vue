<template>
  <div class="row mb-3">
    <div class="card col-sm text-white bg-secondary ml-2 mr-2 text-center">
      <div class="card-body">
        <h3 class="card-title d-inline font-weight-bold">User Accounts</h3>
        <h3 id="userCountText" class="card-text">{{ stats.totalUserCount }}</h3>
      </div>
    </div>
    <div class="card col-sm text-white bg-secondary ml-2 mr-2 text-center">
      <div class="card-body">
        <h3 class="card-title d-inline font-weight-bold">Current Listings</h3>
        <h3 id="availableListingsText" class="card-text">{{ stats.numAvailableListings }}</h3>
      </div>
    </div>
    <div class="card col-sm text-white bg-secondary ml-2 mr-2 text-center">
      <div class="card-body">
        <h3 class="card-title d-inline font-weight-bold">Listings Sold</h3>
        <h3 id="totalSalesText" class="card-text">{{ stats.totalNumSales }}</h3>
      </div>
    </div>
  </div>
</template>

<script>
import { Statistics } from "@/Api";

export default {
  name: "Statistics",

  data() {
    return {
      stats: {
        numAvailableListings: null,
        totalNumSales: null,
        totalUserCount: null
      }
    }
  },

  async mounted() {
    await Statistics.getStatistics().then((res) => {
      this.stats = res.data
      this.formatNumberWithCommas()
    })
    console.log(this.stats)
  },

  methods: {
    /***
     * Converts the statistic values into numbers with commas
     * in them as thousands separators
     */
    formatNumberWithCommas() {
      this.stats.numAvailableListings = this.stats.numAvailableListings.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      this.stats.totalNumSales = this.stats.totalNumSales.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      this.stats.totalUserCount = this.stats.totalUserCount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
  }
}
</script>

<style scoped>

.card {
  padding: 10px;
}

h3 {
  margin: 10px;
}

</style>