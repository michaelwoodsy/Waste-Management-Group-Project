<template>
  <div class="modal-content">

    <div class="modal-header">
      <div class="col-2 text-left">
        <button v-if="showBack" class="btn btn-secondary" @click="$emit('back')">Back</button>
      </div>
      <div class="col text-center">
        <h2>{{ business.name }}</h2>
      </div>
      <div class="col-2 text-right">
        <button aria-label="Close" class="close" data-dismiss="modal" type="button" @click="$emit('close-modal')">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
    </div>

    <div class="modal-body">
      <business-profile :business="business"/>
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
  }
}
</script>

<style scoped>

</style>