<template>
  <page-wrapper col-size="9">

    <div class="container-fluid">
      <div class="row justify-content-center" style="justify-content: center; display: flex">
        <img
            alt="logo"
            src="../../../public/logoInverted.png"
            width="40%">
      </div>

      <popular-listings></popular-listings>

      <statistics/>

      <about-us/>

      <contact-register-login/>

    </div>

  </page-wrapper>
</template>

<script>
import PageWrapper from "@/components/PageWrapper";
import PopularListings from "@/components/landing-page/PopularListings";
import AboutUs from "@/components/landing-page/AboutUs";
import Statistics from "@/components/landing-page/Statistics";
import ContactRegisterLogin from "@/components/landing-page/ContactRegisterLogin";

export default {
  name: "Landing",
  components: {ContactRegisterLogin, Statistics, AboutUs, PopularListings, PageWrapper},

}
</script>

