<template>
  <div :id="modalId" class="modal fade" role="dialog" tabindex="-1">
    <div class="modal-dialog" role="document">
      <div class="modal-content">

        <!-- Title section of modal -->
        <div class="modal-header">
          <h5 class="modal-title">{{ modalHeader }}</h5>
          <button aria-label="Close" class="close" data-dismiss="modal" type="button">
            <span ref="close" aria-hidden="true">&times;</span>
          </button>
        </div>

        <!-- Body section of modal -->
        <div class="modal-body">
          {{ modalMessage }}
        </div>

        <!-- Footer / button section of modal -->
        <div class="modal-footer">
          <button :class="modalConfirmColour" id="confirmButton" class="btn" data-dismiss="modal" type="button" @click="confirm">
            {{ modalConfirmText }}
          </button>
          <button class="btn btn-secondary" data-dismiss="modal" type="button">Cancel</button>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ConfirmationModal",
  props: {
    modalId: String,
    modalHeader: String,
    modalMessage: String,
    modalConfirmText: String,
    modalConfirmColour: String
  },
  methods: {
    /**
     * Emits the confirm event from the modal, so that the parent component can then perform the action
     */
    confirm() {
      this.$emit('confirm')
    }
  }
}
</script>

<style scoped>

</style>