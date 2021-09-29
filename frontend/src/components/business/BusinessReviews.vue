<template>
  <div>
    <div class="row">
      <div class="col text-center">
        <h4>Business' Reviews</h4>
      </div>
    </div>

    <alert v-if="error">
      {{ error }}
    </alert>

    <div v-else class="text-center">

      <div v-if="reviews.length === 0">
        This business has not had any reviews.
      </div>

      <div v-else class="row row-cols-1 row-cols-md-2">
        <div v-for="review in reviews" :key="review.reviewId" class="col mb-5">
          <review :review="review"/>
        </div>
      </div>

      <pagination
          :current-page.sync="page"
          :items-per-page="10"
          :total-items="totalReviews"
          :scroll-to-top-on-change="false"
          class="mx-auto"
          @change-page="changePage"
      />

    </div>

    <hr/>

  </div>
</template>

<script>
import {Business} from "@/Api";
import Alert from "@/components/Alert";
import Review from "@/components/business/Review";
import Pagination from "@/components/Pagination";

export default {
  name: "BusinessReviews",
  components: {Pagination, Review, Alert},
  props: {
    businessId: Number
  },
  data() {
    return {
      reviews: [],
      totalReviews: 0,
      page: 1,
      error: null
    }
  },
  async mounted() {
    await this.getReviews()
  },
  methods: {
    /**
     * Method to get all reviews for a business
     */
    async getReviews() {
      try {
        const res = await Business.getReviews(this.businessId, this.page - 1)
        this.reviews = res.data.reviews
        this.totalReviews = res.data.totalReviews
      } catch (error) {
        this.error = error.message
      }
    },
    /**
     * Method which makes request to get new page of reviews
     * @param page page  number to change to
     */
    async changePage(page) {
      this.page = page;
      await this.getReviews()
    }
  }
}
</script>

<style scoped>

</style>