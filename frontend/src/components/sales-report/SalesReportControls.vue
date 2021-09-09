<template>
  <div>

    <div class="row mb-5 justify-content-center">
      <div class="col-8">

        <div class="form-group">
          <div class="input-group" :class="{'is-invalid': msg.dateRange}">
            <div class="input-group-prepend">
              <span class="input-group-text">Date Range</span>
            </div>
            <input id="startDateRange" v-model="options.startDate"
                   :class="{'is-invalid': msg.dateRange}"
                   class="form-control" type="date">
            <input id="endDateRange" v-model="options.endDate"
                   :class="{'is-invalid': msg.dateRange}"
                   class="form-control" type="date">
          </div>
          <span class="invalid-feedback text-center">{{ msg.dateRange }}</span>
        </div>

        <div class="form-group">
          <div class="input-group">
            <div class="input-group-prepend">
              <span class="input-group-text">Granularity</span>
            </div>
            <select v-model="options.granularity" class="custom-select">
              <option v-for="option of granularityOptions" :key="option" value="option">
                {{ option }}
              </option>
            </select>
            <div class="input-group-append">
              <button class="btn btn-primary" @click="validateInputs">Generate Report</button>
            </div>
          </div>
        </div>

      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "SalesReportControls",
  data() {
    return {
      options: {
        startDate: null,
        endDate: null,
        granularity: 'Total'
      },
      granularityOptions: [
        'Total',
        'Monthly',
        'Weekly',
      ],
      valid: true,
      msg: {
        dateRange: null
      }
    }
  },
  methods: {
    /**
     * Validates the date range for the sales report
     */
    validateDateRange() {
      if (this.options.startDate == null || this.options.endDate == null) {
        this.valid = false
        this.msg.dateRange = 'Please enter a date range'
      } else {
        const startDate = new Date(this.options.startDate)
        const endDate = new Date(this.options.endDate)
        const currentDate = new Date()
        if (currentDate < startDate || currentDate < endDate) {
          this.valid = false
          this.msg.dateRange = 'Date range must be in the past'
        } else {
          this.msg.dateRange = null
        }
      }
    },

    /**
     * Validates inputs and then emits event to generate report
     */
    validateInputs() {
      this.validateDateRange()
      if (this.valid) {
        this.$emit('generate-report', this.options)
      } else {
        this.valid = true
      }
    }
  }
}
</script>

<style scoped>

</style>