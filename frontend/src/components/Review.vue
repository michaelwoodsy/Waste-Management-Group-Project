<template>
  <div :id="saleId" class="modal fade" role="dialog" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="mb-0">Leave a review</h3>
          <button aria-label="Close" class="close" data-dismiss="modal" type="button">
            <span ref="close" aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="review == null">
            <div class="text-center">
              <em v-for="num in reviewRange" :key="num"
                  :class="{'bi-star-fill': reviewForm.rating >= num, 'bi-star': reviewForm.rating < num}"
                  class="icon bi pointer mx-1"
                  style="color: gold"
                  @click="reviewForm.rating = num"
              />
            </div>
            <div class="form-group row">
              <div class="col">
                <label for="review">Message</label>
                <textarea id="review" class="form-control"/>
              </div>
            </div>
            <div class="text-right">
              <button class="btn btn-primary">Leave Review</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {User} from '@/Api'
import {userState} from '@/store/modules/user'

export default {
  name: "Review",
  props: {
    saleId: String,
    review: Object
  },
  data() {
    return {
      reviewRange: [1, 2, 3, 4, 5],
      reviewForm: {
        rating: 0,
        reviewMessage: null
      }
    }
  },
  methods: {
    /**
     * Sends request to leave a review on a sale
     */
    async leaveReview() {
      try {
        await User.leaveReview(userState.actor().id, this.saleId, this.reviewForm)
      } catch (error) {
        console.log(error) // TODO: Handle error
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