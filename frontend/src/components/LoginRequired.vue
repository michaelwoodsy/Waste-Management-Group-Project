<template>
  <page-wrapper>

    <div class="row">
      <div class="col-12 text-center mb-2">
        <h4>Login Required</h4>
      </div>
    </div>
    <!-- User is not logged in-->
    <div class="row justify-content-center">
      <div class="col-12 col-sm-8 col-lg-6 col-xl-4 text-center mb-2">
        <p class="text-muted">You must be logged in to {{ page }}.</p>
        <div class="form-row">
          <div class="form-group col-6">
            <!-- Register Button -->
            <router-link class="no-underline" to="/register">
              <button class="btn btn-block btn-secondary" type="button">Register</button>
            </router-link>
          </div>
          <div class="form-group col-6">
            <!-- Login Button -->
            <router-link class="no-underline" to="/login">
              <button class="btn btn-block btn-primary">Login</button>
            </router-link>
          </div>
        </div>
      </div>
    </div>

  </page-wrapper>
</template>

<script>
import PageWrapper from "@/components/PageWrapper";

export default {
  name: "LoginRequired",
  components: {PageWrapper},
  props: ['page']
}
</script>

<style scoped>
.no-underline {
  text-decoration: none;
}
</style>
