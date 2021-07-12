<template>
  <div class="toast hide" role="alert" aria-live="assertive" aria-atomic="true" data-autohide="false">
    <div class="toast-header">
      <strong class="mr-auto">{{data.title}}</strong>
      <small>{{formatDateTime(data.created)}}</small>
      <button type="button"
              id="closeNotification"
              class="ml-2 close"
              data-dismiss="toast"
              aria-label="Close"
              @click="$emit('remove-notification')">
        <span aria-hidden="true">&times;</span>
      </button>
    </div>
    <div class="toast-body text-black-50">
      {{data.message}}
      <br>
      Card: {{data.card.title}}
    </div>
  </div>
</template>

<script>
export default {
  name: "Notification",
  props: {
    data: {
      type: Object,
      required: true
    }
  },
  methods: {
    /**
     * Takes a datetime in ISO format and formats it like dd/mm/yyy, hh:mm:ss PM
     */
    formatDateTime(dateTime) {
      let date = new Date(dateTime)
      let year = date.getFullYear()
      let month = date.getMonth()+1
      let day = date.getDate()

      let hours = date.getHours()
      let minutes = date.getMinutes()

      if (day < 10)     day = '0' + day;
      if (month < 10)   month = '0' + month;
      if (hours < 10)   hours = '0' + hours;
      if (minutes < 10) minutes = '0' + minutes;

      return `${day}/${month}/${year} ${hours}:${minutes}`
    }
  }
}
</script>

<style scoped>

.toast {
  max-width: 100%;
}

</style>