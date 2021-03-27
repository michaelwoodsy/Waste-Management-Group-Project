<template>
  <div>

<!--    <login-required
        page="view a users profile page"
        v-if="!isLoggedIn"
    />-->

<!--    <div v-else>-->
    <div>
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h2>Business Name</h2>
        </div>
      </div>


      <!-- Name of Business -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Name of Business: </p>
        </div>
        <div class="col-6">
          <p>[Business name goes here]</p>
        </div>
      </div>

      <!-- Description -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Description: </p>
        </div>
        <div class="col-6">
          <p>[Business description goes here]</p>
        </div>
      </div>

      <!-- Address-->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Address: </p>
        </div>
        <div class="col-6">
          <p>[Business address goes here]</p>
        </div>
      </div>

      <!-- Type -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Type: </p>
        </div>
        <div class="col-6">
          <p>[Business type goes here]</p>
        </div>
      </div>

      <!-- Date of Registration -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Date of Registration: </p>
        </div>
        <div class="col-6">
          <p>[e.g. "Registered since: 2 April 2020 (10 months)"]</p>
        </div>
      </div>

      <!-- Administrators -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold">
          <p>Administrators: </p>
        </div>
        <div class="col-6">
          <p>[Links to admin profile pages here]</p>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "BusinessProfilePage"
}
</script>

<style scoped>

</style>