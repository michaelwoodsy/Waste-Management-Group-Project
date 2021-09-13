<template>
  <div class="col-12 d-flex">
    <div class="col-4">
      <div class="row justify-content-center">
        <h5 class="font-weight-bold">User Accounts</h5>
      </div>
      <div class="row justify-content-center">
        <h5>{{ stats.totalUserCount }}</h5>
      </div>
    </div>
    <div class="col-4">
      <div class="row justify-content-center">
        <h5 class="font-weight-bold">Current Listings</h5>
      </div>
      <div class="row justify-content-center">
        <h5>{{ stats.numAvailableListings }}</h5>
      </div>
    </div>
    <div class="col-4">
      <div class="row justify-content-center">
        <h5 class="font-weight-bold">Listings Sold</h5>
      </div>
      <div class="row justify-content-center">
        <h5>{{ stats.totalNumSales }}</h5>
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

</style>