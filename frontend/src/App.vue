<template>
  <div id="app">

    <header class="mb-3">
      <div class="admin-bar" v-if="this.$root.$data.user.canDoAdminAction()"></div>
      <Navbar/>
    </header>

    <!-- Global alert popups -->
    <alerts/>

    <body>
    <div class="container-fluid">
      <div class="row justify-content-center">
        <div class="col-12 col-sm-10">
          <router-view/>
        </div>
      </div>
    </div>
    </body>

    <footer class="text-center">
      <p class="text-muted">SENG302 Team 200 - re: sale</p>
    </footer>
  </div>
</template>

<script>
import Navbar from "./components/Navbar"
import Alerts from "./components/Alerts"

const app = {
  name: "app",
  components: {
    Navbar,
    Alerts
  },
  computed: {
    /**
     * Called to see if user being acted as is a GAA or DGAA
     * @returns {boolean|*}
     */
    isGAA() {
      return this.$root.$data.user.isGAA()
    }
  }
};

// make the 'app' available
export default app;
</script>

<style>
[v-cloak] {
  display: none;
}

body {
  min-height: 500px;
}

.admin-bar {
  height: 10px;
  background-color: red;
  width: 100%;
}

</style>
