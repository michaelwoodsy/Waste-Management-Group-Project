<template>
  <page-wrapper>

    <div class="row">
      <div class="col-12 text-center mb-2">
        <h4>Must be an Administrator and acting as a Business</h4>
      </div>
    </div>
    <!-- User is not logged in-->
    <div class="row">
      <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">
        <p class="text-muted">You must be an Administrator and acting as a Business to {{ page }}.</p>
      </div>
    </div>

  </page-wrapper>
</template>

<script>
import PageWrapper from "@/components/PageWrapper";

export default {
  name: "AdminRequired",
  components: {PageWrapper},
  props: ['page']
}
</script>

