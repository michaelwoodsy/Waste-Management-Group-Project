<template>
  <page-wrapper col-size="9">

    <div class="container-fluid">
      <div class="row justify-content-center" style="justify-content: center; display: flex">
        <img
            alt="logo"
            width="400"
            src="../../../public/logoInverted.png">
      </div>

      <div class="row justify-content-center mb-3 align-content-center" style="border: 1px solid black; height: 100px">
        <featured-listings></featured-listings>
      </div>

      <div class="row justify-content-center mb-3 align-content-center" style="border: 1px solid black; height: 100px">
        <statistics></statistics>
      </div>

      <div class="row justify-content-center mb-3 align-content-center" style="border: 1px solid black; height: 100px">
        <about-us></about-us>
      </div>

      <contact-register-login></contact-register-login>
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

</style>
