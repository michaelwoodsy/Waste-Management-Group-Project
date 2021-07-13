<template>
  <div aria-atomic="true" aria-live="assertive" class="toast hide" data-autohide="false" role="alert">
    <div :class="{'bg-danger text-light': data.type === 'admin'}" class="toast-header">
      <strong class="mr-auto">{{ data.title }}</strong>
      <small>{{ formattedDateTime }}</small>
      <button id="closeNotification"
              aria-label="Close"
              class="ml-2 close"
              data-dismiss="toast"
              type="button"
              @click="$emit('remove-notification')">
        <span :class="{'text-light': data.type === 'admin'}" aria-hidden="true">&times;</span>
      </button>
    </div>
    <div class="toast-body text-black-50">
      {{ data.message }}
      <div v-if="data.type === 'cardExpiry'">
        <br>
        Card: {{ data.card.title }}
      </div>
    </div>
  </div>
</template>

<script>
import {formatDateTime} from "@/utils/dateTime";

export default {
  name: "Notification",
  props: {
    data: {
      type: Object,
      required: true
    }
  },
  computed: {
    formattedDateTime() {
      return formatDateTime(this.data.created)
    }
  }
}
</script>

<style scoped>

.toast {
  max-width: 100%;
}

</style>