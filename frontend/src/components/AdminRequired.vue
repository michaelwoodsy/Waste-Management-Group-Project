<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-12 text-center mb-2">
        <h4>Must be Administrator</h4>
      </div>
    </div>
    <!-- User is not logged in-->
    <div class="row">
      <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">
        <p class="text-muted">You must be an administrator to {{ page }}.</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "AdminRequired",
  props: ['page']
}
</script>

<style scoped>
</style>
