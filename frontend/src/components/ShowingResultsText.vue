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
            // Number of pages
            pageCount () {
                return Math.ceil(this.totalCount / this.itemsPerPage)
            },
            // Number of results showing on current page
            showingCount () {
                const prevPagesCount = this.itemsPerPage * (this.page - 1);
                const futurePagesCount = this.totalCount - prevPagesCount;
                return (futurePagesCount > this.itemsPerPage ? this.itemsPerPage : futurePagesCount)
            },
            itemsInPrevPages () {
                return this.itemsPerPage * (this.page - 1)
            },
            showingRange () {
                return `${this.itemsInPrevPages + (this.totalCount > 0)} - ${this.itemsInPrevPages + this.showingCount}`
            }
        }
    }
</script>

<style scoped>

</style>
