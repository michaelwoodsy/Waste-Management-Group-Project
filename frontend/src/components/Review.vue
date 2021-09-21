<template>
  <div :id="reviewId" class="modal fade" role="dialog" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="mb-0">Leave a review</h3>
        </div>
        <div class="modal-body">
          <div class="text-center">
            <em v-for="num in reviewRange" :key="num"
                :class="{'bi-star-fill': reviewForm.stars >= num, 'bi-star': reviewForm.stars < num}"
                class="icon bi pointer mx-1"
                style="color: gold"
                @click="reviewForm.stars = num"
            />
          </div>
          <div class="form-group row">

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Review",
  props: {
    reviewId: String
  },
  data() {
    return {
      reviewRange: [1, 2, 3, 4, 5],
      reviewForm: {
        stars: 0,
        message: null
      }
    }
  }
}
</script>

<style scoped>

.icon {
  font-size: 30px;
  transition: 0.3s;
}

.icon:hover {
  text-shadow: currentColor 0 0 5px;
}

</style>