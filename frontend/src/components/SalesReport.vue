<template>
  <div class="accordion" id="salesReport">

    <div v-for="section in sections" :key="section.index" class="card">
      <div class="card-header" :id="`salesReportHeading${section.index}`">
        <div class="row align-items-center">
            <div class="col-4">
              <span class="text-muted">
                {{ formattedDate(section.dateStart) }} - {{ formattedDate(section.dateEnd) }}
              </span>
            </div>
            <div class="col">
              <strong>Total Income:</strong> {{ section.total }}
            </div>
            <div class="col">
              <strong>Total Sales:</strong> {{ section.totalSales }}
            </div>
            <div class="col text-right">
              <button class="btn btn-secondary btn-sm"
                      data-toggle="collapse" :data-target="`#salesReportSection${section.index}`">
                View Sales
              </button>
            </div>
        </div>
      </div>
      <div class="collapse" :id="`salesReportSection${section.index}`" data-parent="#salesReport">
        <div class="card-body">
          <!-- TODO add SalesSection table here -->
          <p>Table will go here</p>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "SalesReport",
  data() {
    return {
      report: [
        {
          dateStart: '2021-06-01',
          dateEnd: '2021-06-30',
          total: 50.00,
          totalSales: 4,
          sales: []
        },
        {
          dateStart: '2021-07-01',
          dateEnd: '2021-07-31',
          total: 40.00,
          totalSales: 5,
          sales: []
        }
      ]
    }
  },
  computed: {
    sections() {
      const sects = []
      for (const [index, section] of this.report.entries()) {
        section.index = index
        sects.push(section)
      }
      return sects
    }
  },
  methods: {
    /**
     * Formats a date string for displaying
     *
     * @param date date string to format
     * @returns {string} formatted date string
     */
    formattedDate(date) {
      return new Date(date).toDateString()
    }
  }
}
</script>

<style scoped>

</style>