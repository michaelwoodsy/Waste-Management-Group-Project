<template>
  <div class="col text-center">
    <h2>Popular Listings</h2>
    <div class="">
      <div id="popularListingCarousel" class="carousel slide w-auto" data-interval="false">
        <div class="carousel-inner">
          <!-- Carousel Slides -->
          <div class="carousel-item active">
            <div class="row justify-content-center" v-if="listingsList1">
                <div v-for="listing in listingsList1" v-bind:key="listing.id">
                  <PopularListing :data="listing" style="padding-right: 20px;padding-left: 20px"></PopularListing>
                </div>
            </div>
          </div>
          <div class="carousel-item">
            <div class="row justify-content-center" v-if="listingsList2">
              <div v-for="listing in listingsList2" v-bind:key="listing.id">
                <PopularListing :data="listing" style="padding-right: 20px;padding-left: 20px"></PopularListing>
              </div>
            </div>
          </div>
          <div class="carousel-item" v-if="listingsList3">
            <div class="row justify-content-center">
              <div v-for="listing in listingsList3" v-bind:key="listing.id">
                <PopularListing :data="listing" style="padding-right: 20px;padding-left: 20px"></PopularListing>
              </div>
            </div>
          </div>
        </div>
        <!-- Carousel Buttons -->
        <div v-if="listingsList2">
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
      listingsList1: null,
      listingsList2: null,
      listingsList3: null,
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
      let i = 0;
      if(this.listings.length === 10){
        //Maximum number of liked listings
        this.listingsList1 = this.listings.slice(0,4)
        this.listingsList2 = this.listings.slice(4,8)
        this.listingsList3 = this.listings.slice(8)
        this.listingsList3.push(this.listings[0])
        this.listingsList3.push(this.listings[1])
      } else if(this.listings.length < 10 && this.listings.length > 8){
        //8-9 liked listings
        this.listingsList1 = this.listings.slice(0,4)
        this.listingsList2 = this.listings.slice(4,8)
        this.listingsList3 = this.listings.slice(8)
        while(this.listingsList3.length !== 4){
          this.listingsList3.push(this.listings[i++])
        }
      } else if(this.listings.length === 8){
        //8 liked listings
        this.listingsList1 = this.listings.slice(0,4)
        this.listingsList2 = this.listings.slice(4,8)
      } else if(this.listings.length < 8 && this.listings.length > 4){
        //4-7 liked listings
        this.listingsList1 = this.listings.slice(0,4)
        this.listingsList2 = this.listings.slice(4)
        while(this.listingsList2.length !== 4){
          this.listingsList2.push(this.listings[i++])
        }
      }else {
        this.listingsList1 = this.listings.slice(0)
      }
    },

    async updateData() {
      await this.user.updateData()
    }
  }
}
</script>

<style scoped>
</style>