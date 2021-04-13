<template>
  <div>
    <alert
        class="alert-danger alert-fixed alert-dismissible fade show shadow"
        v-if="alert"
    >
      {{ alert }}
      <!-- Button to close alert -->
      <button type="button" class="close" data-dismiss="alert" aria-label="Close" @click="clear">
        <span aria-hidden="true">&times;</span>
      </button>
    </alert>

  </div>
</template>

<script>
import Alert from './Alert'
import {listen} from "../utils/globalAlerts"

export default {
  name: "Alerts",
  data () {
    return {
      alert: null
    }
  },
  components: {
    Alert
  },
  created () {
    // Create a listener for new alerts to display
    listen(this.pushAlert)
  },
  methods: {
    /**
     * Set the current alert message
     * @param message
     */
    pushAlert (message) {
      this.alert = message
    },

    /**
     * Clears the current alert
     */
    clear () {
      this.alert = null
    }
  }
}
</script>

<style scoped>
.alert-fixed {
  position:fixed;
  top: 50px;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  z-index:9999;
}
</style>