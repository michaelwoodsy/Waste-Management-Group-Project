<template>
  <div class="row justify-content-center mb-3 align-content-center">
    <div class="col-4">
      <div class="card text-white bg-primary shadow">
        <div class="card-body">
          <h1 id="userCountText" class="card-text mb-0">{{ stats.totalUserCount }}</h1>
          <span class="card-title">User Accounts</span>
        </div>
      </div>
    </div>
    <div class="col-4">
      <div class="card text-white bg-primary shadow">
        <div class="card-body">
          <h1 id="availableListingsText" class="card-text mb-0">{{ stats.numAvailableListings }}</h1>
          <span class="card-title">Current Listings</span>
        </div>
      </div>
    </div>
    <div class="col-4">
      <div class="card text-white bg-primary shadow">
        <div class="card-body">
          <h1 id="totalSalesText" class="card-text mb-0">{{ stats.totalNumSales }}</h1>
          <span class="card-title">Listings Sold</span>
        </div>
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
    })
    console.log(this.stats)
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