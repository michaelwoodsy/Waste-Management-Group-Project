<template>
  <component :is="routedComponent"></component>
</template>

<script>
import {listen} from './history'
import {findRoute} from "./routeParser";
import Default from "../../components/Default";

export default {
  name: "Router",
  data() {
    return {
      current: window.location.pathname + window.location.search
    }
  },
  created() {
    listen((route) => {
      route = this.getRoute(route);
      if (route) {
        this.current = route.path
      } else {
        this.current = Default.path
      }
    });
    window.addEventListener(
        'popstate',
        () => {
          this.current = window.location.pathname + window.location.search
        })
  },
  computed: {
    routedComponent() {
      // Finds the component based on routes
      const route = this.getRoute(this.current);
      if (route) {
        return route.component
      }
      return Default
    }
  },
  methods: {
    getRoute(routeInfo) {
      return findRoute(this.$routes, routeInfo)
    }
  }
}
</script>

