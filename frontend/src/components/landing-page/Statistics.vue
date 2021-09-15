<template>
  <div class="col-12 d-flex">
    <div class="col-4">
      <div class="card col-sm text-white bg-secondary text-center">
        <div class="card-body">
          <h3 class="card-title d-inline font-weight-bold">User Accounts</h3>
          <h3 id="userCountText" class="card-text">{{ stats.totalUserCount }}</h3>
        </div>
      </div>
    </div>
    <div class="col-4">
      <div class="card col-sm text-white bg-secondary text-center">
        <div class="card-body">
          <h3 class="card-title d-inline font-weight-bold">Current Listings</h3>
          <h3 id="availableListingsText" class="card-text">{{ stats.numAvailableListings }}</h3>
        </div>
      </div>
    </div>
    <div class="col-4">
      <div class="card col-sm text-white bg-secondary text-center">
        <div class="card-body">
          <h3 class="card-title d-inline font-weight-bold">Listings Sold</h3>
          <h3 id="totalSalesText" class="card-text">{{ stats.totalNumSales }}</h3>
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