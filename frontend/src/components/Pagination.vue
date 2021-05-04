<template>
  <nav class="navigation ">
    <ul class="pagination justify-content-center">
      <li v-for="page in pages"
          v-bind:key="page"
          :class="styles(page)"
          @click.prevent="changePage(page)"
      >
        <a class="page-link no-outline" href>{{ page }}</a>
      </li>
    </ul>
  </nav>
</template>

<script>
export default {
  name: "Pagination",
  props: {
    currentPage: {
      type: Number,
      required: true
    },
    totalItems: {
      type: Number,
      required: true
    },
    itemsPerPage: {
      type: Number,
      required: true
    }
  },

  computed: {
    // An array of page numbers
    /**
     *Creates an array for the correct number of pages needed
     */
    pages() {
      return [
        ...Array(Math.ceil(this.totalItems / this.itemsPerPage)).keys()
      ].map(pageNo => pageNo + 1)
    }
  },

  methods: {
    /**
     * Lets user to change page
     * @param page
     */
    changePage(page) {
      this.$emit('update:currentPage', page);
      this.scrollToTop()
    },

    /**
     * Styles the active page
     */
    styles(page) {
      return {
        'page-item': true,
        'active': page === this.currentPage
      }
    },

    /**
     * Lets user to scroll to top of window
     */
    scrollToTop() {
      window.scrollTo(0, 0)
    }
  }
}
</script>

<style scoped>

</style>
