<template>
    <nav class="navigation ">
        <ul class="pagination justify-content-center">
            <li v-for="page in pages"
                @click.prevent="changePage(page)"
                v-bind:key="page"
                :class="styles(page)"
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
            pages () {
                return [
                    ...Array(Math.ceil(this.totalItems / this.itemsPerPage)).keys()
                ].map(pageNo => pageNo + 1)
            }
        },
        methods: {
            changePage (page) {
                this.$emit('update:currentPage', page);
                this.scrollToTop()
            },
            styles (page) {
                return {
                    'page-item': true,
                    'active': page === this.currentPage
                }
            },
            scrollToTop () {
                window.scrollTo(0, 0)
            }
        }
    }
</script>

<style scoped>

</style>
