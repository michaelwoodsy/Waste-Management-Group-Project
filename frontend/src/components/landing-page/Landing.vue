<template>
  <page-wrapper col-size="9">

    <div class="container-fluid">
      <div class="row justify-content-center" style="justify-content: center; display: flex">
        <img
            alt="logo"
            width="400"
            src="../../../public/logoInverted.png">
      </div>

      <div class="row justify-content-center mb-3 align-content-center round-corners">
        <featured-listings></featured-listings>
      </div>

      <div class="row justify-content-center mb-3 align-content-center round-corners">
        <statistics></statistics>
      </div>

      <div class="row justify-content-center mb-3 align-content-center round-corners">
        <about-us></about-us>
      </div>

      <div class="row justify-content-center mb-3 align-content-center round-corners">
        <contact-register-login></contact-register-login>
      </div>
    </div>

  </page-wrapper>
</template>

<script>
import PageWrapper from "@/components/PageWrapper";
import FeaturedListings from "@/components/landing-page/FeaturedListings";
import AboutUs from "@/components/landing-page/AboutUs";
import Statistics from "@/components/landing-page/Statistics";
import ContactRegisterLogin from "@/components/landing-page/ContactRegisterLogin";

export default {
  name: "Landing",
  components: {ContactRegisterLogin, Statistics, AboutUs, FeaturedListings, PageWrapper},

}
</script>

<style scoped>

.round-corners {
  border-radius: 15px;
  border: 1px solid #42b983;
  padding: 20px;
}

</style>
