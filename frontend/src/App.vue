<template>
  <div id="app">

    <header class="mb-3">
      <Navbar/>
    </header>

    <body>
      <div class="container-fluid">
        <div class="row">
          <div class="d-none d-sm-block col-sm-1"></div>
          <div class="col-12 col-sm-10">
            <router-view/>
          </div>
          <div class="d-none d-sm-block col-sm-1"></div>
        </div>
      </div>
    </body>

    <footer class="text-center">
      <p class="text-muted">SENG302 Team 10</p>
    </footer>
  </div>
</template>

<script>
import Navbar from "./components/Navbar"
const app = {
  name: "app",
  components: {
    Navbar
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

</style>
