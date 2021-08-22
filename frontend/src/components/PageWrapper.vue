<template>
  <div class="container-fluid mt-3">
    <div class="row justify-content-center">
      <div :class="`col-12 col-sm-${colSize}`">

        <slot></slot>

      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "PageWrapper",
  props: {
    colSize: {
      type: String,
      required: false,
      default: "12"
    }
  }
}
</script>

<style scoped>

</style>