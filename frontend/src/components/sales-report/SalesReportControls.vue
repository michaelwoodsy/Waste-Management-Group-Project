<template>
  <div class="mb-3">

    <div class="form-group row">
      <div class="col">
        <div class="input-group">
          <div class="input-group-prepend">
            <span class="input-group-text">Date Range</span>
          </div>
          <input id="startDateRange" class="form-control" type="date">
          <input id="endDateRange" class="form-control" type="date">
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col text-right">
        <button class="btn btn-primary">Generate Report</button>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "SalesReportControls",
  data() {
    return {
      startDate: null,
      endDate: null
    }
  }
}
</script>

<style scoped>

</style>