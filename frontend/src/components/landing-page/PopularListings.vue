<template>
  <div class="row">
    <h2>Popular Listings</h2>
    <div>
      <div id="popularListingCarousel" class="carousel slide w-50 col" data-ride="carousel">
        <div class="carousel-inner" role="listbox">
          <div v-for="(listing, index) in listings" v-bind:key="listing.id"
               :class="{'carousel-item': true, 'active': index === 0}">
            <PopularListing :data="listing" @update-data="updateData"></PopularListing>
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
import $ from "jquery";
import userState from "@/store/modules/user"
import PopularListing from "../sale-listing/PopularListing";

export default {
  name: "PopularListings",
  components: {
    PopularListing
  },
  async mounted() {
    await this.getPopularListings()

    $('#popularListingCarousel').carousel({
      interval: 10000
    })

    $('.carousel .carousel-item').each(function(){
      var minPerSlide = 3;
      var next = $(this).next();
      if (!next.length) {
        next = $(this).siblings(':first');
      }
      next.children(':first-child').clone().appendTo($(this));

      for (var i=0;i<minPerSlide;i++) {
        next=next.next();
        if (!next.length) {
          next = $(this).siblings(':first');
        }

        next.children(':first-child').clone().appendTo($(this));
      }
    });
  },
  data() {
    return {
      user: userState,
      listings: [],
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
    },


    async updateData() {
      await this.user.updateData()
    }
  }
}
</script>

<style scoped>
/* display 2 */
@media (max-width: 768px) {
  .carousel-inner .carousel-item > div {
    display: none;
  }
  .carousel-inner .carousel-item > div:first-child {
    display: block;
  }
}

.carousel-inner .carousel-item.active,
.carousel-inner .carousel-item-next,
.carousel-inner .carousel-item-prev {
  display: flex;
}

/* display 3 */
@media (min-width: 768px) {

  .carousel-inner .carousel-item-right.active,
  .carousel-inner .carousel-item-next {
    transform: translateX(33.333%);
  }

  .carousel-inner .carousel-item-left.active,
  .carousel-inner .carousel-item-prev {
    transform: translateX(-33.333%);
  }
}

.carousel-inner .carousel-item-right,
.carousel-inner .carousel-item-left{
  transform: translateX(0);
}
</style>