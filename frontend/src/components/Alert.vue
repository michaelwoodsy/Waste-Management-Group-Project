<template>
  <div :class="alertStyles" role="alert">
    <slot/>
  </div>
</template>

<script>
export default {
  name: "Alert",
  props: {
    'alertType': {
      type: String,
      default: function () {
        return 'alert-danger'
      }
    }
  },

  computed: {
    /**
     * Returns the alert that just occurred
     * @returns {string}
     */
    alertStyles() {
      return `alert ${this.alertType}`
    }
  }
}
</script>

<style scoped>

</style>
