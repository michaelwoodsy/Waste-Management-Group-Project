<!--
Component for displaying pagination links.

-----------------------------------------------------------
Props
-----------------------------------------------------------
currentPage:    The page the user is currently looking at.
totalItems:     The total number of items being displayed.
itemsPerPage:   The number of items displayed on each page.

-----------------------------------------------------------
Events
-----------------------------------------------------------
"update:currentPage":   Emitted when a page number is clicked,
                        the payload contains the page number
                        clicked.

-----------------------------------------------------------
Example Usage
-----------------------------------------------------------
<pagination
    :current-page.sync="page"
    :items-per-page="resultsPerPage"
    :total-items="totalCount"
    class="mx-auto"
/>

(note, current-page.sync automatically updates the 'page' variable when
the "update:currentPage" event is emitted)
-->
<template>
  <nav class="navigation ">
    <!-- Pages if it is possible to show all page numbers -->
    <ul class="pagination justify-content-center" v-if="allPagesFit">
      <li v-for="page in pages"
          v-bind:key="page"
          :class="styles(page)"
          @click.prevent="changePage(page)"
      >
        <a class="page-link no-outline" href>{{ page }}</a>
      </li>
    </ul>

    <ul class="pagination justify-content-center" v-else-if="true">
      <!-- first page -->
      <li :class="styles(1)" @click.prevent="changePage(1)">
        <a class="page-link no-outline mb-0 disabled" href>{{ 1 }}</a>
      </li>

      <!-- Three dots -->
      <li
          @click.prevent
          v-if="!inLeftRange"
      >
        <a class="page-link no-outline mb-0 disabled disabled-page-link" href>...</a>
      </li>

      <!-- Pages in range of current page -->
      <li v-for="page in shownPages"
          v-bind:key="page"
          :class="styles(page)"
          @click.prevent="changePage(page)"
      >
        <a class="page-link no-outline" href>{{ page }}</a>
      </li>

      <!-- Three dots -->
      <li @click.prevent
          v-if="!inRightRange"
      >
        <a class="page-link no-outline mb-0 disabled disabled-page-link" href>...</a>
      </li>

      <!-- Final page -->
      <li :class="styles(finalPage)" @click.prevent="changePage(finalPage)">
        <a class="page-link no-outline mb-0 disabled" href>{{ finalPage }}</a>
      </li>
    </ul>
  </nav>
</template>

<script>
export default {
  name: "Pagination",
  data() {
    return {
      range: 2  // Range of pages left and right of current page to show
    }
  },
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
    /**
     *Creates an array for the correct number of pages needed
     */
    pages() {
      return [
        ...Array(Math.ceil(this.totalItems / this.itemsPerPage)).keys()
      ].map(pageNo => pageNo + 1)
    },

    /**
     * Array of page numbers withing the range of current page
     */
    shownPages() {
      if (this.inLeftRange) {
        return this.pages.slice(1, (this.range * 2) + 1)
      }
      else if (this.inRightRange) {
        return this.pages.slice(-((this.range * 2) + 1), -1)
      }
      else {
        return this.pages.slice(this.currentPage - this.range - 1, this.currentPage + this.range)
      }

    },

    /**
     * The final page number
     */
    finalPage() {
      return this.pages[this.pages.length - 1]
    },

    /**
     * Boolean, true if all possible pages with within the pagination bar
     */
    allPagesFit() {
      return this.pages.length <= (this.range * 2) + 1
    },

    /**
     * Boolean, true if current page is within the left range of the pagination bar
     */
    inLeftRange() {
      return this.range + 1 >= this.currentPage
    },

    /**
     * Boolean, true if current page is within the right range of the pagination bar
     */
    inRightRange() {
      return (this.range) >= (this.finalPage - this.currentPage)
    }
  },

  methods: {
    /**
     * Lets user to change page
     * @param page
     */
    changePage(page) {
      this.$emit('update:currentPage', page);
      this.$parent.$parent.changePage()
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
.disabled-page-link {
  cursor: default;
}

.disabled-page-link:hover {
  background-color: white;
  color: #42b983;
}
</style>
