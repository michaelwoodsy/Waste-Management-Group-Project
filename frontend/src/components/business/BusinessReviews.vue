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

    <div class="text-center" v-else>

      <div v-if="reviews.length === 0">
        This business has not had any reviews.
      </div>

      <div v-else>
        There are some reviews but I don't know what to do with them yet :(
      </div>

    </div>

    <hr/>

  </div>
</template>

<script>
import {Business} from "@/Api";
import Alert from "@/components/Alert";

export default {
  name: "BusinessReviews",
  components: {Alert},
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
        this.error = error
      }
    }
  }
}
</script>

<style scoped>

</style>