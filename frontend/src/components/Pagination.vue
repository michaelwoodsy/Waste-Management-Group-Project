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
    <ul class="pagination justify-content-center">

      <li :class="{'disabled': currentPage === 1}" class="page-item">
        <a class="page-link no-outline" href @click.prevent="changePage(1)"><em class="bi bi-chevron-double-left"/></a>
      </li>

      <li :class="{'disabled': currentPage === 1}" class="page-item">
        <a class="page-link no-outline" href @click.prevent="changePage(currentPage - 1)"><em
            class="bi bi-chevron-left"/></a>
      </li>

      <!-- Three dots -->
      <li v-if="!inLeftRange" @click.prevent>
        <a class="page-link no-outline disabled disabled-page-link" href><em class="bi bi-three-dots"/></a>
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
      <li v-if="!inRightRange" @click.prevent>
        <a class="page-link no-outline disabled disabled-page-link" href><em class="bi bi-three-dots"/></a>
      </li>

      <li :class="{'disabled': currentPage === finalPage}" class="page-item">
        <a class="page-link no-outline" href @click.prevent="changePage(currentPage + 1)"><em
            class="bi bi-chevron-right"/></a>
      </li>

      <li :class="{'disabled': currentPage === finalPage}" class="page-item">
        <a class="page-link no-outline" href @click.prevent="changePage(finalPage)"><em
            class="bi bi-chevron-double-right"/></a>
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
        return this.pages.slice(0, (this.range * 2) + 1)
      } else if (this.inRightRange) {
        return this.pages.slice(-((this.range * 2) + 1))
      } else {
        return this.pages.slice((this.currentPage - this.range) - 1, this.currentPage + this.range)
      }

    },

    /**
     * The final page number
     */
    finalPage() {
      if (this.pages.length === 0) {
        return 1
      } else {
        return this.pages[this.pages.length - 1]
      }
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
      this.$emit('change-page', page)
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
