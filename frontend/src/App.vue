<template>

  <div id="app">

    <div class="admin-bar bg-danger" v-if="this.$root.$data.user.canDoAdminAction()"></div>
    <Navbar/>

    <!-- Global alert popups -->
    <alerts/>

    <router-view/>

    <footer class="text-center fixed-bottom">
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
  width: 100%;
}

</style>


