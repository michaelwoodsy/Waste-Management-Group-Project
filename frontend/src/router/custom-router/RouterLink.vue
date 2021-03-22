<template>
    <a @click="go" class="pointer">
        <slot/>
    </a>
</template>

<script>
    export default {
        name: "RouterLink",
        props: {
            to: [String, Object]
        },
        methods: {
            go () {
                this.$router.push(this.to)
            }
        }
    }
</script>

<style scoped>

</style>
