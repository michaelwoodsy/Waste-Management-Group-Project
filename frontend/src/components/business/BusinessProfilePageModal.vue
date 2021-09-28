<template>
  <div class="modal-content">

    <div class="modal-header">
      <div class="col-2 text-left">
        <button v-if="showBack" class="btn btn-secondary" @click="$emit('back')">Back</button>
      </div>
      <div class="col text-center">
        <h2 class="mb-0">{{ business.name }}</h2>
      </div>
      <div class="col-2 text-right">
        <button id="closeModalButton" aria-label="Close" class="close" data-dismiss="modal" type="button" @click="$emit('close-modal')">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
    </div>

    <div class="modal-body">
      <business-profile @close-modal="closeModal" :business="business"/>
    </div>

  </div>
</template>

<script>
import BusinessProfile from "@/components/business/BusinessProfile";

export default {
  name: "BusinessProfilePageModal",
  components: {BusinessProfile},
  props: {
    business: {
      type: Object,
      required: true
    },
    showBack: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  methods: {
    /**
     * Closes the business profile modal by simulating a click on the close button.
     */
    closeModal() {
      document.getElementById("closeModalButton").click()
    }
  }
}
</script>

<style scoped>

</style>