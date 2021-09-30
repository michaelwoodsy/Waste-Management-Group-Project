<template>
  <span class="text-muted">Showing {{ showingRange }} of {{ totalCount }}</span>
</template>

<script>
export default {
  name: "ShowingResultsText",
  props: [
    'totalCount',
    'itemsPerPage',
    'page'
  ],
  computed: {
    /**
     * Calculates the number of results being shown on current page
     * @returns {*|number}
     */
    showingCount() {
      const prevPagesCount = this.itemsPerPage * (this.page - 1);
      const futurePagesCount = this.totalCount - prevPagesCount;
      return (futurePagesCount > this.itemsPerPage ? this.itemsPerPage : futurePagesCount)
    },

    /**
     * Calculates the number of results in the previous page
     * @returns {number}
     */
    itemsInPrevPages() {
      return this.itemsPerPage * (this.page - 1)
    },

    /**
     * Calculates the range of results being shown
     * @returns {string}
     */
    showingRange() {
      return `${this.itemsInPrevPages + (this.totalCount > 0)} - ${this.itemsInPrevPages + this.showingCount}`
    }
  }
}
</script>

