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
                  <PopularListing :data="listing" style="padding-right: 20px;padding-left: 20px" @update-data="updateData"></PopularListing>
                </div>
            </div>
          </div>
          <div class="carousel-item">
            <div class="row justify-content-center" v-if="listingsList2">
              <div v-for="listing in listingsList2" v-bind:key="listing.id">
                <PopularListing :data="listing" style="padding-right: 20px;padding-left: 20px" @update-data="updateData"></PopularListing>
              </div>
            </div>
          </div>
          <div class="carousel-item" v-if="listingsList3">
            <div class="row justify-content-center">
              <div v-for="listing in listingsList3" v-bind:key="listing.id">
                <PopularListing :data="listing" style="padding-right: 20px;padding-left: 20px" @update-data="updateData"></PopularListing>
              </div>
            </div>
          </div>
        </div>
        <!-- Carousel Buttons -->
        <div v-if="listingsList2">
          <a class="carousel-control-prev" data-slide="prev" href="#popularListingCarousel" role="button" @click="currentSlide--">
            <span aria-hidden="true" class="carousel-control-prev-icon"></span>
            <span class="sr-only">Previous</span>
          </a>
          <a class="carousel-control-next" data-slide="next" href="#popularListingCarousel" role="button" @click="currentSlide++">
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
import $ from "jquery";

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
      currentSlide: 1,
      country: "",
      error: ""
    }
  },
  methods: {
    /**
     * Gets the popular listings from the backend, as well as setting each sale listing's currency
     */
    async getPopularListings(){
      this.listings = []
      this.listingsList1 = null;
      this.listingsList2 = null;
      this.listingsList3 = null;
      const response = await Landing.getPopularListings()
      for (let listing of response.data) {
        listing.currency = await this.$root.$data.product.getCurrency(listing.business.address.country)
        this.listings.push(listing)
      }
      if(this.listings.length > 6){
        //7-9 listings
        this.listingsList1 = this.listings.slice(0,3)
        this.listingsList2 = this.listings.slice(3,6)
        this.listingsList3 = this.listings.slice(6)
      } else if(this.listings.length > 3){
        //4-6 liked listings
        this.listingsList1 = this.listings.slice(0,3)
        this.listingsList2 = this.listings.slice(3)
      }else {
        //1-3
        this.listingsList1 = this.listings.slice(0)
      }
    },

    async updateData() {
      await this.getPopularListings()
      $(".carousel").carousel(this.currentSlide-1);
    }
  }
}
</script>

<style scoped>
</style>