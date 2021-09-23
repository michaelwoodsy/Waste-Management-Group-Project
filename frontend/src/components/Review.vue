<template>
  <div id="reviewModal" class="modal fade" role="dialog" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <div class="col-2"/>
          <div class="col text-center">
            <h4 class="mb-2">Sale Review</h4>
          </div>
          <div class="col-2">
            <button aria-label="Close" class="close" data-dismiss="modal" type="button">
              <span ref="close" aria-hidden="true">&times;</span>
            </button>
          </div>
        </div>
        <div class="modal-body">
          <alert v-if="error">
            {{ error }}
          </alert>
          <!-- Form to leave a review -->
          <div v-if="!sale.review" id="reviewForm">
            <h5>Leave a review</h5>
            <div class="text-center">
              <em v-for="num in reviewRange" :key="num"
                  :class="{'bi-star-fill': reviewForm.rating >= num, 'bi-star': reviewForm.rating < num}"
                  class="icon bi pointer mx-1"
                  @click="reviewForm.rating = num"
              />
              <span v-if="invalidRating" class="text-danger small"><br>Please enter a rating from 1 to 5</span>
            </div>
            <hr/>
            <div class="form-group row">
              <div class="col">
                <label for="review">Message</label>
                <textarea id="review" v-model="reviewForm.reviewMessage" class="form-control"/>
              </div>
            </div>
            <div class="text-right">
              <button class="btn btn-primary" @click="checkInputs">Leave Review</button>
            </div>
          </div>

          <!-- Review Section -->
          <div v-else id="reviewSection">
            <div class="text-center">
              <em v-for="num in reviewRange" :key="num"
                  :class="{'bi-star-fill': sale.review.rating >= num, 'bi-star': sale.review.rating < num}"
                  class="icon-static bi mx-1"
                  style="color: gold"
              />
            </div>
            <hr v-if="sale.review.reviewMessage"/>
            <p>{{ sale.review.reviewMessage }}</p>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {User} from '@/Api'
import userState from '@/store/modules/user'
import Alert from "@/components/Alert";
import $ from 'jquery'

export default {
  name: "Review",
  components: {Alert},
  props: {
    sale: Object
  },
  data() {
    return {
      reviewRange: [1, 2, 3, 4, 5],
      reviewForm: {
        rating: 0,
        reviewMessage: null
      },
      invalidRating: false,
      error: null
    }
  },
  mounted() {
    $('#reviewModal').on('hidden.bs.modal', () => {
      this.reviewForm.rating = 0
      this.reviewForm.reviewMessage = null
      this.$emit('close-modal')
    })
  },
  methods: {
    /**
     * Checks that a valid rating has been given
     */
    async checkInputs() {
      this.invalidRating = this.reviewForm.rating === 0;
      if (!this.invalidRating) {
        await this.leaveReview()
      }
    },
    /**
     * Sends request to leave a review on a sale
     */
    async leaveReview() {
      try {
        await User.leaveReview(userState.actor().id, this.sale.id, this.reviewForm)
        this.$emit('update-data')
      } catch (error) {
        this.error = error.message
      }
    }
  }
}
</script>

<style scoped>

.icon {
  color: gold;
  font-size: 30px;
  transition: 0.3s;
}

.icon:hover {
  text-shadow: currentColor 0 0 5px;
}

.icon-static {
  color: gold;
  font-size: 30px;
}

</style>