<template>
  <div class="card shadow">

    <div class="card-header">
      {{ review.user.firstName }} {{ review.user.lastName }}
    </div>

    <div class="card-body">



    </div>

  </div>
</template>

<script>
export default {
  name: "Review",
  props: {
    review: Object
  },
  data() {
    return {
      profileImage: null
    }
  }
}
</script>

<style scoped>

</style>