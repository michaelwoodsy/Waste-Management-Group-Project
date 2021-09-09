<template>
  <div>

    <div class="row mb-5 justify-content-center">
      <div class="col-8">

        <div class="form-group row">
          <div class="input-group" :class="{'is-invalid': msg.dateRange}">
            <div class="input-group-prepend">
              <span class="input-group-text">Date Range</span>
            </div>
            <input id="startDateRange" v-model="options.periodStart"
                   :class="{'is-invalid': msg.dateRange}"
                   class="form-control" type="date">
            <input id="endDateRange" v-model="options.periodEnd"
                   :class="{'is-invalid': msg.dateRange}"
                   class="form-control" type="date">
          </div>
          <span class="invalid-feedback text-center">{{ msg.dateRange }}</span>
        </div>

        <div class="form-group row">
          <div class="input-group">
            <div class="input-group-prepend">
              <span class="input-group-text">Granularity</span>
            </div>
            <select v-model="options.granularity" class="custom-select" ref="select">
              <option v-for="option of granularityOptions" :key="option" :value="option.toLowerCase()">
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
import {formatDate} from "@/utils/dateTime";

export default {
  name: "SalesReportControls",
  data() {
    return {
      options: {
        periodStart: null,
        periodEnd: null,
        granularity: 'all'
      },
      granularityOptions: [
        'All',
        'Yearly',
        'Monthly',
        'Weekly',
      ],
      valid: true,
      msg: {
        dateRange: null
      }
    }
  },
  mounted() {
    const previousYear = new Date()
    previousYear.setFullYear(previousYear.getFullYear() - 1)
    const now = new Date()
    this.options.periodStart = formatDate(previousYear)
    this.options.periodEnd = formatDate(now)
  },
  methods: {
    /**
     * Validates the date range for the sales report
     */
    validateDateRange() {
      if (this.options.periodStart == null || this.options.periodEnd == null) {
        this.valid = false
        this.msg.dateRange = 'Please enter a date range'
      } else {
        const startDate = new Date(this.options.periodStart)
        startDate.setHours(0, 0, 0, 0)
        const endDate = new Date(this.options.periodEnd)
        endDate.setHours(0, 0, 0, 0)
        const currentDate = new Date()
        if (startDate > currentDate || endDate > currentDate) {
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