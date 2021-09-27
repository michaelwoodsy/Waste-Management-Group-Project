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

    </div>

    <hr/>

  </div>
</template>

<script>
import {Business} from "@/Api";
import Alert from "@/components/Alert";
import Review from "@/components/business/Review";

export default {
  name: "BusinessReviews",
  components: {Review, Alert},
  props: {
    businessId: Number
  },
  data() {
    return {
      reviews: [],
      error: null
    }
  },
  async mounted() {
    await this.getReviews()
  },
  methods: {
    async getReviews() {
      try {
        const res = await Business.getReviews(this.businessId)
        this.reviews = res.data
      } catch (error) {
        this.error = error.message
      }
    }
  }
}
</script>

<style scoped>

</style>