<template>
  <div class="col text-center">
    <h2>Popular Listings</h2>
    <div class="">
      <div id="popularListingCarousel" class="carousel slide w-100" data-ride="carousel">
        <div class="carousel-inner">
          <div class="carousel-item active">
            <div class="row">
                <div v-for="listing in listingsList1" v-bind:key="listing.id" class="col">
                  <PopularListing :data="listing" @update-data="updateData" style="padding-left: 5px; padding-right: 5px"></PopularListing>
                </div>
            </div>
          </div>
          <div class="carousel-item">
            <div class="row">
              <div v-for="listing in listingsList2" v-bind:key="listing.id" class="col">
                <PopularListing :data="listing" @update-data="updateData" style="padding-left: 5px; padding-right: 5px"></PopularListing>
              </div>
            </div>
          </div>
          <div class="carousel-item">
            <div class="row">
              <div v-for="listing in listingsList3" v-bind:key="listing.id" class="col">
                <PopularListing :data="listing" @update-data="updateData" style="padding-left: 5px; padding-right: 5px"></PopularListing>
              </div>
            </div>
          </div>

        </div>
        <a class="carousel-control-prev" data-slide="prev" href="#popularListingCarousel" role="button">
          <span aria-hidden="true" class="carousel-control-prev-icon"></span>
          <span class="sr-only">Previous</span>
        </a>
        <a class="carousel-control-next" data-slide="next" href="#popularListingCarousel" role="button">
          <span aria-hidden="true" class="carousel-control-next-icon"></span>
          <span class="sr-only">Next</span>
        </a>
      </div>
    </div>
  </div>
</template>

<script>
import {Landing} from "@/Api";
import userState from "@/store/modules/user"
import PopularListing from "../sale-listing/PopularListing";

export default {
  name: "PopularListings",
  components: {
    PopularListing
  },
  async mounted() {
    await this.getPopularListings()
  },
  data() {
    return {
      user: userState,
      listings: [],
      listingsList1: [],
      listingsList2: [],
      listingsList3: [],
      country: "",
      error: ""
    }
  },
  methods: {
    /**
     * Gets the popular listings from the backend, as well as setting each sale listing's currency
     */
    async getPopularListings(){
      try {
        const response = await Landing.getPopularListings()
        for (let listing of response.data) {
          listing.currency = await this.$root.$data.product.getCurrency(listing.business.address.country)
          this.listings.push(listing)
        }
      } catch (error) {
        console.error(error)
        this.error = error
      }
      this.listingsList1 = this.listings.slice(0,4)
      this.listingsList2 = this.listings.slice(4,8)
      this.listingsList3 = this.listings.slice(7)
      this.listingsList3.push(this.listings[0])
    },


    async updateData() {
      await this.user.updateData()
    }
  }
}
</script>

<style scoped>
</style>